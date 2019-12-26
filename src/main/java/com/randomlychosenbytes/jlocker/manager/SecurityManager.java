package com.randomlychosenbytes.jlocker.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.nonabstractreps.Building;
import com.randomlychosenbytes.jlocker.nonabstractreps.Floor;
import com.randomlychosenbytes.jlocker.nonabstractreps.Walk;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Unseals the buildings object. This can't be done in the
     * loadFromCustomFile method, because the data is loaded before the password
     * was entered.
     */
    public static List<Building> unsealAndDeserializeBuildings(SealedObject sealedBuildingsObject, SecretKey key) throws Exception {
        String json = decryptObject(sealedBuildingsObject, key);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        List<Building> buildings = gson.fromJson(json, new TypeToken<List<Building>>() {
        }.getType());

        return buildings.stream().map(b -> {
            List<Floor> floors = b.getFloors().stream().map(f -> {
                List<Walk> walks = f.getWalks().stream().map(w -> {

                    List<ManagementUnit> mus = w.getManagementUnitList().stream().map(mu -> {
                        return new ManagementUnit(mu.mType);
                    }).collect(Collectors.toList());

                    w.setMus(mus);
                    return w;
                }).collect(Collectors.toList());

                f.setWalks(walks);
                return f;
            }).collect(Collectors.toList());
            b.setFloors(floors);
            return b;
        }).collect(Collectors.toList());
    }
}
