package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.Utils;

import javax.crypto.SecretKey;

import static com.randomlychosenbytes.jlocker.manager.Utils.generateAndEncryptKey;

public class SuperUser extends User {

    @Expose
    protected String encSuperUMasterKeyBase64;

    public SuperUser(String password) {
        super(password);
        this.passwordHash = Utils.getHash(password);
        this.encSuperUMasterKeyBase64 = generateAndEncryptKey(password);
        this.encryptedUserMasterKeyBase64 = generateAndEncryptKey(password);
    }

    public SecretKey getSuperUMasterKeyBase64() {
        return Utils.decryptKeyWithString(encSuperUMasterKeyBase64, decryptedUserPassword);
    }
}
