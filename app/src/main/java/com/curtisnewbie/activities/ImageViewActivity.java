package com.curtisnewbie.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.ImgCrypto.Image;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DataStorage;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Show the image that is selected from the ImageListActivity
 */
public class ImageViewActivity extends AppCompatActivity {

    public static final String TAG = "ImageViewActivity";
    private ImageView imageView;
    private AppDatabase db;
    private String pw;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = this.findViewById(R.id.imageView);
        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                // create dialogue for a zoomable PhotoView object.
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ImageViewActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialogue_zoomable_layout, null);

                PhotoView photoView = mView.findViewById(R.id.zoomView);
                photoView.setImageBitmap(bitmap);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        // room database
        db = DataStorage.getInstance(null).getDB();

        // get the data from the database
        String imageName = getIntent().getStringExtra(ImageListAdapter.IMG_TITLE);
        byte[] encryptedData = db.dao().getImgData(imageName);

        // decrypt the data
        pw = getIntent().getStringExtra(DataStorage.PW_TAG);
        byte[] data = Image.decrypt(encryptedData, pw);

        // show image
        try {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Decryption Failed", Toast.LENGTH_SHORT).show();
        }

    }



}

