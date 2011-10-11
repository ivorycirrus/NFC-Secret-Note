
package com.lgcns.nfc.secretnote.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

/**
 *  Image convert utility
 *  1. Bitmap <--> byte[]
 */
public class BitmapUtils {

    public static byte[] bitmapToByteArray(Bitmap $bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            $bitmap.compress(CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap byteArrayToBitmap(byte[] $byteArray) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray($byteArray, 0, $byteArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
