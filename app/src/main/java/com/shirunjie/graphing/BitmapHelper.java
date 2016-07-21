package com.shirunjie.graphing;

import android.graphics.Bitmap;

/**
 * Created by shirunjie on 2016-07-11.
 */

public class BitmapHelper {
    public static Bitmap cropBorder(Bitmap bitmap) {
        final int width  = bitmap.getWidth();
        final int height = bitmap.getHeight();
        Bitmap    cropped;
        if (width > height) {
            int diff = width - height;
            cropped = Bitmap.createBitmap(bitmap, diff / 2, 0, height, height);
        } else {
            int diff = height - width;
            cropped = Bitmap.createBitmap(bitmap, 0, diff / 2, width, width);
        }
        return cropped;
    }
}
