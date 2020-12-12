package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.Utils;

import javax.crypto.SecretKey;

public class SuperUser extends User {

    @Expose
    protected String encSuperUMasterKeyBase64;

    public SuperUser(
            String password,
            String hash,
            String encUserMasterKeyBase64,
            String encSuperUMasterKeyBase64
    ) {
        super(password);

        this.passwordHash = hash;
        this.encSuperUMasterKeyBase64 = encSuperUMasterKeyBase64;
        this.encryptedUserMasterKeyBase64 = encUserMasterKeyBase64;
    }

    public SecretKey getSuperUMasterKeyBase64() {
        return Utils.decryptKeyWithString(encSuperUMasterKeyBase64, decryptedUserPassword);
    }
}
