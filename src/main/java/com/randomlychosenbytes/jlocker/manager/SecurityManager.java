package com.randomlychosenbytes.jlocker.manager;

import com.randomlychosenbytes.jlocker.nonabstractreps.Building;

import javax.crypto.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * The SecurityManager class handles everything regarding encryption and
 * decryption.
 *
 * @author Willi
 * @version latest
 */
public class SecurityManager {
    /**
     * @param f
     * @param ukey
     * @return
     * @deprecated
     */
    public ObjectOutputStream getEncryptOOS(File f, SecretKey ukey) {
        System.out.print("* encrypt " + f.getName() + "... ");

        ObjectOutputStream oos = null;

        try {
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.ENCRYPT_MODE, ukey);

            FileOutputStream fos = new FileOutputStream(f);
            CipherOutputStream cos = new CipherOutputStream(fos, desCipher);
            oos = new ObjectOutputStream(cos);

            System.out.println(" successful!");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            System.err.println(" failed!");
        }
        return oos;
    }

    /**
     * @param f
     * @param ukey
     * @return
     * @deprecated
     */
    public ObjectInputStream getDecryptOIS(File f, SecretKey ukey) {
        System.out.print("* decrypt " + f.getName() + "... ");

        ObjectInputStream ois = null;
        try {
            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.DECRYPT_MODE, ukey);

            FileInputStream fis = new FileInputStream(f);
            CipherInputStream cis = new CipherInputStream(fis, desCipher);
            ois = new ObjectInputStream(cis);

            System.out.println(" successful!");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            System.err.println(" failed!");
        }

        return ois;
    }

    /**
     * Retuns a MD5 hash to a given array of bytes.
     *
     * @param pw
     * @return
     */
    public String getHash(byte[] pw) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(pw, 0, pw.length);

            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SecurityManager.getHash  failed!");
        }

        return "";
    }

    /**
     * @deprecated found at: http://www.javafaq.nu/java-article236.html
     */
    private byte[] getObjectBytes(Object obj) throws java.io.IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        byte[] data = bos.toByteArray();
        return data;
    }

    /**
     * TODO utilisize to see if changes have been made before closing the
     * application
     *
     * @param obj
     * @return
     */
    public String getObjectHash(Object obj) {
        // or serialize method
        return getHash(serializeXml(obj));
    }

    /**
     * Returns the ObjectInputStream to a given path.
     *
     * @param filename
     * @return
     */
    static public ObjectInputStream getOis(String filename) {
        Object o = null;

        try {
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return ois;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns the ObjectOutputStream to a given path.
     *
     * @param filename
     * @return
     */
    static public ObjectOutputStream getOos(String filename) {
        try {
            File f = new File(filename);
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // The encrypted master keys from the user objects remain untouchend 
            // and encrypted and can be written directly with the "users" object 
            // itself.

            return oos;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Turns an object to a series of bytes.
     *
     * @param obj
     * @return
     */
    public static byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);

            os.close();

            return out.toByteArray();
        } catch (IOException e) {
            System.err.println("SecurityManager.serialization failed");
            System.err.println(e);
        }

        return null;
    }

    /**
     * Turns a series of bytes to an object.
     * <p>
     * Found at:
     * http://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet
     *
     * @param data
     * @return
     */
    public static Object deserialize(byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            Object o = is.readObject();

            is.close();

            return o;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("SecurityMngr.deseserialize failed");
            System.err.println(e);
        }

        return null;
    }

    /**
     * http://www.exampledepot.com/egs/javax.crypto/EncryptObject.html
     *
     * @param o
     * @param key
     * @return
     */
    static public SealedObject encryptObject(byte o[], SecretKey key) {
        try {
            Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);

            return new SealedObject(o, ecipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException e) {
            System.err.println("SecurityMngr.encryptObject failed");
            System.err.println(e);
        }

        return null;
    }

    public static byte[] decryptObject(SealedObject so, SecretKey key) // Key is saved as string
    {
        try {
            Cipher dcipher = Cipher.getInstance("DES");
            dcipher.init(Cipher.DECRYPT_MODE, key);
            byte[] o = (byte[]) so.getObject(dcipher);

            return o;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("SecurityMngr.decryptObject failed");
            System.err.println(e);
        }

        return null;
    }

    /**
     * Unseals the buildings object. This can't be done in the
     * loadFromCustomFile method, because the data is loaded before the password
     * was entered.
     *
     * @param sealedBuildingsObject
     * @param key
     * @return
     */
    public static List<Building> unsealBuidingsObject(SealedObject sealedBuildingsObject, SecretKey key) {
        System.out.println("* unsealing buildings object...");

        System.out.print(" * decryption... ");

        byte b[] = SecurityManager.decryptObject(sealedBuildingsObject, key);

        if (b == null) {
            System.err.println("failed!");
        } else {
            System.out.println("successfull!");
        }

        System.out.print(" * deserialization...");

        List<Building> buildings = (LinkedList<Building>) SecurityManager.deserialize(b);

        if (buildings == null) {
            System.err.println("failed!");
            return new LinkedList<>();
        } else {
            System.out.println("successful!");
            return buildings;
        }
    }

    /**
     * This method is for the new file format coming in version 1.6
     * <p>
     * http://www.avajava.com/tutorials/lessons/how-do-i-convert-a-javabean-to-an-xml-string-using-xmlencoder.html
     *
     * @param obj
     * @return
     */
    public static byte[] serializeXml(Object obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(baos);
        xmlEncoder.writeObject(obj);

        xmlEncoder.close();

        return baos.toByteArray();
    }

    /**
     * This method is for the new file format coming in version 1.6
     *
     * @param data
     * @return
     */
    public static Object deserializeXml(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        XMLDecoder xmlDecoder = new XMLDecoder(bais);

        Object obj = xmlDecoder.readObject();

        xmlDecoder.close();

        return obj;
    }

    /**
     * This method is for the new file format coming in version 1.6
     * <p>
     * Found at:
     * http://stackoverflow.com/questions/10562908/encryption-of-strings-works-encryption-of-byte-array-type-does-not-work
     *
     * @param Data
     * @param key
     * @return
     */
    public static byte[] encryptByteArray(byte[] Data, SecretKey key) {
        try {
            Cipher c = Cipher.getInstance("DES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(Data);

            return encVal;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("SecurityMngr.encryptByteArray failed");
            System.err.println(e);
        }

        return null;
    }

    /**
     * This method is for the new file format coming in version 1.6
     * <p>
     * Found at:
     * http://stackoverflow.com/questions/10562908/encryption-of-strings-works-encryption-of-byte-array-type-does-not-work
     *
     * @param encryptedData
     * @param key
     * @return
     */
    public static byte[] decryptByteArray(byte[] encryptedData, SecretKey key) {
        try {
            Cipher c = Cipher.getInstance("DES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decValue = c.doFinal(encryptedData);

            return decValue;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("SecurityMngr.decryptByteArray failed");
            System.err.println(e);
        }

        return null;
    }
}
