package com.randomlychosenbytes.jlocker.nonabstractreps;

import javax.crypto.SecretKey;

import static com.randomlychosenbytes.jlocker.manager.UtilsKt.encryptKeyWithString;

public class RestrictedUser extends User {

    public RestrictedUser(String password, SecretKey key) {
        super(password, encryptKeyWithString(key, password));
    }
}
