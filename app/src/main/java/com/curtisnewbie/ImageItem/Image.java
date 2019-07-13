package com.curtisnewbie.ImageItem;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


public class Image extends ImgCryptoSpec implements Decryptable {

    private byte[] encryptedData;
    private String name;

    public Image(byte[] data, String name) {
        this.salt = new byte[ImgCryptoSpec.SALT_SIZE];
        this.encryptedData = data;
        this.name = name;

    }

    public String getName() {
        return this.name;
    }

    /**
     * @param pw password
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    @Override
    public byte[] decrypt(char[] pw) throws IOException {
        // inputStream for reading encrypted data
        InputStream byteIn = new ByteArrayInputStream(encryptedData);
        try {
            // read salt
            byteIn.read(salt);

            // read encrypted data
            int sizeOfData = encryptedData.length - salt.length;
            byte[] data = new byte[sizeOfData];
            byteIn.read(data);

            // get the cipher for decryption
            Cipher cipher = prepDecryption(pw, salt);
            if (cipher != null) {
                byte[] decryptedData = cipher.doFinal(data);
                return decryptedData;
            }
        } catch (IOException e) {
            throw new IOException();

        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        } finally {
            byteIn.close();
        }
        return null;
    }

    public Cipher prepDecryption(char[] pw, byte[] salt) {
        Cipher cipher;
        PBEParameterSpec pbeParam;
        SecretKey key;

        // Setup SecretKey
        try {
            key = createKey(pw);

            // Create PBE parameter specification
            pbeParam = new PBEParameterSpec(salt, ImgCryptoSpec.PARAM_ITERATION_COUNT);

            // Create cipher for decryption
            cipher = Cipher.getInstance(ImgCryptoSpec.ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParam);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (InvalidKeySpecException e) {
            return null;
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        }


    }


    private SecretKey createKey(char[] pw) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Generate PBE key object.
        PBEKeySpec pbeKeySpec = new PBEKeySpec(pw);

        // Create secret Key factory based on the specified algorithm

        SecretKeyFactory skFactory = SecretKeyFactory.getInstance(ImgCryptoSpec.ALGORITHM);

        // Generate the key based on the given password
        return skFactory.generateSecret(pbeKeySpec);
    }


}
