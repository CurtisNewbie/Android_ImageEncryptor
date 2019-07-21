package com.curtisnewbie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtisnewbie.imgCrypto.Image;
import com.curtisnewbie.daoThread.AddImgThread;
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
 * Shows a list of images, this uses the recyclerView to show list of 'items/smaller views'.
 */
public class ImageListActivity extends AppCompatActivity {

    // recyclerView
    private RecyclerView recycleView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rManager;

    // components
    private Button addImgBtn;
    private DialogProperties properties;

    // FilePicker - the dialog that allows users to select file.
    private FilePickerDialog dialog;
    private List<File> selectedFiles;

    // password passed to this activitiy
    private String pw;

    // tag used for putExtras for refreshing this activity.
    public static final String TAG = "ImageList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        // get password when refreshing this activity
        pw = getIntent().getStringExtra(DataStorage.PW_TAG);
        selectedFiles = null;

        recycleView = findViewById(R.id.recycleView);
        addImgBtn = findViewById(R.id.addImgBtn);

        // set fix layout size of the recycle view to improve performance
        recycleView.setHasFixedSize(true);

        /*
         adapter that adapt inidividual items (activity_each_item.xml);
         and passing password to the adapter for decrypting images.
          */
        rAdapter = new ImageListAdapter(this, pw);
        recycleView.setAdapter(rAdapter);

        // linear manager
        rManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(rManager);

        // initiate file picker variables
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

    /**
     * initialise file picker (variables) - the dialogue for selecting files.
     */
    private void iniFilePicker() {

        // Dialogue Properties
        properties = new DialogProperties();
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
                            // read the selected images
                            InputStream in = new FileInputStream(file);
                            byte[] rawData = new byte[in.available()];
                            in.read(rawData);
                            in.close();

                            // encrypt selected images and output the encrypted file to asset folder
                            byte[] encryptedData = Image.encrypt(rawData, pw);
                            OutputStream out = ImageListActivity.this.openFileOutput(file.getName()
                                    , MODE_PRIVATE);
                            out.write(encryptedData);
                            out.close();

                            // store name and path in database
                            ImageData img = new ImageData();
                            img.setImage_name(file.getName());
                            img.setImage_path(ImageListActivity.this.getFilesDir().getPath() + "//" + file.getName());

                            new AddImgThread(img, db).start();
                        } catch (FileNotFoundException e) {
                            Toast.makeText(ImageListActivity.this, "Fail to find file:"
                                    + file.getName(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(ImageListActivity.this, "Fail to read from file:"
                                    + file.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    refreshIntent();
                }
            }
        });
    }

    private void refreshIntent(){
        // refresh the activity
        Intent intent = getIntent();
        intent.putExtra(DataStorage.PW_TAG, pw);
        startActivity(intent);
    }
}
