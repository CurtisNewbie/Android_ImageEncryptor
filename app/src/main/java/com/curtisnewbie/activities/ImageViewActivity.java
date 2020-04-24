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

import com.curtisnewbie.crypto.ImageUtil;
import com.curtisnewbie.daoThread.GetImgPathThread;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DBManager;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Show the image that is selected from the ImageListActivity
 */
public class ImageViewActivity extends AppCompatActivity {

    public static final String TAG = "ImageViewActivity";
    private ImageView imageView;
    private AppDatabase db;
    private String pw;
    private Bitmap bitmap;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = this.findViewById(R.id.imageView);

        // room database
        db = DBManager.getInstance(null).getDB();

        // get the data from the database
        String imageName = getIntent().getStringExtra(ImageListAdapter.IMG_TITLE);

        Thread t = new GetImgPathThread(this, db, imageName);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] encryptedData = loadEncryptedData(imgPath);

        if (encryptedData != null) {
            // decrypt the data
            pw = getIntent().getStringExtra(DBManager.PW_TAG);
            byte[] data = ImageUtil.decrypt(encryptedData, pw);

            // show image
            try {
                // required size
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int reqHeight = displayMetrics.heightPixels;
                int reqWidth = displayMetrics.widthPixels;

                // decode and downscale if needed to avoid OutOfMemory exception
                bitmap = decodeBitmapWithScaling(data, reqWidth, reqHeight);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageBitmap(bitmap);

                // setup dialogue for zoomable PhotoView
                setupZoomableView();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Decryption Failed", Toast.LENGTH_SHORT).show();
            }
        }
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

                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    /**
     * Decode the data into bitmap based on the required size. It downscale
     * the image if necessary to avoid OutOfMemory issue.
     *
     * @param data      data of the image
     * @param reqWidth  required width
     * @param reqHeight required height
     * @return bitmap that is decoded.
     */
    private Bitmap decodeBitmapWithScaling(byte[] data, int reqWidth, int reqHeight) {

        // memory not yet allocated
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // for getting the outWidth and outHeight
        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;

        // check whether it needs to be downscaled.
        int inSampleSize = 1;
        if (imgWidth > reqWidth || imgHeight > reqHeight) {

            inSampleSize = 2;

            if (imgWidth / inSampleSize > reqWidth && imgHeight / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }

        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Load the encrypted data from the local file dir
     *
     * @param path file path
     * @return encrypted data
     */
    private byte[] loadEncryptedData(String path) {

        try {
            // read each file
            InputStream in = new FileInputStream(new File(path));

            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

}

