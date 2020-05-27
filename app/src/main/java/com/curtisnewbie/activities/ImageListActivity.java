package com.curtisnewbie.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Button;

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
import com.curtisnewbie.util.ImageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static com.curtisnewbie.util.IntentUtil.hasIntentActivity;

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
public class ImageListActivity extends AppCompatActivity {
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
    private String tempFilePath;
    private boolean addImgBtnDisabled = false;
    private boolean takeImgBtnDisabled = false;
    private String pathFromMain = null;

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
            MsgToaster.msgShort(this, R.string.account_not_authenticated_msg);
            lifeCycleManager.restart();
            return;
        }
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

        // check if there is an image from main_activity to encrypt
        Intent intent = getIntent();
        pathFromMain = intent.getStringExtra(MainActivity.DATA_FROM_MAIN);
    }

    /**
     * Initialise ItemTouchHelper for swipe and drag and drop animations in recyclerview
     *
     * @return ItemTouchHelper for swipe and drag and drop animations in recyclerview
     */
    private ItemTouchHelper initItemTouchHelper() {
        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
                | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                final int from = viewHolder.getAdapterPosition();
                final int to = target.getAdapterPosition();
                ((ImageListAdapter) rAdapter).swapPosition(from, to);
                rAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final String imageName = ((ImageListAdapter.ViewHolder) viewHolder).getNameStr();
                final int index = viewHolder.getAdapterPosition();

                ImageListAdapter adapter = ((ImageListAdapter) rAdapter);
                // always remove the image from the view, such that the image name in the list can be consistently
                // recovered when user wants to revert the swipe animation
                adapter.deleteImageName(index);
                runOnUiThread(() -> {
                    adapter.createDeleteDialog(() -> {
                        // select positive btn
                        es.submit(() -> {
                            if (!adapter.deleteImageFile(imageName))
                                adapter.addImageName(imageName, index); // recover if not deleted
                        });
                    }, () -> {
                        // select negative btn, revert the swipe animation
                        adapter.addImageName(imageName, index);
                    });
                });
            }
        });
        return ith;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (pathFromMain != null) {
            es.submit(() -> {
                String fpath = pathFromMain;
                pathFromMain = null;
                try {
                    encryptImage(Uri.parse(fpath));
                    runOnUiThread(() -> {
                        createExitDialog();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Create a dialog that asks whether the user want to exit this app
     */
    private void createExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_dialog_title)
                .setPositiveButton(this.getString(R.string.positiveBtnTxt), (dia, id) -> {
                })
                .setNegativeButton(this.getString(R.string.negativeBtnTxt), (dia, id) -> {
                    finish();
                });
        AlertDialog dia = builder.create();
        dia.show();
    }

    /**
     * Register listener for {@code addImgBtn} that uses preferred software (e.g., Gallery) to
     * pick an image
     */
    private void regAddImgBtnListener() {
        // verify that there is at least one activity that can respond to this intent
        if (!hasIntentActivity(this, new Intent(Intent.ACTION_PICK)))
            addImgBtnDisabled = true;

        addImgBtn.setOnClickListener(view -> {
            if (!addImgBtnDisabled) {
                // do not reuse this intent, always create a new one
                Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
                selectImageIntent.setDataAndType(EXTERNAL_CONTENT_URI, "image/*"); // Data is URI
                startActivityForResult(Intent.createChooser(selectImageIntent, getString(R.string.image_chooser_title)), SELECT_IMAGE);
            } else {
                MsgToaster.msgShort(this, R.string.operation_not_supported);
            }
        });
    }

    /**
     * Register listener for {@code takeImgBtn} that uses preferred app (e.g., default camera) to
     * take picture
     */
    private void regTakeImgBtnListener() {
        if (!hasIntentActivity(this, new Intent(MediaStore.ACTION_IMAGE_CAPTURE)))
            takeImgBtnDisabled = true;

        takeImgBtn.setOnClickListener(view -> {
            if (!takeImgBtnDisabled) {
                // do not reuse this intent, always create a new one, as this can cause permission denial
                Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File tempFile = null;
                try {
                    tempFile = IOUtil.createTempFile(this);
                    tempFilePath = tempFile.getAbsolutePath(); // save absolute path for later access
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (tempFile != null) {
                    Uri tempUri = FileProvider.getUriForFile(this, getString(R.string.fileprovider_auth), tempFile);
                    // write the image to the tempFile
                    takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    startActivityForResult(takePicIntent, CAPTURE_IMAGE);
                }
            } else {
                MsgToaster.msgShort(this, R.string.operation_not_supported);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            es.submit(() -> {
                encryptImage(uri);
            });
        } else if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            es.submit(() -> {
                File tempFile = new File(tempFilePath);
                if (tempFile.exists()) {
                    encryptImage(tempFile);
                }
                if (!IOUtil.deleteFile(tempFile))
                    MsgToaster.msgLong(this, String.format("Fail to delete temp file, please delete it manually. It's at: '%s'",
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
     * Encrypt an image by its Uri. This is a high-level method that involves calling a chain of helper
     * methods, e.g., persisting the new {@code Image} to the database and updating the recyclerView.
     *
     * @param uri
     */
    private void encryptImage(Uri uri) {
        try (InputStream in = getContentResolver().openInputStream(uri);
             Cursor cursor = getContentResolver().query(uri, null, null, null, null);) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            final String fname = cursor.getString(nameIndex);
            final int fsize = cursor.getInt(sizeIndex);
            encryptImage(in, fname, fsize);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            MsgToaster.msgShort(this, R.string.file_not_found_msg);
        } catch (IOException e2) {
            e2.printStackTrace();
            MsgToaster.msgShort(this, R.string.file_not_read_msg);
        }
    }

    /**
     * Encrypt an image File. This is a high-level method that involves calling a chain of helper
     * methods, e.g., persisting the new {@code Image} to the database and updating the recyclerView.
     *
     * @param file
     */
    private void encryptImage(File file) {
        try (InputStream in = new FileInputStream(file);) {
            final String fname = file.getName();
            final int fsize = (int) file.length();
            encryptImage(in, fname, fsize);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            MsgToaster.msgShort(this, R.string.file_not_found_msg);
        } catch (IOException e2) {
            e2.printStackTrace();
            MsgToaster.msgShort(this, R.string.file_not_read_msg);
        }
    }

    /**
     * Encrypt an image from an InputStream. This is a high-level method that involves calling a chain of helper
     * methods, e.g., persisting the new {@code Image} to the database and updating the recyclerView.
     *
     * @param in       inputStream
     * @param filename
     * @param filesize size of the file or {@code ALL} for the whole file
     */
    private void encryptImage(InputStream in, String filename, int filesize) {
        try {
            if (db.imgDao().imageCount(filename) > 0) // already added
                return;

            encryptNWrite(in, filename, filesize);
            persistImage(filename);
            ((ImageListAdapter) this.rAdapter).addImageName(filename);
            MsgToaster.msgShort(this, String.format("Added: %s", filename));
        } catch (IOException e2) {
            e2.printStackTrace();
            MsgToaster.msgShort(this, R.string.file_not_read_msg);
        }
    }

    /**
     * Persist the {@code Image} into the database (not the actual encrypted data).
     * <p>
     * This is a helper method, to undertake a series of operations involved for image encryption, consider using
     * {@code ImageListActivity#encryptImage(...)}
     *
     * @param filename name of the encrypted image that will be created
     */
    private void persistImage(String filename) {
        // persist image (name and path)
        Image img = new Image();
        img.setName(filename);
        img.setPath(ImageListActivity.this.getFilesDir().getPath() + "//" + filename);
        img.setThumbnailPath(img.getPath() + ImageUtil.THUMBNAIL_EXTENSION);
        img.setTimestamp(System.currentTimeMillis());
        db.imgDao().addImage(img);
    }

    /**
     * Encrypt bytes from the input stream and write them to internal storage. Both the image and a
     * downscaled thumbnail are encrypted and stored locally.
     * <p>
     * This is a helper method, to undertake a series of operations involved for image encryption, consider using
     * {@code ImageListActivity#encryptImage(...)}
     *
     * @param in       input stream
     * @param filename name of the file (encrypted) that will be created in internal
     *                 storage
     * @param filesize size of the file
     * @throws IOException
     */
    private void encryptNWrite(InputStream in, String filename, int filesize) throws IOException {
        // read the image
        byte[] rawData = IOUtil.read(in, filesize);

        // downscale image for thumbnail
        Bitmap bitmap = ImageUtil.decodeBitmap(rawData);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, ImageUtil.THUMBNAIL_SIZE, ImageUtil.THUMBNAIL_SIZE);
        byte[] thumbnail = ImageUtil.toBytes(bitmap);

        // encrypt image & thumbnail
        byte[] encryptedData = CryptoUtil.encrypt(rawData, authService.getImgKey());
        byte[] encryptedThumbnail = CryptoUtil.encrypt(thumbnail, authService.getImgKey());

        // write encrypted image to internal storage
        IOUtil.write(encryptedData, filename, this);
        // write thumbnail to internal storage
        IOUtil.write(encryptedThumbnail, filename + ImageUtil.THUMBNAIL_EXTENSION, this);
    }
}
