package com.curtisnewbie.util;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Util Class for encryption, decryption, hashing, etc.
 */
public class CryptoUtil {
    /*
     * Specification of the encryption and decryption
     */
    private static final String HASHING_ALGORITHM = "SHA-256";
    private static final String ENCRYPTION_STANDARD = "AES";
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SALT_LEN = 4;

    /**
     * Hash text with salt
     *
     * @param str  text
     * @param salt salt
     * @return hash in {@code byte[]} or {@code NULL} if error happens
     */
    public static byte[] hash(String str, String salt) {
        try {
            // append salt to password
            String cred = str + salt;
            byte[] credBytes = cred.getBytes("UTF-8");

            // digest the credBytes
            MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);
            md.update(credBytes, 0, credBytes.length);
            byte[] hashedCred = md.digest();
            return hashedCred;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate random salt
     *
     * @param len length of salt
     * @return salt
     */
    private static String randSalt(int len) {
        SecureRandom sr = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(sr.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Generate random salt of the default length
     *
     * @return salt
     * @see {@link CryptoUtil#SALT_LEN}
     */
    public static String randSalt() {
        return randSalt(SALT_LEN);
    }

    /**
     * create the a {@code SecretKeySpec} for the specified {@code ENCRYPTION_STANDARD} through
     * hashing given password using the specified {@code HASHING_ALGORITHM}
     *
     * @param pw password
     * @return SecretKeySpec
     * @see {@link CryptoUtil#HASHING_ALGORITHM}
     * @see {@link CryptoUtil#ENCRYPTION_STANDARD}
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt using specified {@code HASHING_ALGORITHM} and {@code ENCRYPTION_STANDARD}
     *
     * @param data encryptedData
     * @param pw   password
     * @return original data byte[]
     * @see {@link CryptoUtil#HASHING_ALGORITHM}
     * @see {@link CryptoUtil#ENCRYPTION_STANDARD}
     */
    public static byte[] decrypt(byte[] data, String pw) {
        SecretKeySpec keySpec = createKey(pw);
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_STANDARD);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedData = cipher.doFinal(data);
            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypt using SHA-256 & AES
     *
     * @param data image data
     * @param pw   password
     * @return encrypted byte[]
     */
    public static byte[] encrypt(byte[] data, String pw) {
        SecretKeySpec keySpec = createKey(pw);
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_STANDARD);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedData = cipher.doFinal(data);
            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
