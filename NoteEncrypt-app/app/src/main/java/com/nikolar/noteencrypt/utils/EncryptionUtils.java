package com.nikolar.noteencrypt.utils;

import android.content.Context;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static Cipher cipher;
    private static EncryptionUtils instance;
    private static Logger logger = Logger.getLogger("EncryptionUtils");
    private Key key;

    private EncryptionUtils(Key key) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.key = key;
        cipher = Cipher.getInstance(TRANSFORMATION);
    }
    public static boolean setPassword(Context context, String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (instance == null) {
            instance = new EncryptionUtils(getPasswordBasedKey(FileUtils.getSalt(context), 256, password.toCharArray()));
            byte[] encryptedSalt = FileUtils.getEncryptedSalt(context);
            byte[] salt = FileUtils.getSalt(context);
            if (Arrays.equals(Arrays.copyOf(encryptedSalt, 96), Arrays.copyOf(salt,96)))
                return true;
            logger.info(Arrays.toString(encryptedSalt) + "\n" + Arrays.toString(salt));
            instance = null;
            return false;
        }
        throw new IllegalStateException("Password was already set");
    }

    public static EncryptionUtils getInstance(){
        if (instance != null)
            return instance;
        throw new IllegalStateException("Password wasn't set");
    }
    private static Key getPasswordBasedKey(byte[] salt, int keySize, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, 1000, keySize);
        SecretKey pbeKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(pbeKeySpec);
        return new SecretKeySpec(pbeKey.getEncoded(), ALGORITHM);
    }

    public Cipher getCipher(){
        return cipher;
    }

    public Key getKey(){
        return key;
    }
}
