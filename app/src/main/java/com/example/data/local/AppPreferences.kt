package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.AppLanguage
import com.example.domain.GraphicsQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "imposter_g_prefs")

data class UserSettings(
    val language: AppLanguage = AppLanguage.ENGLISH,
    val activeProfileId: Long = 1L,
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val graphicsQuality: GraphicsQuality = GraphicsQuality.HIGH,
    val reduceMotion: Boolean = false,
    val playedLanguagesCsv: String = ""
)

class AppPreferences(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language_code")
        val ACTIVE_PROFILE = longPreferencesKey("active_profile_id")
        val MUSIC = booleanPreferencesKey("music_enabled")
        val SFX = booleanPreferencesKey("sfx_enabled")
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
        val GRAPHICS_QUALITY = stringPreferencesKey("graphics_quality")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val PLAYED_LANGUAGES = stringPreferencesKey("played_languages_csv")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            language = AppLanguage.fromCode(prefs[Keys.LANGUAGE] ?: AppLanguage.ENGLISH.code),
            activeProfileId = prefs[Keys.ACTIVE_PROFILE] ?: 1L,
            musicEnabled = prefs[Keys.MUSIC] ?: true,
            sfxEnabled = prefs[Keys.SFX] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION] ?: true,
            graphicsQuality = GraphicsQuality.fromId(prefs[Keys.GRAPHICS_QUALITY] ?: GraphicsQuality.HIGH.id),
            reduceMotion = prefs[Keys.REDUCE_MOTION] ?: false,
            playedLanguagesCsv = prefs[Keys.PLAYED_LANGUAGES] ?: ""
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setActiveProfileId(id: Long) {
        context.dataStore.edit { it[Keys.ACTIVE_PROFILE] = id }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MUSIC] = enabled }
    }

    suspend fun setSfxEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SFX] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIBRATION] = enabled }
    }

    suspend fun setGraphicsQuality(quality: GraphicsQuality) {
        context.dataStore.edit { it[Keys.GRAPHICS_QUALITY] = quality.id }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.dataStore.edit { it[Keys.REDUCE_MOTION] = enabled }
    }

    suspend fun resetSettings() {
        context.dataStore.edit { prefs ->
            prefs[Keys.MUSIC] = true
            prefs[Keys.SFX] = true
            prefs[Keys.VIBRATION] = true
            prefs[Keys.GRAPHICS_QUALITY] = GraphicsQuality.HIGH.id
            prefs[Keys.REDUCE_MOTION] = false
        }
    }

    suspend fun recordLanguagePlayed(language: AppLanguage): Int {
        var distinctCount = 1
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.PLAYED_LANGUAGES] ?: "")
                .split(",")
                .filter { it.isNotBlank() }
                .toMutableSet()
            current.add(language.code)
            distinctCount = current.size
            prefs[Keys.PLAYED_LANGUAGES] = current.joinToString(",")
        }
        return distinctCount
    }
}
