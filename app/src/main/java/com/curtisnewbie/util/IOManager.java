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
public class IOManager {

    /**
     * Read all bytes from file
     *
     * @param file file
     * @return bytes of the file
     * @throws IOException
     */
    public static byte[] read(File file) throws IOException {
        try (InputStream in = new FileInputStream(file);) {
            byte[] bytes = new byte[(int) file.length()];
            in.read(bytes);
            return bytes;
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
     * Create temp file with name created like this:
     * '{@code "PIC" + DateUtil.getDateTimeStr()}'
     *
     * @param context
     * @return a temp file
     */
    public static File createTempFile(Context context) throws IOException {
        String filename = "PIC" + DateUtil.getDateTimeStr();
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File tempFile = File.createTempFile(filename, ".t", storageDir);
        return tempFile;
    }

    /**
     * Attempt to delete the file for 10 times at most
     *
     * @param file
     * @return whether the file is deleted
     */
    public static boolean deleteFile(File file) {
        for (int i = 0; i < 10; i++) {
            if (file.delete()) {
                return true;
            }
        }
        return !file.exists();
    }

}
