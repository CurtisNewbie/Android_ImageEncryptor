package com.curtisnewbie.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES30;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.curtisnewbie.database.Image;
import com.curtisnewbie.services.App;
import com.curtisnewbie.services.AuthService;
import com.curtisnewbie.services.ExecService;
import com.curtisnewbie.util.CryptoUtil;
import com.curtisnewbie.util.IOUtil;
import com.curtisnewbie.util.ImageUtil;
import com.curtisnewbie.database.AppDatabase;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * {@code Activity} that shows the image that is selected from the
 * {@code ImageListActivity}
 * </p>
 */
public class ImageViewActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Inject
    protected AppDatabase db;
    @Inject
    protected AuthService authService;
    @Inject
    protected ExecService es;
    @Inject
    protected AppLifeCycleManager lifeCycleManager;

    private PhotoView photoView;
    private String imageName;
    private byte[] decryptedData = null;
    private boolean waitingPermissionResult = false;
    private boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        if (!authService.isAuthenticated()) {
            lifeCycleManager.restart();
            return;
        }

        hideActionBar();
        photoView = this.findViewById(R.id.photoView);
        photoView.setMaximumScale(20.0f);
        imageName = getIntent().getStringExtra(ImageListAdapter.IMG_NAME);
        decryptNDisplay();
        regPhotoViewListener();
    }

    private void hideActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.hide();
    }

    /**
     * Register listener for {@code photoView} by which the long click creates a dialog asking
     * whether the user wants to recover the image.
     */
    private void regPhotoViewListener() {
        photoView.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.recover_dialog_title)
                    .setPositiveButton(R.string.positiveBtnTxt, (dia, id) -> {
                        es.submit(() -> {
                            requestPermission();
                            while (waitingPermissionResult == true)
                                ; // block, wait for user permission
                            if (!permissionGranted) {
                                MsgToaster.msgShort(this, R.string.permission_denied_to_recover_msg);
                                return;
                            }
                            Image img = this.db.imgDao().getImage(imageName);
                            int msg;
                            if (img != null && recoverImage(img))
                                msg = R.string.image_recovered_msg;
                            else
                                msg = R.string.image_not_recovered_msg;
                            MsgToaster.msgShort(this, msg);
                        });
                    })
                    .setNegativeButton(R.string.negativeBtnTxt, (dia, id) -> {
                        // do nothing
                    });
            AlertDialog dia = builder.create();
            dia.show();
            return true;
        });
    }

    /**
     * Decrypt and display the selected image on a separate thread
     */
    private void decryptNDisplay() {
        // get the path to the decrypted image, decrypt data and display
        es.submit(() -> {
            try {
                // read encrypted data
                String imgPath = db.imgDao().getImagePath(imageName);
                byte[] encryptedData = IOUtil.read(new File(imgPath));

                // decrypt the data
                decryptedData = CryptoUtil.decrypt(encryptedData, authService.getImgKey());

                // get the allowed maximum size of texture in OpenGL ES3.0
                int[] maxsize = new int[1];
                GLES30.glGetIntegerv(GLES30.GL_MAX_TEXTURE_SIZE, maxsize, 0);
                int reqWidth, reqHeight;
                reqWidth = reqHeight = maxsize[0];

                // decode and downscale if needed to avoid OutOfMemory exception
                Bitmap bitmap = ImageUtil.decodeBitmapWithScaling(decryptedData, reqWidth, reqHeight);
                runOnUiThread(() -> {
                    photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    photoView.setImageBitmap(bitmap);
                });
            } catch (Exception e) {
                MsgToaster.msgShort(this, R.string.file_not_decrypted_msg);
                e.printStackTrace();
            }
        });
    }

    /**
     * Request permission for {@code Manifest.permission.WRITE_EXTERNAL_STORAGE}. This method doesn't
     * guarantee the permission is granted. {@code waitingPermissionResult} indicates that the
     * app is waiting for user to grant/reject permission, and {@code permissionGranted} indicates
     * whether the permission is actually granted.
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            this.permissionGranted = true;
        } else {
            // if not granted, request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            this.waitingPermissionResult = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted
            this.permissionGranted = true;
        }
        this.waitingPermissionResult = false;
    }

    /**
     * Recover the image back to the Gallery.
     *
     * @param image
     * @return
     */
    private boolean recoverImage(Image image) {
        try {
            // create temp file
            File file = IOUtil.createExternalSharedFile(this, image.getName(), ".jpg");
            if (file != null) {
                if (decryptedData != null) {
                    IOUtil.write(decryptedData, file);
                } else {
                    byte[] encryptedData = IOUtil.read(new File(db.imgDao().getImagePath(imageName)));
                    IOUtil.write(CryptoUtil.decrypt(encryptedData, authService.getImgKey()), file);
                }
                // expose it to Gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
