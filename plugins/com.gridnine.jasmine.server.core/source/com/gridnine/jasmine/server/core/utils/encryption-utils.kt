package com.gridnine.jasmine.server.core.utils

import org.apache.commons.codec.digest.DigestUtils
import java.security.Key
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DESUtils {
    private const val KEY = "1234345356"
    private val encryptCipher: Cipher
    private val decryptCipher: Cipher


    fun encrypt(strIn: String): String {
        return Base64.getEncoder().encodeToString(encrypt(strIn.toByteArray()))
    }

    fun encrypt(arrB: ByteArray): ByteArray {
        return encryptCipher.doFinal(arrB)
    }

    fun decrypt(strIn: String): String {
        return String(decrypt(Base64.getDecoder().decode(strIn)))
    }

    fun decrypt(arrB: ByteArray): ByteArray {
        return decryptCipher.doFinal(arrB)
    }

    private fun getKey(arrBTmp: ByteArray): Key {
        val arrB = ByteArray(8)

        var i = 0
        while (i < arrBTmp.size && i < arrB.size) {
            arrB[i] = arrBTmp[i]
            i++
        }
        return SecretKeySpec(arrB, "DES")
    }

    init {
        encryptCipher = Cipher.getInstance("DES")
        encryptCipher.init(Cipher.ENCRYPT_MODE, getKey(KEY.toByteArray()))
        decryptCipher = Cipher.getInstance("DES")
        decryptCipher.init(Cipher.DECRYPT_MODE, getKey(KEY.toByteArray()))
    }
}

object DigestUtils{
    fun getMd5Hash(value:String):String{
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(value)
    }
}