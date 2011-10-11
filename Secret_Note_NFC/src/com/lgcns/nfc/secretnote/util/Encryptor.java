
package com.lgcns.nfc.secretnote.util;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Encrypt and decrypt data if password is exist.
 */
public class Encryptor {

    public static byte[] encrypt(String origin, String password) {
        if (password == null || password.equals("")) {
            return ByteUtils.StringToByteArray(origin);
        } else {
            byte[] input = ByteUtils.StringToByteArray(origin);
            byte[] pw = ByteUtils.StringToByteArray(password);
            return encrypt(input, pw);
        }
    }

    public static byte[] encrypt(byte[] origin, byte[] password) {
        if(password==null || password.length==0) return origin;
        byte[] encrypt = Arrays.copyOf(origin, origin.length);
        for (int inx = 0; inx < encrypt.length; inx++) {
            if (inx > 0) encrypt[inx] ^= encrypt[inx - 1];
            encrypt[inx] = (byte) (origin[inx] ^ password[inx % password.length]);
        }
        return encrypt;
    }

    public static String decrypt(byte[] encrypted, String password) {
        if (password == null || password.equals("")) {
            return ByteUtils.ByteArrayToString(encrypted);
        } else {
            byte[] pw = password.getBytes(Charset.forName("utf-8"));
            return ByteUtils.ByteArrayToString(decrypt(encrypted, pw));
        }
    }

    public static byte[] decrypt(byte[] encrypted, byte[] password) {
        if(password==null || password.length==0) return encrypted;
        byte[] decrypt = Arrays.copyOf(encrypted, encrypted.length);
        for (int jnx = 0; jnx < encrypted.length; jnx++) {
            if (jnx > 0) decrypt[jnx] ^= encrypted[jnx - 1];
            decrypt[jnx] = (byte) (encrypted[jnx] ^ password[jnx % password.length]);
        }
        return decrypt;
    }

}