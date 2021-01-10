package com.randomlychosenbytes.jlocker.model

import com.randomlychosenbytes.jlocker.utils.encryptKeyWithString
import javax.crypto.SecretKey

class RestrictedUser(password: String, key: SecretKey) : User(
    password, encryptKeyWithString(key, password)
)