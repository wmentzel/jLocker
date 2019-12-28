package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.SecurityManager;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static com.randomlychosenbytes.jlocker.manager.SecurityManager.encryptKeyWithString;

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
    private String encUserMasterKeyBase64;

    @Expose
    private String encSuperUMasterKeyBase64;

    public String getDecUserPW() {
        return decUserPW;
    }

    public void setDecUserPW(String decUserPW) {
        this.decUserPW = decUserPW;
    }

    transient private String decUserPW;

    public User(String name, String password, SecretKey ukey) {
        sName = name;
        decUserPW = password;
        isSuperUser = false;
        sHash = SecurityManager.getHash(password.getBytes()); // MD5 hash

        encUserMasterKeyBase64 = SecurityManager.encryptKeyWithString(ukey, password);
        encSuperUMasterKeyBase64 = null;
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
            encUserMasterKeyBase64 = encryptKeyWithString(KeyGenerator.getInstance("DES").generateKey(), decUserPW);

            // only super users have this variables initialized
            // this key is used to encrypt/decrypt the locker codes
            encSuperUMasterKeyBase64 = encryptKeyWithString(KeyGenerator.getInstance("DES").generateKey(), decUserPW);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("*** Executing User Constructor... failed");
        }
    }

    public boolean isPasswordCorrect(String pw) {

        if (!SecurityManager.getHash(pw.getBytes()).equals(sHash)) {
            return false;
        }

        decUserPW = pw;

        return true;
    }

    public void setName(String newname) {
        sName = newname;
    }

    public String getName() {
        return sName;
    }

    public SecretKey getUserMasterKey() {
        return SecurityManager.decryptKeyWithString(encUserMasterKeyBase64, decUserPW);
    }

    public SecretKey getSuperUMasterKey() {
        return SecurityManager.decryptKeyWithString(encSuperUMasterKeyBase64, decUserPW);
    }

    public boolean isSuperUser() {
        return isSuperUser;
    }
}
