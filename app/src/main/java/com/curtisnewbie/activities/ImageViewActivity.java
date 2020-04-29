package com.curtisnewbie.activities;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
public class ImageViewActivity extends AppCompatActivity implements Promptable {
    @Inject
    protected AppDatabase db;
    @Inject
    protected AuthService authService;
    @Inject
    protected ExecService tm;
    private PhotoView photoView;
    private Bitmap bitmap;
    private String imgKey;
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imgKey = authService.getImgKey();
        photoView = this.findViewById(R.id.photoView);
        photoView.setMaximumScale(20.0f);
        imageName = getIntent().getStringExtra(ImageListAdapter.IMG_NAME);
        decryptNDisplay();
    }

    /**
     * Decrypt and display the selected image
     */
    private void decryptNDisplay() {
        if (imgKey != null)
            // get the path to the decrypted image, decrypt data and display
            tm.submit(() -> {
                try {
                    // read encrypted data
                    String imgPath = db.imgDao().getImagePath(imageName);
                    byte[] encryptedData = IOUtil.read(new File(imgPath));

                    // decrypt the data
                    byte[] decryptedData = CryptoUtil.decrypt(encryptedData, imgKey);

                    // get the allowed maximum size of texture in OpenGL ES3.0
                    int[] maxsize = new int[1];
                    GLES30.glGetIntegerv(GLES30.GL_MAX_TEXTURE_SIZE, maxsize, 0);
                    int reqWidth, reqHeight;
                    reqWidth = reqHeight = maxsize[0];

                    // decode and downscale if needed to avoid OutOfMemory exception
                    this.bitmap = ImageUtil.decodeBitmapWithScaling(decryptedData, reqWidth, reqHeight);
                    runOnUiThread(() -> {
                        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        photoView.setImageBitmap(bitmap);
                    });
                } catch (Exception e) {
                    prompt("Decryption Failed");
                    e.printStackTrace();
                }
            });
        else
            prompt("Your are not authenticated. Please sign in first.");
    }

    @Override
    public void prompt(String msg) {
        this.runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
