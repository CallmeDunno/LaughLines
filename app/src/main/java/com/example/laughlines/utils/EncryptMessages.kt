package com.example.laughlines.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class EncryptMessages {

    private val algorithm = "AES"
    private lateinit var key: SecretKeySpec

    fun createKey(string: String) {
        this.key = SecretKeySpec(string.toByteArray(), algorithm)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("GetInstance")
    fun encode(message: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.getEncoder().encodeToString(cipher.doFinal(message.toByteArray(Charsets.UTF_8)))
    }

    @SuppressLint("GetInstance")
    @RequiresApi(Build.VERSION_CODES.O)
    fun decode(message: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(Base64.getDecoder().decode(message)), Charsets.UTF_8)
    }

}