package com.example.jarvis

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

val Context.jarvisDataStore: DataStore<Preferences> by preferencesDataStore(name = "jarvis_secure")

/**
 * Manages device-bound Bearer authentication token using Android Keystore (AES-256-GCM)
 * backed by Jetpack DataStore.
 *
 * Guarantees:
 * 1. Plaintext token never touches disk or logs.
 * 2. Hardware-backed encryption via AndroidKeyStore (TEE / StrongBox).
 * 3. Graceful self-healing: clears invalid ciphertext on decrypt failure.
 */
class SecureTokenManager(private val context: Context) {

    companion object {
        private const val TAG = "SecureTokenManager"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "jarvis_device_token_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12

        private val KEY_ENCRYPTED_TOKEN = stringPreferencesKey("encrypted_bearer_token")
        private val KEY_GCM_IV = stringPreferencesKey("bearer_token_iv")

        private val TOKEN_REGEX = Regex("^jv_[A-Za-z0-9_-]{43}$")

        fun isValidTokenFormat(token: String): Boolean {
            return TOKEN_REGEX.matches(token.trim())
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    suspend fun saveToken(plainToken: String): Boolean {
        val trimmed = plainToken.trim()
        if (!isValidTokenFormat(trimmed)) {
            Log.e(TAG, "Rejected token save: format does not match jv_ + 43 characters")
            return false
        }

        return try {
            val key = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val cipherText = cipher.doFinal(trimmed.toByteArray(Charsets.UTF_8))

            val ivB64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val cipherB64 = Base64.encodeToString(cipherText, Base64.NO_WRAP)

            context.jarvisDataStore.edit { prefs ->
                prefs[KEY_GCM_IV] = ivB64
                prefs[KEY_ENCRYPTED_TOKEN] = cipherB64
            }
            Log.i(TAG, "Device Bearer token securely persisted to DataStore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed encrypting device token: ${e.javaClass.simpleName}")
            false
        }
    }

    suspend fun getToken(): String? {
        return try {
            val prefs = context.jarvisDataStore.data.first()
            val ivB64 = prefs[KEY_GCM_IV] ?: return null
            val cipherB64 = prefs[KEY_ENCRYPTED_TOKEN] ?: return null

            val iv = Base64.decode(ivB64, Base64.NO_WRAP)
            val cipherText = Base64.decode(cipherB64, Base64.NO_WRAP)

            val key = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plainBytes = cipher.doFinal(cipherText)
            String(plainBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.w(TAG, "Failed decrypting device token (${e.javaClass.simpleName}), clearing corrupted preference")
            clearToken()
            null
        }
    }

    suspend fun hasToken(): Boolean {
        return context.jarvisDataStore.data.map { prefs ->
            prefs.contains(KEY_ENCRYPTED_TOKEN) && prefs.contains(KEY_GCM_IV)
        }.first()
    }

    suspend fun clearToken() {
        context.jarvisDataStore.edit { prefs ->
            prefs.remove(KEY_GCM_IV)
            prefs.remove(KEY_ENCRYPTED_TOKEN)
        }
        Log.i(TAG, "Device token cleared from DataStore")
    }
}
