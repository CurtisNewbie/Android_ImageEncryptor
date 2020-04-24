package com.curtisnewbie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DBManager;
import com.curtisnewbie.database.Image;
import com.curtisnewbie.crypto.ImageUtil;
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

    // password passed to this activity
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        // get password when refreshing this activity
        pw = getIntent().getStringExtra(DBManager.PW_TAG);

        recycleView = findViewById(R.id.recycleView);
        addImgBtn = findViewById(R.id.addImgBtn);

        // set fix layout size of the recycle view to improve performance
        recycleView.setHasFixedSize(true);

        /*
         adapter that adapt individual items (activity_each_item.xml);
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

    @Override
    public void onBackPressed() {
        // go back to the login page
        Intent intent = new Intent(".MainActivity");
        startActivity(intent);
    }

    /**
     * initialise file picker (variables) - the dialogue for selecting files.
     */
    private void iniFilePicker() {

        // Dialogue Properties
        properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.MULTI_MODE;
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
                // encrypt the files, store them to the db
                encryptNPersist(files);
            }
        });
    }

    /**
     * Read images, encrypt them, write the encrypted images data into local internal storage,
     * and persist their names and filepath (of the encrypted ones) in the database. Note that
     * a separate Thread is spawned to undertake this operation.
     *
     * @param files list of image files that are not encrypted.
     */
    private void encryptNPersist(String[] files) {
        new Thread(() -> {
            AppDatabase db = DBManager.getInstance(null).getDB();
            File file;
            for (String f : files) {
                file = new File(f);
                try {
                    // encrypt and write the encrypted image
                    encryptNWrite(file);
                    // persist image (name and path)
                    Image img = new Image();
                    img.setName(file.getName());
                    img.setPath(ImageListActivity.this.getFilesDir().getPath() + "//" + file.getName());
                    db.imgDao().addImage(img);
                    // update RecyclerView
                    ((ImageListAdapter) this.rAdapter).addImageName(img.getName());
                } catch (FileNotFoundException e) {
                    Toast.makeText(ImageListActivity.this, "Fail to find file:"
                            + file.getName(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(ImageListActivity.this, "Fail to read from file:"
                            + file.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    /**
     * Encrypt Image file using the given pw and write them to internal storage. I/O is auto closed
     * even when exceptions are thrown.
     *
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void encryptNWrite(File file) throws FileNotFoundException, IOException {
        try (InputStream in = new FileInputStream(file);
             OutputStream out = ImageListActivity.this.openFileOutput(file.getName()
                     , MODE_PRIVATE);) {
            // read the selected images
            byte[] rawData = new byte[in.available()];
            in.read(rawData);

            // encrypt selected images and output the encrypted file to asset folder
            byte[] encryptedData = ImageUtil.encrypt(rawData, pw);
            out.write(encryptedData);
        }
    }
}
