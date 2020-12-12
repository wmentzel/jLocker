package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.randomlychosenbytes.jlocker.manager.Utils;

import javax.crypto.SecretKey;

public class RestrictedUser extends User {

    public RestrictedUser(String password, SecretKey key) {
        super(password);

        passwordHash = Utils.getHash(password); // MD5 hash
        encryptedUserMasterKeyBase64 = Utils.encryptKeyWithString(key, password);
    }
}
