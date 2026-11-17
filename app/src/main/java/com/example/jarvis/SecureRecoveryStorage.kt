package com.example.jarvis

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Hardware-backed AES-256-GCM storage for interrupted action recovery state.
 *
 * Security guarantees:
 * 1. Zero plaintext PII (phone numbers, SMS bodies, memories) written to SharedPreferences or logs.
 * 2. Purges any legacy plaintext "pending_actions" entries automatically.
 * 3. Fail-closed: if Keystore encryption/decryption fails, persisted recovery state is wiped.
 */
object SecureRecoveryStorage {
    private const val TAG = "SecureRecoveryStorage"
    private const val PREFS_NAME = "rohit_recovery"
    private const val KEY_LEGACY_PLAINTEXT = "pending_actions"
    private const val KEY_CIPHERTEXT = "pending_actions_cipher"
    private const val KEY_IV = "pending_actions_iv"

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "jarvis_recovery_aes_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) return existingKey

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

    fun saveEncryptedJson(context: Context, plainJson: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return try {
            val key = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val cipherBytes = cipher.doFinal(plainJson.toByteArray(Charsets.UTF_8))

            val ivB64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val cipherB64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)

            prefs.edit()
                .remove(KEY_LEGACY_PLAINTEXT)
                .putString(KEY_IV, ivB64)
                .putString(KEY_CIPHERTEXT, cipherB64)
                .apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed encrypting recovery state (${e.javaClass.simpleName}); clearing prefs (fail-closed)")
            clear(context)
            false
        }
    }

    fun loadDecryptedJson(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_LEGACY_PLAINTEXT)) {
            // Purge any unencrypted legacy entries immediately
            prefs.edit().remove(KEY_LEGACY_PLAINTEXT).apply()
        }

        val ivB64 = prefs.getString(KEY_IV, null) ?: return null
        val cipherB64 = prefs.getString(KEY_CIPHERTEXT, null) ?: return null

        return try {
            val iv = Base64.decode(ivB64, Base64.NO_WRAP)
            val cipherBytes = Base64.decode(cipherB64, Base64.NO_WRAP)

            val key = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.w(TAG, "Failed decrypting recovery state (${e.javaClass.simpleName}); clearing corrupted prefs")
            clear(context)
            null
        }
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_LEGACY_PLAINTEXT)
            .remove(KEY_CIPHERTEXT)
            .remove(KEY_IV)
            .apply()
    }
}
