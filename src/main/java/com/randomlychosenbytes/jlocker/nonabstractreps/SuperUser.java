package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.manager.SecurityManager;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static com.randomlychosenbytes.jlocker.manager.SecurityManager.generateAndEncryptKey;

public class SuperUser extends User {

    @Expose
    protected String encSuperUMasterKeyBase64;

    public SuperUser(String password) {
        super(password);
        sHash = SecurityManager.getHash(password.getBytes()); // MD5 hash

        //
        // Generate master key(s)
        //

        try {
            // everyone has at least this password.
            // it's used to encrypt/decrypt the buildings object
            encUserMasterKeyBase64 = generateAndEncryptKey(decUserPW);

            // only super users have this variables initialized
            // this key is used to encrypt/decrypt the locker codes
            encSuperUMasterKeyBase64 = generateAndEncryptKey(decUserPW);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("*** Executing User Constructor... failed");
        }
    }

    public SecretKey getSuperUMasterKey() {
        return SecurityManager.decryptKeyWithString(encSuperUMasterKeyBase64, decUserPW);
    }
}
