/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec

object DesUtil{
    private val globalKey = intArrayOf(48, 49, 50, 51, 52, 53, 54, 55, 56, 57)

    fun decode(str: String): String {
        val psw = CharArray(globalKey.size)
        for (i in psw.indices) {
            psw[i] = globalKey[i].toChar()
        }
        return try {
            String(getCipher(psw, Cipher.DECRYPT_MODE).doFinal(Base64.getDecoder().decode(str)), Charsets.UTF_8)
        } finally {
            Arrays.fill(psw, '\u0000')
        }
    }

    fun encode(data:String):String{
        val psw = CharArray(globalKey.size)
        for (i in psw.indices) {
            psw[i] = globalKey[i].toChar()
        }
        return try {
            Base64.getEncoder().encodeToString(getCipher(psw, Cipher.ENCRYPT_MODE).doFinal(data.toByteArray(Charsets.UTF_8)))
        } finally {
            Arrays.fill(psw, '\u0000')
        }
    }

    private fun getCipher(psw: CharArray, mode: Int): Cipher { // 8-byte Salt
        val slt = byteArrayOf(0xA9.toByte(), 0x9B.toByte(), 0xC8.toByte(), 0x32.toByte(),
                0x56.toByte(), 0x35.toByte(), 0xE3.toByte(), 0x03.toByte())
        // Iteration count
        val ctr = 19
        val alg = "PBEWithMD5AndDES" //$NON-NLS-1$
        val key = SecretKeyFactory.getInstance(alg)
                .generateSecret(PBEKeySpec(psw, slt, ctr))
        val result = Cipher.getInstance(key.algorithm)
        result.init(mode, key, PBEParameterSpec(slt, ctr))
        return result
    }


}
