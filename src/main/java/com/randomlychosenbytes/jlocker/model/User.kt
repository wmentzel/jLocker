package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import com.randomlychosenbytes.jlocker.utils.getHash

/**
 * Represents a User of the program. There are two different kinds a the moment
 * a restricted user and a super user. The super user can do everything the
 * restricted user can, plus he can view/edit the locker codes.
 */
abstract class User(password: String, encryptedUserMasterKeyBase64: String) {
    @Expose
    private val passwordHash: String = getHash(password)

    @Expose
    val encryptedUserMasterKeyBase64: String = encryptedUserMasterKeyBase64

    fun isPasswordCorrect(pw: String): Boolean {
        return getHash(pw) == passwordHash
    }
}