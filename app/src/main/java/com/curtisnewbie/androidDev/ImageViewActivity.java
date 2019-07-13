package com.curtisnewbie.androidDev;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.ImageItem.Image;
import com.curtisnewbie.ImageItem.TempDataStorage;
import com.curtisnewbie.account.Account;

import java.io.IOException;
import java.io.InputStream;

/**
 * Show the image that is selected from the ImageListActivity
 */
public class ImageViewActivity extends AppCompatActivity {

    public static final String TAG = "ImageViewActivity";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = this.findViewById(R.id.imageView);

        // get the data from the previous activity
        byte[] data;
        data = TempDataStorage.getInstance().getTempData();
        TempDataStorage.getInstance().cleanTempData();

        // show image
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageBitmap(bitmap);

    }
}

