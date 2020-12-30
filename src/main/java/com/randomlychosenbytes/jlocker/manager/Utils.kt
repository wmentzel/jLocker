package com.randomlychosenbytes.jlocker.manager

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.randomlychosenbytes.jlocker.ModuleDeserializer
import com.randomlychosenbytes.jlocker.nonabstractreps.Building
import java.math.BigInteger
import java.security.MessageDigest
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
        com.randomlychosenbytes.jlocker.nonabstractreps.Module::class.java,
        ModuleDeserializer<com.randomlychosenbytes.jlocker.nonabstractreps.Module>()
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

fun getCalendarFromString(dateStr: String): Calendar? {
    return if (dateStr.length < 10) {
        null
    } else try {
        val day = dateStr.substring(0, 2).toInt()
        val month = dateStr.substring(3, 5).toInt() - 1
        val year = dateStr.substring(6, 10).toInt()
        val calendar: Calendar = GregorianCalendar(year, month, day)
        calendar.isLenient = false
        calendar.time
        calendar
    } catch (e: Exception) {
        null
    }
}

fun isDateValid(dateStr: String): Boolean {
    return getCalendarFromString(dateStr) != null
}

fun getDifferenceInMonths(start: Calendar, end: Calendar): Long {
    return Math.round((end.timeInMillis.toDouble() - start.timeInMillis) / 2592000000.0) // 2592000000.0 = 24 * 60 * 60 * 1000 * 30
}