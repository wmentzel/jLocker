package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.randomlychosenbytes.jlocker.manager.SecurityManager;

import javax.crypto.SecretKey;

public class RestrictedUser extends User {

    public RestrictedUser(String password, SecretKey ukey) {
        super(password);

        sHash = SecurityManager.getHash(password.getBytes()); // MD5 hash
        encUserMasterKeyBase64 = SecurityManager.encryptKeyWithString(ukey, password);
    }
}
