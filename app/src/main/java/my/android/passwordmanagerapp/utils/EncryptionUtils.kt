package my.android.passwordmanagerapp.utils

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object AESUtils {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val TAG_SIZE = 128
    private const val IV_SIZE = 12

    fun encrypt(plaintext: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))

        val combined = ByteBuffer.allocate(iv.size + ciphertext.size)
            .put(iv)
            .put(ciphertext)
            .array()

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(encryptedData: String, key: SecretKey): String {
        val decoded = Base64.decode(encryptedData, Base64.NO_WRAP)
        val bb = ByteBuffer.wrap(decoded)

        val iv = ByteArray(IV_SIZE)
        bb.get(iv)

        val ciphertext = ByteArray(bb.remaining())
        bb.get(ciphertext)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, StandardCharsets.UTF_8)
    }
}