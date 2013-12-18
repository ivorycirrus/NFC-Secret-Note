
package com.lgcns.nfc.secretnote.util;

import java.nio.charset.Charset;

/**
 * Data convert utility
 * 1. String <--> byte[]
 * 2. integer <--> byte[]
 */
public class ByteUtils {
    
    // Convert String to byte array with UTF-8 encoding
    public static byte[] StringToByteArray(String input) {
        if (input == null || input.equals("")) return new byte[] {};
        else return input.getBytes(Charset.forName("utf-8"));
    }

    // Convert byte array to String with UTF-8 encoding
    public static String ByteArrayToString(byte[] input) {
        if(input==null) return null;
        else  return new String(input, Charset.forName("utf-8"));
    }
    
   // Convert integer to byte array
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value >>> 24), 
                (byte) (value >>> 16),
                (byte) (value >>> 8), 
                (byte) value
        };
    }

    // Convert byte array to integer
    public static final int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + 
               ((b[1] & 0xFF) << 16) + 
               ((b[2] & 0xFF) << 8) + 
               (b[3] & 0xFF);
    }
}
