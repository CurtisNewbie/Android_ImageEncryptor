package com.curtisnewbie.util;

import java.security.MessageDigest;
import java.util.Random;

/**
 * Util Class for hashing
 */
public class CryptoUtil {
    public static final String HASHING_ALGORITHM = "SHA-256";

    /**
     * Hash password with salt
     *
     * @param pw   password
     * @param salt salt
     * @return hash in {@code byte[]} or {@code NULL} if error happens
     */
    public static byte[] hash(String pw, String salt) {
        try {
            // append salt to password
            String cred = pw + salt;
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
    public static String randSalt(int len) {
        // TODO: fix base, its' for temporary use and Random (Use SecureRandom probably)
        String base = "abcdefghijklmnopqrstuvwxyz";
        Random rd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(rd.nextInt(base.length()));
        }
        return sb.toString();
    }
}
