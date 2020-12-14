package com.randomlychosenbytes.jlocker.nonabstractreps

import com.randomlychosenbytes.jlocker.manager.encryptKeyWithString
import javax.crypto.SecretKey

class RestrictedUser(password: String, key: SecretKey) : User(
    password, encryptKeyWithString(key, password)
)