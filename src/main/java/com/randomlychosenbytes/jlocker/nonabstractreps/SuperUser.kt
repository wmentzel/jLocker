package com.randomlychosenbytes.jlocker.nonabstractreps

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.manager.generateAndEncryptKey

class SuperUser(password: String) : User(
    password, generateAndEncryptKey(password)
) {
    @Expose
    val encryptedSuperUMasterKeyBase64: String = generateAndEncryptKey(password)
}