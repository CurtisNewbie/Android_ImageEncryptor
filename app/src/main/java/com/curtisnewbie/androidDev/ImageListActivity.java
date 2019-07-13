package com.curtisnewbie.androidDev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.curtisnewbie.ImageItem.Image;

import java.util.ArrayList;

/**
 * Shows a list of images
 */
public class ImageListActivity extends AppCompatActivity {

    private RecyclerView recycleView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        recycleView = findViewById(R.id.recycleView);

        // set fix layout size of the recycle view to improve performance
        recycleView.setHasFixedSize(true);

        // Test data
        ArrayList images = new ArrayList<Image>();
        images.add(new Image(null, "ImgOne"));
        images.add(new Image(null, "ImgTwo"));
        images.add(new Image(null, "ImgThree"));

        // adapter that adapt inidividual items (activity_each_item.xml)
        rAdapter = new ImageListAdapter(images, this);
        recycleView.setAdapter(rAdapter);

        // linear manager
        rManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(rManager);

        Toast.makeText(ImageListActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
    }
}
