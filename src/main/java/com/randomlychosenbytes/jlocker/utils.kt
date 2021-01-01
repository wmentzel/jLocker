package com.randomlychosenbytes.jlocker

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.randomlychosenbytes.jlocker.model.Building
import com.randomlychosenbytes.jlocker.model.Locker
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.SecretKeySpec

private const val CRYPTO_ALOGRITHM_NAME = "DES"

private fun getUtf8Bytes(str: String): ByteArray {
    return try {
        str.toByteArray(charset("UTF-8"))
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}

/**
 * Retuns a MD5 hash to a given array of bytes.
 */
fun getHash(pw: String): String {
    return try {
        val m = MessageDigest.getInstance("MD5")
        val bytes = getUtf8Bytes(pw)
        m.update(bytes, 0, bytes.size)
        BigInteger(1, m.digest()).toString(16)
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}

private fun bytesToBase64String(bytes: ByteArray): String {
    return Base64.getEncoder().encodeToString(bytes)
}

private fun base64StringToBytes(str: String): ByteArray {
    return Base64.getDecoder().decode(str)
}

fun encrypt(s: String, key: SecretKey?): String {
    return try {
        val ecipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
        ecipher.init(Cipher.ENCRYPT_MODE, key)
        bytesToBase64String(ecipher.doFinal(getUtf8Bytes(s)))
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}

fun decrypt(base64: String, key: SecretKey?): String {
    return try {
        val dcipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
        dcipher.init(Cipher.DECRYPT_MODE, key)
        String(dcipher.doFinal(base64StringToBytes(base64)), Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}

/**
 * Unseals the buildings object. This can't be done in the
 * loadFromCustomFile method, because the data is loaded before the password
 * was entered.
 */
fun unsealAndDeserializeBuildings(encryptedBuildingsBase64: String, key: SecretKey?): List<Building> {
    val json = decrypt(encryptedBuildingsBase64, key)
    val gson = GsonBuilder().registerTypeAdapter(
        com.randomlychosenbytes.jlocker.model.Module::class.java,
        ModuleDeserializer<com.randomlychosenbytes.jlocker.model.Module>()
    ).excludeFieldsWithoutExposeAnnotation().create()
    return gson.fromJson(json, object : TypeToken<List<Building?>?>() {}.type)
}

fun generateAndEncryptKey(pw: String): String {
    return try {
        encryptKeyWithString(KeyGenerator.getInstance(CRYPTO_ALOGRITHM_NAME).generateKey(), pw)
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}

fun encryptKeyWithString(key: SecretKey, pw: String): String {
    return try {
        val ecipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
        val desKeySpec = DESKeySpec(getUtf8Bytes(pw))
        val keyFactory = SecretKeyFactory.getInstance(CRYPTO_ALOGRITHM_NAME)
        val secretKey = keyFactory.generateSecret(desKeySpec)
        ecipher.init(Cipher.ENCRYPT_MODE, secretKey)
        bytesToBase64String(ecipher.doFinal(key.encoded))
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}

fun decryptKeyWithString(encKeyBase64: String, pw: String): SecretKey { // Key is saved as string
    return try {
        val dcipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
        val desKeySpec = DESKeySpec(getUtf8Bytes(pw))
        val keyFactory = SecretKeyFactory.getInstance(CRYPTO_ALOGRITHM_NAME)
        val secretKey = keyFactory.generateSecret(desKeySpec)
        dcipher.init(Cipher.DECRYPT_MODE, secretKey)
        SecretKeySpec(dcipher.doFinal(base64StringToBytes(encKeyBase64)), CRYPTO_ALOGRITHM_NAME)
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}

fun isDateValid(dateStr: String): Boolean = try {
    LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    true
} catch (e: DateTimeParseException) {
    false
}

fun moveOwner(sourceLocker: Locker, destLocker: Locker) {

    if (sourceLocker.isFree) {
        throw IllegalStateException("The source locker does not have an owner who could be moved to a new locker.")
    }

    if (!destLocker.isFree) {
        throw IllegalStateException("The destination locker still has an owner who has to be unassigned before a new owner can be assigned.")
    }

    destLocker.moveInNewOwner(sourceLocker.pupil)
    sourceLocker.empty()
}