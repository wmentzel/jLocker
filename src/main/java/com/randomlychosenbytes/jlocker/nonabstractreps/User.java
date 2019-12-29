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
    protected String sHash;

    @Expose
    protected String encUserMasterKeyBase64;

    public String getDecUserPW() {
        return decUserPW;
    }

    public void setDecUserPW(String decUserPW) {
        this.decUserPW = decUserPW;
    }

    transient protected String decUserPW;

    public User(String password) {
        decUserPW = password;
    }

    public boolean isPasswordCorrect(String pw) {

        if (!Utils.getHash(pw).equals(sHash)) {
            return false;
        }

        decUserPW = pw;

        return true;
    }

    public SecretKey getUserMasterKey() {
        return Utils.decryptKeyWithString(encUserMasterKeyBase64, decUserPW);
    }
}
