package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import static com.randomlychosenbytes.jlocker.manager.UtilsKt.getHash;

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

    public User(String password, String encryptedUserMasterKeyBase64) {
        this.passwordHash = getHash(password);
        this.encryptedUserMasterKeyBase64 = encryptedUserMasterKeyBase64;
    }

    public String getEncryptedUserMasterKeyBase64() {
        return encryptedUserMasterKeyBase64;
    }

    public boolean isPasswordCorrect(String pw) {

        if (!getHash(pw).equals(passwordHash)) {
            return false;
        }

        return true;
    }
}
