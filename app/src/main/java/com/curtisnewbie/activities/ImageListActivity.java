package com.curtisnewbie.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DBManager;
import com.curtisnewbie.database.Image;
import com.curtisnewbie.util.CryptoUtil;
import com.curtisnewbie.util.IOManager;
import com.curtisnewbie.util.ThreadManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

/**
 * Shows a list of images, this uses the recyclerView to show list of 'items/smaller views'.
 */
public class ImageListActivity extends AppCompatActivity implements Promptable {
    /**
     * Request code code selecting images in Gallery
     */
    public static final int SELECT_IMAGE = 1;

    // recyclerView
    private RecyclerView recycleView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rManager;

    // components
    private Button addImgBtn;

    // password passed to this activity
    private String pw;
    private ThreadManager tm = ThreadManager.getThreadManager();

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

        // initiate preferred software (e.g., Gallery) to pick an image
        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(EXTERNAL_CONTENT_URI); // Data is URI
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Images"), SELECT_IMAGE);
            }
        });
        prompt("Login Successful");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_IMAGE && data != null) {
            Uri uri = data.getData();
            try {
                Cursor cursor = getContentResolver().query(uri, null, null,
                        null, null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();
                encryptNPersist(getContentResolver().openInputStream(uri), cursor.getString(nameIndex),
                        cursor.getInt(sizeIndex));
            } catch (FileNotFoundException e) {
                prompt("File not found.");
            }
        }
    }

    @Override
    public void onBackPressed() {
        // go back to the login page
        Intent intent = new Intent(".MainActivity");
        startActivity(intent);
    }

    /**
     * Read image, encrypt it, write the encrypted images data into local internal storage,
     * and persist the name and filepath (of the encrypted ones) in the database. Note that
     * a separate Thread is spawned to undertake this operation.
     *
     * @param in       input stream of a image file that is not encrypted.
     * @param filename name of the encrypted image that will be created
     * @param filesize size of the file
     */
    private void encryptNPersist(InputStream in, String filename, int filesize) {
        tm.submit(() -> {
            AppDatabase db = DBManager.getInstance(null).getDB();
            try {
                // encrypt and write the encrypted image
                encryptNWrite(in, filename, filesize);
                // persist image (name and path)
                Image img = new Image();
                img.setName(filename);
                img.setPath(ImageListActivity.this.getFilesDir().getPath() + "//" + filename);
                db.imgDao().addImage(img);
                // update RecyclerView
                ((ImageListAdapter) this.rAdapter).addImageName(img.getName());
                prompt(String.format("Added: %s", filename));
            } catch (FileNotFoundException e) {
                prompt("Fail to find file:");
            } catch (IOException e) {
                prompt("Fail to read from file:");
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Encrypt bytes from the input stream using the given pw and write them to internal storage.
     * I/O is auto closed even when exceptions are thrown.
     *
     * @param in       input stream
     * @param filename name of the file (encrypted) that will be created in internal storage
     * @param filesize size of the file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void encryptNWrite(InputStream in, String filename, int filesize) throws FileNotFoundException, IOException {
        // read the image
        byte[] rawData = IOManager.read(in, filesize);
        // encrypt image
        byte[] encryptedData = CryptoUtil.encrypt(rawData, pw);
        // write encrypted image to internal storage
        IOManager.write(encryptedData, filename, this);
    }

    @Override
    public void prompt(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
    }
}
