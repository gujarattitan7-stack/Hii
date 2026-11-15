package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SynthAudioEngine
import com.example.data.local.UserSettings
import com.example.data.repository.GameRepository
import com.example.domain.AchievementProgress
import com.example.domain.AppLanguage
import com.example.domain.AvatarConfig
import com.example.domain.GraphicsQuality
import com.example.domain.PlayerProfile
import com.example.domain.RoundHistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileStatsViewModel(
    private val repository: GameRepository
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    val profiles: StateFlow<List<PlayerProfile>> = repository.profiles
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val activeProfile: StateFlow<PlayerProfile?> = repository.activeProfile
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val roundHistory: StateFlow<List<RoundHistoryItem>> = repository.roundHistory
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val achievements: StateFlow<List<AchievementProgress>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { repository.setLanguage(language) }
    }

    fun toggleLanguage() {
        val next = if (userSettings.value.language == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
        setLanguage(next)
    }

    fun setMusicEnabled(enabled: Boolean) {
        SynthAudioEngine.configure(enabled, userSettings.value.sfxEnabled)
        viewModelScope.launch { repository.setMusicEnabled(enabled) }
    }

    fun setSfxEnabled(enabled: Boolean) {
        SynthAudioEngine.configure(userSettings.value.musicEnabled, enabled)
        viewModelScope.launch { repository.setSfxEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setVibrationEnabled(enabled) }
    }

    fun setGraphicsQuality(quality: GraphicsQuality) {
        viewModelScope.launch { repository.setGraphicsQuality(quality) }
    }

    fun setReduceMotion(enabled: Boolean) {
        viewModelScope.launch { repository.setReduceMotion(enabled) }
    }

    fun resetSettings() {
        SynthAudioEngine.configure(music = true, sfx = true)
        viewModelScope.launch { repository.resetSettings() }
    }

    fun setActiveProfile(id: Long) {
        viewModelScope.launch { repository.setActiveProfile(id) }
    }

    fun createProfile(name: String, avatar: AvatarConfig) {
        viewModelScope.launch { repository.createProfile(name, avatar) }
    }

    fun updateProfileAvatar(profileId: Long, name: String, avatar: AvatarConfig) {
        viewModelScope.launch { repository.updateProfileAvatar(profileId, name, avatar) }
    }

    fun deleteProfile(profileId: Long) {
        viewModelScope.launch { repository.deleteProfile(profileId) }
    }
}
