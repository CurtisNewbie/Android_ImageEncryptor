package com.curtisnewbie.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.util.CryptoUtil;
import com.curtisnewbie.util.IOManager;
import com.curtisnewbie.util.ImageUtil;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DBManager;
import com.curtisnewbie.util.ThreadManager;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

/**
 * Show the image that is selected from the ImageListActivity
 */
public class ImageViewActivity extends AppCompatActivity implements Promptable {

    public static final String TAG = "ImageViewActivity";
    private ImageView imageView;
    private AppDatabase db;
    private String pw;
    private Bitmap bitmap;
    private ThreadManager tm = ThreadManager.getThreadManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = this.findViewById(R.id.imageView);
        // room database
        db = DBManager.getInstance(null).getDB();
        // get the imageName passed by intent
        String imageName = getIntent().getStringExtra(ImageListAdapter.IMG_TITLE);
        //get the path to the decrypted image, decrypt data and display
        tm.submit(() -> {
            try {
                // read encrypted data
                String imgPath = db.imgDao().getImagePath(imageName);
                byte[] encryptedData = IOManager.read(new File(imgPath));

                // decrypt the data
                pw = getIntent().getStringExtra(DBManager.PW_TAG);
                byte[] decryptedData = CryptoUtil.decrypt(encryptedData, pw);

                // show image
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int reqHeight = displayMetrics.heightPixels;
                int reqWidth = displayMetrics.widthPixels;

                // decode and downscale if needed to avoid OutOfMemory exception
                this.bitmap = ImageUtil.decodeBitmapWithScaling(decryptedData, reqWidth, reqHeight);
                runOnUiThread(() -> {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setImageBitmap(bitmap);
                });

                // setup dialogue for zoomable PhotoView
                setupZoomableView();
            } catch (Exception e) {
                prompt("Decryption Failed");
                e.printStackTrace();
            }
        });
    }

    /**
     * Create dialogue that contains a zoomable PhotoView when the imageView is clicked
     */
    private void setupZoomableView() {
        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // create dialogue for a zoomable PhotoView object.
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ImageViewActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialogue_zoomable_layout, null);
                PhotoView zoomView = mView.findViewById(R.id.zoomView);
                zoomView.setImageBitmap(bitmap);
                zoomView.setMaximumScale(20.0f);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    @Override
    public void prompt(String msg) {
        this.runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}

