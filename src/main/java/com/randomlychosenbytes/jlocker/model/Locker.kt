package com.randomlychosenbytes.jlocker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.randomlychosenbytes.jlocker.utils.decrypt
import com.randomlychosenbytes.jlocker.utils.encrypt
import javax.crypto.SecretKey

class Locker(
    @Expose
    var id: String,
    pupil: Pupil?,
    @Expose var lockCode: String,
    @Expose var isOutOfOrder: Boolean,
    @Expose var note: String,
    @Expose var currentCodeIndex: Int,
    encryptedCodes: Array<String>?
) {
    @SerializedName("pupil")
    @Expose
    private var _pupil: Pupil? = pupil

    val pupil get() = _pupil ?: throw IllegalStateException()

    fun moveInNewOwner(pupil: Pupil) {
        _pupil = pupil
    }

    @Expose
    var encryptedCodes: Array<String>? = encryptedCodes
        private set

    fun getCodes(sukey: SecretKey) = (0..4).map { i ->
        getCode(i, sukey)
    }.toTypedArray()

    private fun getCode(i: Int, sukey: SecretKey): String {
        if (encryptedCodes == null) {
            return "00-00-00"
        }
        val code = decrypt(encryptedCodes!![i], sukey)
        return code.substring(0, 2) + "-" + code.substring(2, 4) + "-" + code.substring(4, 6)
    }

    fun setCodes(codes: Array<String>, superUserMasterKey: SecretKey) {
        // codes is unencrypted... encrypting and saving in encCodes

        // The Value of code[i] looks like "00-00-00"
        // it is saved without the "-", so we have
        // to remove them.
        encryptedCodes = (0..4).map { i ->
            val code = codes[i].replace("-", "")
            encrypt(code, superUserMasterKey)
        }.toTypedArray()
    }

    fun empty() {
        _pupil = null
        currentCodeIndex = if (encryptedCodes == null) {
            0
        } else {
            (currentCodeIndex + 1) % encryptedCodes!!.size
        }
    }

    fun setNextCode() {
        currentCodeIndex = (currentCodeIndex + 1) % 5
    }

    val isFree: Boolean
        get() = _pupil == null

    fun getCurrentCode(sukey: SecretKey): String {
        return getCode(currentCodeIndex, sukey)
    }

    constructor(id: String) : this(
        id = id,
        pupil = null,
        currentCodeIndex = 0,
        lockCode = "",
        isOutOfOrder = false,
        note = "",
        encryptedCodes = null
    )

    constructor() : this(
        id = "",
        pupil = null,
        currentCodeIndex = 0,
        lockCode = "",
        isOutOfOrder = false,
        note = "",
        encryptedCodes = null
    )

    constructor(locker: Locker) : this(
        id = locker.id,
        pupil = if (locker.isFree) null else locker.pupil,
        currentCodeIndex = locker.currentCodeIndex,
        lockCode = locker.lockCode,
        isOutOfOrder = locker.isOutOfOrder,
        note = locker.note,
        encryptedCodes = locker.encryptedCodes
    )
}