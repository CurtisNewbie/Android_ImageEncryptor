package com.curtisnewbie.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.ImgCrypto.Image;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DataStorage;

/**
 * Show the image that is selected from the ImageListActivity
 */
public class ImageViewActivity extends AppCompatActivity {

    public static final String TAG = "ImageViewActivity";
    private ImageView imageView;
    private AppDatabase db;
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = this.findViewById(R.id.imageView);
        db = DataStorage.getInstance(null).getDB();

        // get the data from the database
        String imageName = getIntent().getStringExtra(ImageListAdapter.IMG_TITLE);
        byte[] encryptedData = db.dao().getImgData(imageName);

        // decrypt the data
        pw = getIntent().getStringExtra(DataStorage.PW_TAG);
        byte[] data = Image.decrypt(encryptedData, pw);

        // show image
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageBitmap(bitmap);
        } catch (RuntimeException e) {
            Log.w(TAG, e.toString() + ":" + e.getMessage());
            Toast.makeText(this, "Decryption Failed", Toast.LENGTH_SHORT).show();
        }


    }
}

