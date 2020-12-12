package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.Utils;

import javax.crypto.SecretKey;

/**
 * Represents a User of the program. There are two different kinds a the moment
 * a restricted user and a super user. The super user can do everything the
 * restricted user can, plus he can view/edit the locker codes.
 */
public abstract class User {

    @Expose
    protected String passwordHash;

    @Expose
    protected String encryptedUserMasterKeyBase64;

    public String getDecryptedUserPassword() {
        return decryptedUserPassword;
    }

    public void setDecryptedUserPassword(String decryptedUserPassword) {
        this.decryptedUserPassword = decryptedUserPassword;
    }

    transient protected String decryptedUserPassword;

    public User(String password) {
        decryptedUserPassword = password;
    }

    public boolean isPasswordCorrect(String pw) {

        if (!Utils.getHash(pw).equals(passwordHash)) {
            return false;
        }

        decryptedUserPassword = pw;

        return true;
    }

    public SecretKey getUserMasterKey() {
        return Utils.decryptKeyWithString(encryptedUserMasterKeyBase64, decryptedUserPassword);
    }
}
