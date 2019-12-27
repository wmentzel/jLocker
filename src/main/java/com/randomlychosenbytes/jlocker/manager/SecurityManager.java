package com.randomlychosenbytes.jlocker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static SealedObject encryptObject(Serializable s, SecretKey key) throws Exception {
        Cipher ecipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        return new SealedObject(s, ecipher);
    }

    private static String decryptObject(SealedObject so, SecretKey key) throws Exception {
        Cipher dcipher = Cipher.getInstance("DES");
        dcipher.init(Cipher.DECRYPT_MODE, key);
        return (String) so.getObject(dcipher);
    }

    public static byte[] encrypt(String s, SecretKey key) throws Exception {
        Cipher ecipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        return ecipher.doFinal(s.getBytes("UTF8"));
    }

    private static String decrypt(byte[] bytes, SecretKey key) throws Exception {
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
}
