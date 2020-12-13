package com.randomlychosenbytes.jlocker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * The SecurityManager class handles everything regarding encryption and
 * decryption.
 */
final public class Utils {

    private static final String cryptoAlgorithmName = "DES";

    private static byte[] getUtf8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Retuns a MD5 hash to a given array of bytes.
     */
    public static String getHash(String pw) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] bytes = getUtf8Bytes(pw);
            m.update(bytes, 0, bytes.length);

            return new BigInteger(1, m.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static String bytesToBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] base64StringToBytes(String str) {
        return Base64.getDecoder().decode(str);
    }

    public static String encrypt(String s, SecretKey key) {
        try {
            Cipher ecipher = Cipher.getInstance(cryptoAlgorithmName);
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            return bytesToBase64String(ecipher.doFinal(getUtf8Bytes(s)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String decrypt(String base64, SecretKey key) {
        try {
            Cipher dcipher = Cipher.getInstance(cryptoAlgorithmName);
            dcipher.init(Cipher.DECRYPT_MODE, key);
            return new String(dcipher.doFinal(base64StringToBytes(base64)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Unseals the buildings object. This can't be done in the
     * loadFromCustomFile method, because the data is loaded before the password
     * was entered.
     */
    public static List<Building> unsealAndDeserializeBuildings(String encryptedBuildingsBase64, SecretKey key) throws Exception {
        String json = decrypt(encryptedBuildingsBase64, key);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return gson.fromJson(json, new TypeToken<List<Building>>() {
        }.getType());
    }

    public static String generateAndEncryptKey(String pw) {
        try {
            return encryptKeyWithString(KeyGenerator.getInstance(cryptoAlgorithmName).generateKey(), pw);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static String encryptKeyWithString(SecretKey key, String pw) {
        try {
            Cipher ecipher = Cipher.getInstance(cryptoAlgorithmName);

            DESKeySpec desKeySpec = new DESKeySpec(getUtf8Bytes(pw));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoAlgorithmName);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return bytesToBase64String(ecipher.doFinal(key.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SecretKey decryptKeyWithString(String encKeyBase64, String pw) { // Key is saved as string
        try {
            Cipher dcipher = Cipher.getInstance(cryptoAlgorithmName);

            DESKeySpec desKeySpec = new DESKeySpec(getUtf8Bytes(pw));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cryptoAlgorithmName);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new SecretKeySpec(dcipher.doFinal(base64StringToBytes(encKeyBase64)), cryptoAlgorithmName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static Calendar getCalendarFromString(String dateStr) {

        if (dateStr.length() < 10) {
            return null;
        }

        try {
            int day = Integer.parseInt(dateStr.substring(0, 2));
            int month = Integer.parseInt(dateStr.substring(3, 5)) - 1;
            int year = Integer.parseInt(dateStr.substring(6, 10));

            Calendar calendar = new GregorianCalendar(year, month, day);
            calendar.setLenient(false);
            calendar.getTime();

            return calendar;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isDateValid(String dateStr) {
        return getCalendarFromString(dateStr) != null;
    }

    public static long getDifferenceInMonths(Calendar start, Calendar end) {
        return Math.round(((double) end.getTimeInMillis() - start.getTimeInMillis()) / 2592000000.0); // 2592000000.0 = 24 * 60 * 60 * 1000 * 30
    }
}
