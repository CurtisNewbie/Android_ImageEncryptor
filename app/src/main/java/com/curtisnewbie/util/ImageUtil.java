package com.curtisnewbie.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        while ((imgWidth / inSampleSize) > reqWidth || (imgHeight / inSampleSize) > reqHeight) {
            // always set to the power of 2
            inSampleSize = inSampleSize == 1 ? 2 : inSampleSize * 2;
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
    public static Bitmap decode(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }
}
