package com.curtisnewbie.ImgCrypto;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Image {

    /*
     * Specification of the encryption and decryption
     */
    protected static final String HASHING_ALGORITHM = "SHA-256";
    protected static final String ENCRYPTION_STANDARD = "AES";

    public static byte[] encrypt(byte[] data, String pw) {
        SecretKeySpec keySpec = createKey(pw);
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_STANDARD);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedData = cipher.doFinal(data);
            return encryptedData;
        } catch (NoSuchAlgorithmException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (InvalidKeyException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (BadPaddingException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        }
        return null;
    }

    public static byte[] decrypt (byte[] data, String pw) {
        SecretKeySpec keySpec = createKey(pw);
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_STANDARD);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedData = cipher.doFinal(data);
            return decryptedData;
        } catch (NoSuchAlgorithmException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (InvalidKeyException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (BadPaddingException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Log.w("encrypt", e.toString() + ":" + e.getMessage());
        }
        return null;
    }

    /**
     * get the secretKeySpec through one-way hashing and encryption Algorithm (SHA-256/AES)
     *
     * @param pw password
     * @return SecretKeySpec
     */
    private static SecretKeySpec createKey(String pw) {

        try {
            byte[] pwBytes = pw.getBytes("UTF-8");

            // Create secret Key factory based on the specified algorithm
            MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);

            // digest the pwBytes to be a new key
            md.update(pwBytes, 0, pwBytes.length);
            byte[] key = md.digest();
            return new SecretKeySpec(key, ENCRYPTION_STANDARD);
        } catch (NoSuchAlgorithmException e) {
            Log.w("createKey", e.toString() + ":" + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.w("createKey", e.toString() + ":" + e.getMessage());
        }
        return null;
    }


}
