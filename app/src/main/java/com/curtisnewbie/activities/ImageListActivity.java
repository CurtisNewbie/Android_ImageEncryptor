package com.curtisnewbie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtisnewbie.ImgCrypto.Image;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DataStorage;
import com.curtisnewbie.database.ImageData;
import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

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
    private String pw;

    private List<File> selectedFiles;

    public static final String TAG = "ImageList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        pw = getIntent().getStringExtra(DataStorage.PW_TAG);
        selectedFiles = null;
        recycleView = findViewById(R.id.recycleView);
        addImgBtn = findViewById(R.id.addImgBtn);

        // set fix layout size of the recycle view to improve performance
        recycleView.setHasFixedSize(true);

        // adapter that adapt inidividual items (activity_each_item.xml)
        rAdapter = new ImageListAdapter(this, pw);
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
                selectedFiles = new LinkedList<>();
                for (String path : files) {
                        selectedFiles.add(new File(path));
                }

                // encrypt the files, store them to the db
                if (selectedFiles != null) {
                    AppDatabase db = DataStorage.getInstance(null).getDB();
                    for (File file : selectedFiles) {
                        try {
                            InputStream in = new FileInputStream(file);
                            byte[] rawData = new byte[in.available()];
                            in.read(rawData);
                            in.close();

                            // encrypt
                            byte[] encryptedData = Image.encrypt(rawData, pw);
                            ImageData img = new ImageData();
                            img.setImage_name(file.getName());
                            img.setImage_data(encryptedData);
                            db.dao().addImageData(img);

                            // output the encrypted file to asset folder
                            OutputStream out = ImageListActivity.this.openFileOutput(file.getName() + ".txt", MODE_PRIVATE);
                            out.write(encryptedData);
                            out.close();
                        } catch (FileNotFoundException e) {
                            Toast.makeText(ImageListActivity.this, "Fail to find file:" + file.getName(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(ImageListActivity.this, "Fail to read from file:" + file.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Intent intent = getIntent();
                intent.putExtra(DataStorage.PW_TAG, pw);
                startActivity(intent);
            }
        });


    }
}
