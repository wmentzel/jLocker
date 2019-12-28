package com.randomlychosenbytes.jlocker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * The SecurityManager class handles everything regarding encryption and
 * decryption.
 */
final public class SecurityManager {
    /**
     * Retuns a MD5 hash to a given array of bytes.
     */
    public static String getHash(byte[] pw) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(pw, 0, pw.length);

            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SecurityManager.getHash  failed!");
        }

        return "";
    }

    public static String bytesToBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] base64StringToBytes(String str) {
        return Base64.getDecoder().decode(str);
    }

    public static byte[] encrypt(String s, SecretKey key) throws Exception {
        Cipher ecipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        return ecipher.doFinal(s.getBytes("UTF8"));
    }

    public static String decrypt(byte[] bytes, SecretKey key) throws Exception {
        Cipher dcipher = Cipher.getInstance("DES");
        dcipher.init(Cipher.DECRYPT_MODE, key);
        return new String(dcipher.doFinal(bytes), "UTF8");
    }

    /**
     * Unseals the buildings object. This can't be done in the
     * loadFromCustomFile method, because the data is loaded before the password
     * was entered.
     */
    public static List<Building> unsealAndDeserializeBuildings(byte[] sealedBuildingsObject, SecretKey key) throws Exception {
        String json = decrypt(sealedBuildingsObject, key);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return gson.fromJson(json, new TypeToken<List<Building>>() {
        }.getType());
    }

    public static byte[] encryptKeyWithString(SecretKey key, String pw) {
        try {
            Cipher ecipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(pw.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return ecipher.doFinal(key.getEncoded());
        } catch (Exception e) {
            System.err.println("* User.EncryptKeyWithString()... failed");
        }

        return null;
    }

    public static SecretKey decryptKeyWithString(byte[] enc_key, String pw) { // Key is saved as string
        try {
            Cipher dcipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(pw.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new SecretKeySpec(dcipher.doFinal(enc_key), "DES");
        } catch (Exception e) {
            System.err.println("* User.DecryptKeyWithString()... failed");
        }

        return null;
    }
}
