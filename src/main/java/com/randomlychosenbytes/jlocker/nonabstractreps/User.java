package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.Utils;

/**
 * Represents a User of the program. There are two different kinds a the moment
 * a restricted user and a super user. The super user can do everything the
 * restricted user can, plus he can view/edit the locker codes.
 */
public abstract class User {

    @Expose
    private String passwordHash;

    @Expose
    private String encryptedUserMasterKeyBase64;

    public User(String passwordHash, String encryptedUserMasterKeyBase64) {
        this.passwordHash = passwordHash;
    }

    public String getEncryptedUserMasterKeyBase64() {
        return encryptedUserMasterKeyBase64;
    }

    public boolean isPasswordCorrect(String pw) {

        if (!Utils.getHash(pw).equals(passwordHash)) {
            return false;
        }

        return true;
    }
}
