package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import static com.randomlychosenbytes.jlocker.manager.Utils.generateAndEncryptKey;

public class SuperUser extends User {

    @Expose
    private String encSuperUMasterKeyBase64;

    public String getEncryptedSuperUMasterKeyBase64() {
        return encSuperUMasterKeyBase64;
    }

    public SuperUser(String password) {
        super(password, generateAndEncryptKey(password));
        this.encSuperUMasterKeyBase64 = generateAndEncryptKey(password);
    }
}
