package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.encryptKeyWithString
import com.randomlychosenbytes.jlocker.generateKey

class SuperUser(password: String) : User(
    password, encryptKeyWithString(generateKey(), password)
) {
    @Expose
    val encryptedSuperUMasterKeyBase64: String = encryptKeyWithString(generateKey(), password)
}