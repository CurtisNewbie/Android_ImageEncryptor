package com.curtisnewbie.androidDev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.curtisnewbie.ImageItem.Image;
import com.curtisnewbie.ImageItem.TempDataStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Shows a list of images
 */
public class ImageListActivity extends AppCompatActivity {

    private RecyclerView recycleView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rManager;

    public static final String TAG = "ImageList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        recycleView = findViewById(R.id.recycleView);

        // set fix layout size of the recycle view to improve performance
        recycleView.setHasFixedSize(true);

        // Test data
        ArrayList images = new ArrayList<Image>();
        try {
            InputStream in = getResources().getAssets().open("encrypted.txt");
            byte[] tData = new byte[in.available()];
            in.read(tData);
            images.add(new Image(tData, "ImgOne"));
            in.close();
            Log.i(TAG, "data preped");
        } catch (IOException e) {
            Log.e(TAG, "Test data exception");
        }

        // adapter that adapt inidividual items (activity_each_item.xml)
        rAdapter = new ImageListAdapter(images, this);
        recycleView.setAdapter(rAdapter);

        // linear manager
        rManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(rManager);

        Toast.makeText(ImageListActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TempDataStorage.getInstance().cleanTempData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TempDataStorage.getInstance().cleanTempData();
    }
}
