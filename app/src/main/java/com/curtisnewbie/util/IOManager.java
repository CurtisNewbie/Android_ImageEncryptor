package com.curtisnewbie.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class that is responsible for managing I/O.
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
     * @param fileOutName name of the file that the data are written to in internal storage
     * @param context     context
     * @throws IOException
     */
    public static void write(byte[] bytes, String fileOutName, Context context) throws IOException {
        try (OutputStream out = context.openFileOutput(fileOutName, MODE_PRIVATE)) {
            out.write(bytes);
        }
    }
}
