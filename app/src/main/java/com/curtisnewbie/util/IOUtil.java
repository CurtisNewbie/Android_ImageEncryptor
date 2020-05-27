package com.curtisnewbie.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that is responsible for managing I/O operations.
 * </p>
 */
public class IOUtil {

    /**
     * Read all bytes from file
     *
     * @param file file
     * @return bytes of the file
     * @throws IOException
     */
    public static byte[] read(File file) throws IOException {
        try (InputStream in = new FileInputStream(file);) {
            return read(in, (int) file.length());
        }
    }

    /**
     * Read bytes from an input stream
     *
     * @param in  input stream
     * @param len number of bytes should be read
     * @return bytes
     */
    public static byte[] read(InputStream in, int len) throws IOException {
        byte[] bytes = new byte[len];
        in.read(bytes);
        return bytes;
    }

    /**
     * Write all bytes to file
     *
     * @param bytes binary data
     * @param file  file
     * @throws IOException
     */
    public static void write(byte[] bytes, File file) throws IOException {
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(bytes);
        }
    }

    /**
     * Write all bytes to a file that will be created in internal storage
     *
     * @param bytes       bytes
     * @param fileOutName name of the file that the data are written to in internal
     *                    storage
     * @param context     context
     * @throws IOException
     */
    public static void write(byte[] bytes, String fileOutName, Context context) throws IOException {
        try (OutputStream out = context.openFileOutput(fileOutName, MODE_PRIVATE)) {
            out.write(bytes);
        }
    }

    /**
     * Create temp file with an extension '.t' that has a name created like this:
     * '{@code "PIC" + DateUtil.getDateTimeStr()}'. This temp file tho is not protected in internal
     * storage, it can be unavailable to other apps as well.
     *
     * @param context
     * @return a temp file
     */
    public static File createTempFile(Context context) throws IOException {
        String filename = "PIC" + DateUtil.getDateStr();
        File storageDir = context.getExternalFilesDir(DIRECTORY_PICTURES);
        File tempFile = File.createTempFile(filename, ".t", storageDir);
        return tempFile;
    }

    /**
     * Create temp file with the specified filename and file extension. This file is created in
     * external storage public directory (DIRECTORY_PICTURES), thus it can be visible to the user
     * as well as other apps. This method requires {@code Manifest.permission.WRITE_EXTERNAL_STORAGE}
     *
     * @param filename
     * @param fileExtension
     * @return a temp file
     */
    public static File createExternalSharedFile(String filename, String fileExtension) throws IOException {
        if (!fileExtension.startsWith("."))
            fileExtension = "." + fileExtension;
        if (filename.isEmpty())
            filename = "PIC" + DateUtil.getDateStr();
        File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File tempFile = File.createTempFile(filename, fileExtension, storageDir);
        return tempFile;
    }


    /**
     * Attempt to delete a file
     *
     * @param file
     * @return whether the file is deleted
     */
    public static boolean deleteFile(File file) {
        if (file.delete()) {
            return true;
        }
        return !file.exists();
    }

    /**
     * Attempt to delete a file
     *
     * @param path
     * @return whether the file is deleted
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return deleteFile(file);
    }
}
