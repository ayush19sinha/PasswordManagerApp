package my.android.passwordmanagerapp.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import my.android.passwordmanagerapp.utils.AESUtils
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class PasswordRepository(private val dao: PasswordDao) {

    companion object {
        private const val KEY_ALIAS = "password_manager_key"
        private lateinit var secretKey: SecretKey

        init {
            secretKey = getOrCreateKey()
        }

        private fun getOrCreateKey(): SecretKey {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
                )
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()

                keyGenerator.init(keyGenParameterSpec)
                return keyGenerator.generateKey()
            }

            return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }
    }

    suspend fun getAll(): List<Password> = withContext(Dispatchers.IO) {
        dao.getAll().map { password ->
            password.copy(password = AESUtils.decrypt(password.password, secretKey))
        }
    }

    suspend fun insert(password: Password) = withContext(Dispatchers.IO) {
        dao.insert(password.copy(password = AESUtils.encrypt(password.password, secretKey)))
    }

    suspend fun update(password: Password) = withContext(Dispatchers.IO) {
        dao.update(password.copy(password = AESUtils.encrypt(password.password, secretKey)))
    }

    suspend fun delete(password: Password) = withContext(Dispatchers.IO) {
        dao.delete(password)
    }
}