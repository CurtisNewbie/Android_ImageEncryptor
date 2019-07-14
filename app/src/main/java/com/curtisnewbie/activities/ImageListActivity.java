package com.curtisnewbie.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;

import java.io.File;

/**
 * Shows a list of images
 */
public class ImageListActivity extends AppCompatActivity {

    private RecyclerView recycleView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rManager;
    private Button addImgBtn;
    private DialogProperties properties;
    private FilePickerDialog dialog;

    public static final String TAG = "ImageList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        recycleView = findViewById(R.id.recycleView);
        addImgBtn = findViewById(R.id.addImgBtn);

        // set fix layout size of the recycle view to improve performance
        recycleView.setHasFixedSize(true);

        // adapter that adapt inidividual items (activity_each_item.xml)
        rAdapter = new ImageListAdapter(this);
        recycleView.setAdapter(rAdapter);

        // linear manager
        rManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(rManager);

        //initiate file picker
        iniFilePicker();
        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the file picker dialogue
                dialog.show();
            }
        });

        Toast.makeText(ImageListActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
    }

    private void iniFilePicker() {

        // Dialogue Properties
        properties = new DialogProperties();

        // setup properties
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        // create FilePickerDialog
        dialog = new FilePickerDialog(this, properties);
        dialog.setTitle("Select an image file");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
            }
        });



    }
}
