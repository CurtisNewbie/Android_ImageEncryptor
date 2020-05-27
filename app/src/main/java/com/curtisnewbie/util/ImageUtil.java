package com.curtisnewbie.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.widget.ImageView;

import com.curtisnewbie.activities.MsgToaster;
import com.curtisnewbie.activities.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Util Class for image decoding
 * </p>
 */
public class ImageUtil {

    public static String THUMBNAIL_EXTENSION = "_THUMBNAIL";
    public static int THUMBNAIL_SIZE = 200;

    // TODO: this does not seem to work properly, fix it
    /**
     * Decode the bytes into bitmap based on the required size.
     *
     * @param data      image data
     * @param reqWidth  required width
     * @param reqHeight required height
     * @return bitmap that is decoded.
     */
    public static Bitmap decodeBitmapWithScaling(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // for getting the outWidth and outHeight
        int imgWidth = options.outWidth;
        int imgHeight = options.outHeight;

        // sub-sampling if necessary
        int inSampleSize = 1;

        if (imgHeight > reqHeight || imgWidth > reqWidth) {
            final int halfHeight = imgHeight / 2;
            final int halfWidth = imgWidth / 2;
            while ((halfWidth / inSampleSize) >= reqWidth || (halfHeight / inSampleSize) >= reqHeight) {
                // always set to the power of 2
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Decode to bytes to bitmap without any downscaling
     *
     * @param data image data
     * @return decoded bitmap
     */
    public static Bitmap decodeBitmap(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Convert a Bitmap to byte[]
     *
     * @param bitmap
     */
    public static byte[] toBytes(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getAllocationByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }
}
