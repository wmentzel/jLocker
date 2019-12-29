package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.randomlychosenbytes.jlocker.manager.Utils;

import javax.crypto.SecretKey;

public class RestrictedUser extends User {

    public RestrictedUser(String password, SecretKey ukey) {
        super(password);

        sHash = Utils.getHash(password); // MD5 hash
        encUserMasterKeyBase64 = Utils.encryptKeyWithString(ukey, password);
    }
}
