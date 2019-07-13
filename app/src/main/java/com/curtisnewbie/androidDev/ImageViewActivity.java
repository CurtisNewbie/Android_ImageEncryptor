package com.curtisnewbie.androidDev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Show the image that is selected from the ImageListActivity
 */
public class ImageViewActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = this.findViewById(R.id.imageView);


    }
}

