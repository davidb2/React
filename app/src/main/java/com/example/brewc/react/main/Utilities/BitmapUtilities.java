package com.example.brewc.react.main.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Encoding and decoding photos
 * Credit given to the people of stackoverflow.com
 */

public class BitmapUtilities {

    /**
     * Converts a Bitmap image to a base64 string
     * @param bitmap the image
     * @return the encoded string
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * Converts a base4 encoded string to a Bitmap image
     * @param encodedImage base64 encoded string
     * @return the bitmap image
     */
    public static Bitmap base64ToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
