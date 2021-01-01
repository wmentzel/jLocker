package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.generateAndEncryptKey

class SuperUser(password: String) : User(
    password, generateAndEncryptKey(password)
) {
    @Expose
    val encryptedSuperUMasterKeyBase64: String = generateAndEncryptKey(password)
}