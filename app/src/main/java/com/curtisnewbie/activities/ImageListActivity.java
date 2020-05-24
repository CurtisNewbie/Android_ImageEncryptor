package com.curtisnewbie.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.Image;
import com.curtisnewbie.services.App;
import com.curtisnewbie.services.AuthService;
import com.curtisnewbie.util.CryptoUtil;
import com.curtisnewbie.util.IOUtil;
import com.curtisnewbie.services.ExecService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * {@code Activity} that shows a list of images using {@code RecyclerView}
 * </p>
 */
public class ImageListActivity extends AppCompatActivity implements Promptable {
    /**
     * Request code for selecting image
     */
    public static final int SELECT_IMAGE = 1;
    /**
     * Request code for capturing image
     */
    public static final int CAPTURE_IMAGE = 2;

    private RecyclerView recycleView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rManager;
    private Button addImgBtn;
    private Button takeImgBtn;
    private String imgKey;
    private String tempFilePath;

    @Inject
    protected ExecService es;
    @Inject
    protected AppDatabase db;
    @Inject
    protected AuthService authService;
    @Inject
    protected AppLifeCycleManager lifeCycleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inject dependencies
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        if (!authService.isAuthenticated()) {
            prompt("Your are not authenticated. Please sign in first.");
            lifeCycleManager.restart();
            return;
        }
        // setup image key for encryption/decryption
        imgKey = authService.getImgKey();

        addImgBtn = findViewById(R.id.addImgBtn);
        takeImgBtn = findViewById(R.id.takeImgBtn);
        recycleView = findViewById(R.id.recycleView);
        recycleView.setHasFixedSize(true); // set fix layout size of the recycle view to improve performance
        rAdapter = new ImageListAdapter(this);
        recycleView.setAdapter(rAdapter);
        rManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(rManager);

        // get ItemTouchHelper for swipe & delete animation
        this.initItemTouchHelper().attachToRecyclerView(recycleView);

        // initiate preferred software (e.g., Gallery) to pick an image
        this.regAddImgBtnListener();
        // initiate preferred app (e.g., default camera) to take picture
        this.regTakeImgBtnListener();
    }

    /**
     * Initialise ItemTouchHelper for swipe animation in recyclerview
     *
     * @return ItemTouchHelper for swipe animation in recyclerview
     */
    private ItemTouchHelper initItemTouchHelper() {
        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
                | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                // TODO: not supported for now
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    es.submit(() -> {
                        ((ImageListAdapter) rAdapter).deleteImageNFile(viewHolder.getAdapterPosition());
                    });
                }
            }
        });
        return ith;
    }

    /**
     * Register listener for {@code addImgBtn} that uses preferred software (e.g., Gallery) to
     * pick an image
     */
    private void regAddImgBtnListener() {
        addImgBtn.setOnClickListener(view -> {
            Intent selectImageIntent = new Intent();
            selectImageIntent.setAction(Intent.ACTION_PICK);
            selectImageIntent.setDataAndType(EXTERNAL_CONTENT_URI, "image/*"); // Data is URI
            startActivityForResult(Intent.createChooser(selectImageIntent, "Select Images"), SELECT_IMAGE);
        });
    }

    /**
     * Register listener for {@code takeImgBtn} that uses preferred app (e.g., default camera) to
     * take picture
     */
    private void regTakeImgBtnListener() {
        takeImgBtn.setOnClickListener(view -> {
            Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePicIntent.resolveActivity(getPackageManager()) != null) {
                // there are packages that can resolve this intent (i.e., take picture)
                File tempFile = null;
                try {
                    tempFile = IOUtil.createTempFile(this);
                    tempFilePath = tempFile.getAbsolutePath(); // save absolute path for later access
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (tempFile != null) {
                    Uri tempUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", tempFile);
                    // write the image to the tempFile
                    takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(takePicIntent, CAPTURE_IMAGE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            es.submit(() -> {
                try (InputStream in = getContentResolver().openInputStream(uri);) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    cursor.moveToFirst();
                    encryptNPersist(in, cursor.getString(nameIndex), cursor.getInt(sizeIndex));
                } catch (FileNotFoundException e) {
                    prompt("File not found.");
                } catch (IOException e1) {
                    prompt("Failed to encrypt image");
                }
            });
        } else if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            es.submit(() -> {
                File tempFile = new File(tempFilePath);
                if (tempFile.exists())
                    try (InputStream in = new FileInputStream(tempFile)) {
                        encryptNPersist(in, tempFile.getName(), (int) tempFile.length());
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                if (!IOUtil.deleteFile(tempFile))
                    prompt(String.format("Fail to delete temp file, please delete it manually. It's at: '%s'",
                            tempFile.getAbsolutePath()));
            });
        }
    }

    @Override
    public void onBackPressed() {
        lifeCycleManager.restart();
        return;
    }

    /**
     * Read image, encrypt it, write the encrypted images data into local internal
     * storage, and persist the name and filepath (of the encrypted ones) in the
     * database.
     *
     * @param in       input stream of a image file that is not encrypted.
     * @param filename name of the encrypted image that will be created
     * @param filesize size of the file
     */
    private void encryptNPersist(InputStream in, String filename, int filesize) {
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
        } catch (FileNotFoundException e1) {
            prompt("Fail to find file:");
            e1.printStackTrace();
        } catch (IOException e2) {
            prompt("Fail to read from file:");
            e2.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Encrypt bytes from the input stream and write them to internal storage. I/O
     * is auto closed even when exceptions are thrown.
     *
     * @param in       input stream
     * @param filename name of the file (encrypted) that will be created in internal
     *                 storage
     * @param filesize size of the file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void encryptNWrite(InputStream in, String filename, int filesize)
            throws FileNotFoundException, IOException {
        // read the image
        byte[] rawData = IOUtil.read(in, filesize);
        // encrypt image
        byte[] encryptedData = CryptoUtil.encrypt(rawData, imgKey);
        // write encrypted image to internal storage
        IOUtil.write(encryptedData, filename, this);
    }

    @Override
    public void prompt(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
    }
}
