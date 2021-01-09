package com.randomlychosenbytes.jlocker

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.randomlychosenbytes.jlocker.model.Building
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

private fun getUtf8Bytes(str: String): ByteArray = str.toByteArray(charset("UTF-8"))

/**
 * Retuns a MD5 hash to a given array of bytes.
 */
fun getHash(pw: String): String {
    val m = MessageDigest.getInstance("MD5")
    val bytes = getUtf8Bytes(pw)
    m.update(bytes, 0, bytes.size)
    return BigInteger(1, m.digest()).toString(16)
}

private fun bytesToBase64String(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)


private fun base64StringToBytes(str: String): ByteArray = Base64.getDecoder().decode(str)


fun encrypt(s: String, key: SecretKey?): String {
    val ecipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
    ecipher.init(Cipher.ENCRYPT_MODE, key)
    return bytesToBase64String(ecipher.doFinal(getUtf8Bytes(s)))

}

fun decrypt(base64: String, key: SecretKey?): String {
    val dcipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
    dcipher.init(Cipher.DECRYPT_MODE, key)
    return String(dcipher.doFinal(base64StringToBytes(base64)), Charsets.UTF_8)
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

fun generateAndEncryptKey(
    pw: String
): String = encryptKeyWithString(KeyGenerator.getInstance(CRYPTO_ALOGRITHM_NAME).generateKey(), pw)

fun encryptKeyWithString(key: SecretKey, pw: String): String {
    val ecipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
    val desKeySpec = DESKeySpec(getUtf8Bytes(pw))
    val keyFactory = SecretKeyFactory.getInstance(CRYPTO_ALOGRITHM_NAME)
    val secretKey = keyFactory.generateSecret(desKeySpec)
    ecipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return bytesToBase64String(ecipher.doFinal(key.encoded))
}

fun decryptKeyWithString(encKeyBase64: String, pw: String): SecretKey { // Key is saved as string
    val dcipher = Cipher.getInstance(CRYPTO_ALOGRITHM_NAME)
    val desKeySpec = DESKeySpec(getUtf8Bytes(pw))
    val keyFactory = SecretKeyFactory.getInstance(CRYPTO_ALOGRITHM_NAME)
    val secretKey = keyFactory.generateSecret(desKeySpec)
    dcipher.init(Cipher.DECRYPT_MODE, secretKey)
    return SecretKeySpec(dcipher.doFinal(base64StringToBytes(encKeyBase64)), CRYPTO_ALOGRITHM_NAME)
}