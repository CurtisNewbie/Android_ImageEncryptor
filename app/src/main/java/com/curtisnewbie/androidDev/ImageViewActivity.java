package com.curtisnewbie.androidDev;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.ImageItem.Image;
import com.curtisnewbie.database.DataStorage;
import com.curtisnewbie.database.DatabaseHelper;

import java.io.IOException;

/**
 * Show the image that is selected from the ImageListActivity
 */
public class ImageViewActivity extends AppCompatActivity {

    public static final String TAG = "ImageViewActivity";
    private ImageView imageView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = this.findViewById(R.id.imageView);

        db = DataStorage.getInstance(null).getDB();
        String imageName = getIntent().getStringExtra(ImageListAdapter.IMG_TITLE);

        // get the data from the database
        byte[] encryptedData = db.getEncryptedImgData(imageName);

        // decrypt the data
        Image image = new Image(encryptedData);
        try {
            byte[] data = image.decrypt(db.getDecryPW());

            // show image
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Decryption Failed", Toast.LENGTH_SHORT).show();
        }
    }
}

