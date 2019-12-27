package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.SecurityManager;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

/**
 * Represents a User of the program. There are two different kinds a the moment
 * a restricted user and a super user. The super user can do everything the
 * restricted user can, plus he can view/edit the locker codes.
 */
public class User {

    @Expose
    private String sName;

    @Expose
    private String sHash;

    @Expose
    private boolean isSuperUser;

    @Expose
    private byte[] encUserMasterKey;

    @Expose
    private byte[] encSuperUMasterKey;

    // transient variables don't get serialized!
    transient private static SecretKey decUserMasterKey = null; // no static, no initialization, add transient
    transient private SecretKey decSuperUMasterKey;
    transient private String decUserPW;

    public User() {
    }

    public User(String name, String password, SecretKey ukey) {
        sName = name;
        decUserPW = password;
        isSuperUser = false;
        sHash = SecurityManager.getHash(password.getBytes()); // MD5 hash

        decUserMasterKey = ukey;
        encUserMasterKey = encryptKeyWithString(decUserMasterKey);

        decSuperUMasterKey = null;
        encSuperUMasterKey = null;
    }

    public User(String name, String password) {
        sName = name;
        decUserPW = password;
        isSuperUser = true;
        sHash = SecurityManager.getHash(password.getBytes()); // MD5 hash

        //
        // Generate master key(s)
        //

        try {
            // everyone has at least this password.
            // it's used to encrypt/decrypt the buildings object
            decUserMasterKey = KeyGenerator.getInstance("DES").generateKey();
            encUserMasterKey = encryptKeyWithString(decUserMasterKey);

            // only super users have this variables initialized
            // this key is used to encrypt/decrypt the locker codes
            decSuperUMasterKey = KeyGenerator.getInstance("DES").generateKey();
            encSuperUMasterKey = encryptKeyWithString(decSuperUMasterKey);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("*** Executing User Constructor... failed");
        }
    }

    public boolean isPasswordCorrect(String pw) {

        if (!SecurityManager.getHash(pw.getBytes()).equals(sHash)) {
            return false;
        }
        decUserPW = pw;

        // decrypt master keys
        decUserMasterKey = decryptKeyWithString(encUserMasterKey);

        if (isSuperUser) {
            decSuperUMasterKey = decryptKeyWithString(encSuperUMasterKey);
        }

        return true;
    }

    private byte[] encryptKeyWithString(SecretKey key) {
        try {
            Cipher ecipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(decUserPW.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return ecipher.doFinal(key.getEncoded());
        } catch (Exception e) {
            System.err.println("* User.EncryptKeyWithString()... failed");
        }

        return null;
    }

    private SecretKey decryptKeyWithString(byte[] enc_key) { // Key is saved as string
        try {
            Cipher dcipher = Cipher.getInstance("DES");

            DESKeySpec desKeySpec = new DESKeySpec(decUserPW.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            dcipher.init(Cipher.DECRYPT_MODE, secretKey);

            return new SecretKeySpec(dcipher.doFinal(enc_key), "DES");
        } catch (Exception e) {
            System.err.println("* User.DecryptKeyWithString()... failed");
        }

        return null;
    }

    public void setName(String newname) {
        sName = newname;
    }

    public String getName() {
        return sName;
    }

    public String getUserPW() {
        return decUserPW;
    }

    public byte[] getEncUserMasterKey() {
        return encUserMasterKey;
    }

    public byte[] getEncSuperUMasterKey() {
        return encSuperUMasterKey;
    }

    public SecretKey getUserMasterKey() {
        return decUserMasterKey;
    }

    public SecretKey getSuperUMasterKey() {
        return decSuperUMasterKey;
    }

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public String getSHash() {
        return sHash;
    }

    public void setCurrentUserPW(String pw) {
        decUserPW = pw;
    }

    public void setSuperUser(boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
    }

    public void setSHash(String sHash) {
        this.sHash = sHash;
    }

    public void setEncSuperUMasterKey(byte[] encSuperUMasterKey) {
        this.encSuperUMasterKey = encSuperUMasterKey;
    }

    public void setEncUserMasterKey(byte[] encUserMasterKey) {
        this.encUserMasterKey = encUserMasterKey;
    }
}
