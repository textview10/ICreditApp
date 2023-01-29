package com.loan.icreditapp.util

import android.text.TextUtils
import android.util.Log
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class EncodeUtils {
    companion object {
        private val password = "3dd6f287c328a018cb5c93aa48554e8b"
        fun encryptAES(content: String): String {
            if (TextUtils.isEmpty(content)) {
                throw RuntimeException("content can not be empty")
            }
            val encryptResult = encrypt(content, password)
            return parseByte2HexStr(encryptResult)
        }

        fun decryptAES(ciphertext: String): String? {
            if (TextUtils.isEmpty(ciphertext)) {
                throw RuntimeException("ciphertext can not be empty")
            }
            return try {
                val decryptFrom = parseHexStr2Byte(ciphertext)
                val decryptResult = decrypt(decryptFrom, password)
                String(decryptResult!!)
            } catch (e: Exception) {
                null
            }
        }

        private fun encrypt(content: String, password: String): ByteArray? {
            try {
                val key = SecretKeySpec(password.toByteArray(), "AES")
                val cipher: Cipher = Cipher.getInstance("AES")
                val byteContent = content.toByteArray(charset("utf-8"))
                cipher.init(Cipher.ENCRYPT_MODE, key)
                return cipher.doFinal(byteContent)
            } catch (e: Exception) {
                Log.e("encrypt error", e.message.toString())
            }
            return null
        }

        private fun decrypt(content: ByteArray?, password: String): ByteArray? {
            try {
                val key = SecretKeySpec(password.toByteArray(), "AES")
                val cipher: Cipher = Cipher.getInstance("AES")
                cipher.init(Cipher.DECRYPT_MODE, key)
                return cipher.doFinal(content)
            } catch (e: Exception) {
                Log.e("decrypt error", e.message.toString())
            }
            return null
        }

        private fun parseByte2HexStr(buf: ByteArray?): String {
            val sb = StringBuffer()
            for (i in buf!!.indices) {
                var hex = Integer.toHexString(buf[i].toInt() and 0xFF)
                if (hex.length == 1) {
                    hex = "0$hex"
                }
                sb.append(hex.uppercase(Locale.getDefault()))
            }
            return sb.toString()
        }

        private fun parseHexStr2Byte(hexStr: String): ByteArray? {
            if (hexStr.length < 1) {
                return null
            }
            val result = ByteArray(hexStr.length / 2)
            for (i in 0 until hexStr.length / 2) {
                val high = hexStr.substring(i * 2, i * 2 + 1).toInt(16)
                val low = hexStr.substring(i * 2 + 1, i * 2 + 2).toInt(16)
                result[i] = (high * 16 + low).toByte()
            }
            return result
        }

        fun mainTest() {
            Log.e("Test", "加密解密测试:")
            var password = "ICREDIT" + "2022-01-01"
//            password = DigestUtils.md5DigestAsHex(password.toByteArray())
//            println("密码为:$password")
            val content = "this is update data 加密解密测试，&%……&（（））））¥#@！%……&（+——）"
            Log.e("Test","原内容为：$content")
            val encryContent: String? = encryptAES(content)
            Log.e("Test","加密后的内容为：$encryContent")
            val decryContent: String? = decryptAES(encryContent!!)
            Log.e("Test","解密后的内容为：$decryContent")
        }
    }
}