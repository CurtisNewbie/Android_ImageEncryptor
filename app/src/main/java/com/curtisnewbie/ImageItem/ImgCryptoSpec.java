package com.curtisnewbie.ImageItem;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Specification of the encryption and decryption
 */
public abstract class ImgCryptoSpec {

    protected static final String ALGORITHM = "PBEWithMD5AndTripleDES";
    protected static final int PARAM_ITERATION_COUNT = 100;
    protected static final int SALT_SIZE = 8;

    protected byte[] salt;


}
