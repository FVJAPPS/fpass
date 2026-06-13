package com.fvjapps.fpass.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageCompressor {

    private static final int MAX_DIM = 512;

    public static byte[] compress(byte[] imageBytes) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);

        int width = opts.outWidth;
        int height = opts.outHeight;

        if (width <= MAX_DIM && height <= MAX_DIM) {
            return imageBytes;
        }

        int larger = Math.max(width, height);
        float divisor = (float) larger / MAX_DIM;
        int newWidth = Math.round(width / divisor);
        int newHeight = Math.round(height / divisor);

        opts.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
        if (src == null) return imageBytes;

        Bitmap scaled = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (scaled != src) src.recycle();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        scaled.recycle();

        return baos.toByteArray();
    }
}
