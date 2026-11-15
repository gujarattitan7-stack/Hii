package com.example.data.repository

import com.example.data.local.AchievementEntity
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.RoundHistoryEntity
import com.example.data.local.SavedRulesEntity
import com.example.data.local.UserSettings
import com.example.data.words.WordBank
import com.example.domain.AccessoryStyle
import com.example.domain.AchievementProgress
import com.example.domain.AppLanguage
import com.example.domain.AvatarConfig
import com.example.domain.AvatarPreset
import com.example.domain.CategoryId
import com.example.domain.DifficultyLevel
import com.example.domain.GameModeType
import com.example.domain.GlassesStyle
import com.example.domain.GraphicsQuality
import com.example.domain.HairStyle
import com.example.domain.HatStyle
import com.example.domain.MatchConfig
import com.example.domain.OutfitStyle
import com.example.domain.PlayerProfile
import com.example.domain.PlayerSlot
import com.example.domain.RoundHistoryItem
import com.example.domain.WinnerSide
import com.example.domain.WordEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GameRepository(
    private val db: AppDatabase,
    private val prefs: AppPreferences
) {
    private val profileDao = db.profileDao()
    private val historyDao = db.roundHistoryDao()
    private val achievementDao = db.achievementDao()
    private val rulesDao = db.savedRulesDao()

    val userSettings: Flow<UserSettings> = prefs.settingsFlow

    val profiles: Flow<List<PlayerProfile>> = profileDao.observeProfiles().map { list ->
        list.map { it.toDomain() }
    }

    val activeProfile: Flow<PlayerProfile?> = combine(profiles, userSettings) { list, settings ->
        list.firstOrNull { it.id == settings.activeProfileId } ?: list.firstOrNull()
    }

    val roundHistory: Flow<List<RoundHistoryItem>> = historyDao.observeHistory().map { list ->
        list.map { it.toDomain() }
    }

    val achievements: Flow<List<AchievementProgress>> = achievementDao.observeAchievements().map { entities ->
        val byId = entities.associateBy { it.id }
        WordBank.achievements.map { def ->
            val entity = byId[def.id]
            AchievementProgress(
                def = def,
                progress = entity?.progress ?: 0,
                isUnlocked = entity?.isUnlocked ?: false,
                unlockedAt = entity?.unlockedAt
            )
        }
    }

    val savedMatchConfig: Flow<MatchConfig> = rulesDao.observeRules().map { entity ->
        entity?.toDomain() ?: MatchConfig()
    }

    suspend fun ensureSeeded() {
        val existingProfiles = profileDao.getAllProfilesOnce()
        if (existingProfiles.isEmpty()) {
            val leoAvatar = AvatarConfig.fromPreset(AvatarPreset.LEO)
            val riyaAvatar = AvatarConfig.fromPreset(AvatarPreset.RIYA)
            val arjunAvatar = AvatarConfig.fromPreset(AvatarPreset.ARJUN)
            val nehaAvatar = AvatarConfig.fromPreset(AvatarPreset.NEHA)

            val mainId = profileDao.insertProfile(
                createEntityFromAvatar(name = "GamerPro", avatar = leoAvatar, xp = 850, level = 1)
            )
            profileDao.insertProfile(
                createEntityFromAvatar(name = "Riya", avatar = riyaAvatar, xp = 240, level = 1)
            )
            profileDao.insertProfile(
                createEntityFromAvatar(name = "Arjun", avatar = arjunAvatar, xp = 180, level = 1)
            )
            profileDao.insertProfile(
                createEntityFromAvatar(name = "Neha", avatar = nehaAvatar, xp = 120, level = 1)
            )
            prefs.setActiveProfileId(mainId)
        }

        val achievementEntities = WordBank.achievements.map { def ->
            AchievementEntity(
                id = def.id,
                progress = 0,
                target = def.target,
                isUnlocked = false,
                unlockedAt = null
            )
        }
        achievementDao.insertAllIgnore(achievementEntities)

        if (rulesDao.getRulesOnce() == null) {
            saveMatchConfig(MatchConfig())
        }
    }

    private fun createEntityFromAvatar(
        name: String,
        avatar: AvatarConfig,
        xp: Int = 0,
        level: Int = 1
    ): PlayerProfileEntity = PlayerProfileEntity(
        id = 0,
        name = name,
        presetId = avatar.preset.id,
        hairStyleId = avatar.hairStyle.id,
        glassesId = avatar.glasses.id,
        hatId = avatar.hat.id,
        outfitId = avatar.outfit.id,
        accessoryId = avatar.accessory.id,
        colorIndex = avatar.colorIndex,
        pedestalColorIndex = avatar.pedestalColorIndex,
        xp = xp,
        level = level,
        gamesPlayed = 0,
        crewWins = 0,
        imposterWins = 0,
        losses = 0,
        correctVotes = 0,
        missionsCompleted = 0,
        currentStreak = 0,
        bestStreak = 0,
        createdAt = System.currentTimeMillis()
    )

    suspend fun setLanguage(language: AppLanguage) = prefs.setLanguage(language)
    suspend fun setActiveProfile(id: Long) = prefs.setActiveProfileId(id)
    suspend fun setMusicEnabled(enabled: Boolean) = prefs.setMusicEnabled(enabled)
    suspend fun setSfxEnabled(enabled: Boolean) = prefs.setSfxEnabled(enabled)
    suspend fun setVibrationEnabled(enabled: Boolean) = prefs.setVibrationEnabled(enabled)
    suspend fun setGraphicsQuality(quality: GraphicsQuality) = prefs.setGraphicsQuality(quality)
    suspend fun setReduceMotion(enabled: Boolean) = prefs.setReduceMotion(enabled)
    suspend fun resetSettings() = prefs.resetSettings()

    suspend fun createProfile(name: String, avatar: AvatarConfig): Long {
        val cleanName = name.trim().ifEmpty { "Agent" }.take(18)
        val id = profileDao.insertProfile(createEntityFromAvatar(cleanName, avatar))
        prefs.setActiveProfileId(id)
        unlockAchievement("ach_perfect_name")
        return id
    }

    suspend fun updateProfileAvatar(profileId: Long, name: String, avatar: AvatarConfig) {
        val existing = profileDao.getProfileById(profileId) ?: return
        profileDao.updateProfile(
            existing.copy(
                name = name.trim().ifEmpty { existing.name }.take(18),
                presetId = avatar.preset.id,
                hairStyleId = avatar.hairStyle.id,
                glassesId = avatar.glasses.id,
                hatId = avatar.hat.id,
                outfitId = avatar.outfit.id,
                accessoryId = avatar.accessory.id,
                colorIndex = avatar.colorIndex,
                pedestalColorIndex = avatar.pedestalColorIndex
            )
        )
        unlockAchievement("ach_perfect_name")
    }

    suspend fun deleteProfile(profileId: Long) {
        val all = profileDao.getAllProfilesOnce()
        if (all.size <= 1) return
        profileDao.deleteProfile(profileId)
        val remaining = profileDao.getAllProfilesOnce()
        if (remaining.isNotEmpty()) {
            prefs.setActiveProfileId(remaining.first().id)
        }
    }

    suspend fun saveMatchConfig(config: MatchConfig) {
        rulesDao.saveRules(
            SavedRulesEntity(
                id = 1,
                modeType = config.modeType.id,
                playerCount = config.playerCount,
                imposterCount = config.imposterCount,
                randomImposterCount = config.randomImposterCount,
                discussionSeconds = config.discussionSeconds,
                votingSeconds = config.votingSeconds,
                difficulty = config.difficulty.name,
                selectedCategoriesCsv = config.selectedCategories.joinToString(",") { it.id.toString() },
                hintsEnabled = config.hintsEnabled,
                maxHintsPerRound = config.maxHintsPerRound,
                secretMissionsEnabled = config.secretMissionsEnabled,
                challengeWheelEnabled = config.challengeWheelEnabled,
                suddenDeathEnabled = config.suddenDeathEnabled,
                skipWordEnabled = config.skipWordEnabled,
                secretVotingEnabled = config.secretVotingEnabled
            )
        )
    }

    suspend fun unlockAchievement(id: String): Boolean {
        val def = WordBank.achievements.firstOrNull { it.id == id } ?: return false
        val existing = achievementDao.getById(id) ?: AchievementEntity(id, 0, def.target, false, null)
        if (existing.isUnlocked) return false
        achievementDao.upsert(
            existing.copy(
                progress = def.target,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
        )
        return true
    }

    data class RoundSaveResult(
        val xpAwardedTotal: Int,
        val newlyUnlockedAchievementIds: List<String>,
        val leveledUpNames: List<String>
    )

    suspend fun recordRoundOutcome(
        config: MatchConfig,
        players: List<PlayerSlot>,
        word: WordEntry,
        winnerSide: WinnerSide,
        votesCast: Map<String, String>,
        suddenDeathTriggered: Boolean,
        challengeTitleEn: String?,
        challengeTitleHi: String?,
        xpMultiplier: Float,
        activeLanguage: AppLanguage
    ): RoundSaveResult {
        val imposters = players.filter { it.isImposter }
        val imposterIds = imposters.map { it.slotId }.toSet()
        val now = System.currentTimeMillis()
        var totalXpThisRound = 0
        val leveledUpNames = mutableListOf<String>()

        for (slot in players) {
            val missionBonus = if (slot.missionCompleted) (slot.secretMission?.bonusXp ?: 50) else 0
            val didWin = (slot.isImposter && winnerSide == WinnerSide.IMPOSTER) ||
                (!slot.isImposter && winnerSide == WinnerSide.CREW)
            val votedTarget = votesCast[slot.slotId]
            val votedCorrectly = !slot.isImposter && votedTarget != null && votedTarget in imposterIds

            val rawXp = (if (didWin) 180 else 65) +
                (if (votedCorrectly) 50 else 0) +
                (if (suddenDeathTriggered) 40 else 0) +
                missionBonus
            val finalXp = (rawXp * xpMultiplier).toInt().coerceAtLeast(40)

            if (!slot.isBot) {
                totalXpThisRound += finalXp
            }

            val profileId = slot.profileId ?: continue
            val entity = profileDao.getProfileById(profileId) ?: continue
            val newXp = entity.xp + finalXp
            val newLevel = (newXp / 1200) + 1
            if (newLevel > entity.level) {
                leveledUpNames.add(entity.name)
            }
            val newStreak = if (didWin) entity.currentStreak + 1 else 0
            val newBestStreak = maxOf(entity.bestStreak, newStreak)

            profileDao.updateProfile(
                entity.copy(
                    xp = newXp,
                    level = newLevel,
                    gamesPlayed = entity.gamesPlayed + 1,
                    crewWins = entity.crewWins + if (!slot.isImposter && winnerSide == WinnerSide.CREW) 1 else 0,
                    imposterWins = entity.imposterWins + if (slot.isImposter && winnerSide == WinnerSide.IMPOSTER) 1 else 0,
                    losses = entity.losses + if (!didWin) 1 else 0,
                    correctVotes = entity.correctVotes + if (votedCorrectly) 1 else 0,
                    missionsCompleted = entity.missionsCompleted + if (slot.missionCompleted) 1 else 0,
                    currentStreak = newStreak,
                    bestStreak = newBestStreak
                )
            )
        }

        if (totalXpThisRound == 0) {
            totalXpThisRound = (150 * xpMultiplier).toInt()
        }

        historyDao.insertRound(
            RoundHistoryEntity(
                timestamp = now,
                modeType = config.modeType.id,
                categorySummary = word.category.name,
                difficulty = word.difficulty.name,
                secretWordEn = word.wordEn,
                secretWordHi = word.wordHi,
                imposterNames = imposters.joinToString(", ") { it.name },
                winnerSide = winnerSide.name,
                suddenDeathTriggered = suddenDeathTriggered,
                challengeTitleEn = challengeTitleEn,
                challengeTitleHi = challengeTitleHi,
                xpAwarded = totalXpThisRound,
                playerCount = players.size
            )
        )

        val newlyUnlocked = mutableListOf<String>()
        val langsCount = prefs.recordLanguagePlayed(activeLanguage)

        suspend fun advanceAchievement(id: String, delta: Int = 0, absoluteMin: Int? = null) {
            val def = WordBank.achievements.firstOrNull { it.id == id } ?: return
            val existing = achievementDao.getById(id) ?: AchievementEntity(id, 0, def.target, false, null)
            val updatedProgress = when {
                absoluteMin != null -> maxOf(existing.progress, absoluteMin)
                else -> existing.progress + delta
            }.coerceAtMost(def.target)
            val nowUnlocked = updatedProgress >= def.target
            if (nowUnlocked && !existing.isUnlocked) {
                newlyUnlocked.add(id)
            }
            achievementDao.upsert(
                existing.copy(
                    progress = updatedProgress,
                    isUnlocked = existing.isUnlocked || nowUnlocked,
                    unlockedAt = if (!existing.isUnlocked && nowUnlocked) now else existing.unlockedAt
                )
            )
        }

        advanceAchievement("ach_first_win", delta = 1)
        if (winnerSide == WinnerSide.CREW) advanceAchievement("ach_imp_hunter", delta = 1)
        if (winnerSide == WinnerSide.IMPOSTER) advanceAchievement("ach_imp_gamer", delta = 1)
        advanceAchievement("ach_game_master", delta = 1)
        if (config.modeType == GameModeType.PRACTICE) advanceAchievement("ach_practice_3", delta = 1)
        if (suddenDeathTriggered || config.modeType == GameModeType.SUDDEN_DEATH) {
            advanceAchievement("ach_sudden_death", delta = 1)
        }
        if (config.challengeWheelEnabled || config.modeType == GameModeType.CHALLENGE) {
            advanceAchievement("ach_wheel_5", delta = 1)
        }
        advanceAchievement("ach_polyglot", absoluteMin = langsCount)
        if (players.size >= 8) advanceAchievement("ach_full_house", delta = 1)

        return RoundSaveResult(
            xpAwardedTotal = totalXpThisRound,
            newlyUnlockedAchievementIds = newlyUnlocked,
            leveledUpNames = leveledUpNames
        )
    }

    suspend fun awardSecretMissionBonus(
        profileId: Long?,
        bonusXp: Int,
        deltaCount: Int
    ) {
        if (profileId != null && bonusXp != 0) {
            val entity = profileDao.getProfileById(profileId)
            if (entity != null) {
                val newXp = (entity.xp + bonusXp).coerceAtLeast(0)
                val newLevel = (newXp / 1200) + 1
                profileDao.updateProfile(
                    entity.copy(
                        xp = newXp,
                        level = newLevel,
                        missionsCompleted = (entity.missionsCompleted + deltaCount).coerceAtLeast(0)
                    )
                )
            }
        }
    }
}

private fun PlayerProfileEntity.toDomain(): PlayerProfile = PlayerProfile(
    id = id,
    name = name,
    avatar = AvatarConfig(
        preset = AvatarPreset.fromId(presetId),
        hairStyle = HairStyle.fromId(hairStyleId),
        glasses = GlassesStyle.fromId(glassesId),
        hat = HatStyle.fromId(hatId),
        outfit = OutfitStyle.fromId(outfitId),
        accessory = AccessoryStyle.fromId(accessoryId),
        colorIndex = colorIndex,
        pedestalColorIndex = pedestalColorIndex
    ),
    xp = xp,
    level = level,
    gamesPlayed = gamesPlayed,
    crewWins = crewWins,
    imposterWins = imposterWins,
    losses = losses,
    correctVotes = correctVotes,
    missionsCompleted = missionsCompleted,
    currentStreak = currentStreak,
    bestStreak = bestStreak,
    createdAt = createdAt
)

private fun RoundHistoryEntity.toDomain(): RoundHistoryItem = RoundHistoryItem(
    id = id,
    timestamp = timestamp,
    modeType = GameModeType.fromId(modeType),
    categorySummary = categorySummary,
    difficulty = runCatching { DifficultyLevel.valueOf(difficulty) }.getOrDefault(DifficultyLevel.MEDIUM),
    secretWordEn = secretWordEn,
    secretWordHi = secretWordHi,
    imposterNames = imposterNames,
    winnerSide = runCatching { WinnerSide.valueOf(winnerSide) }.getOrDefault(WinnerSide.CREW),
    suddenDeathTriggered = suddenDeathTriggered,
    challengeTitleEn = challengeTitleEn,
    challengeTitleHi = challengeTitleHi,
    xpAwarded = xpAwarded,
    playerCount = playerCount
)

private fun SavedRulesEntity.toDomain(): MatchConfig {
    val cats = selectedCategoriesCsv.split(",")
        .mapNotNull { it.toIntOrNull()?.let { id -> CategoryId.fromId(id) } }
        .toSet()
        .ifEmpty { CategoryId.entries.toSet() }
    return MatchConfig(
        modeType = GameModeType.fromId(modeType),
        playerCount = playerCount.coerceIn(1, 10),
        imposterCount = imposterCount.coerceIn(1, 3),
        randomImposterCount = randomImposterCount,
        difficulty = runCatching { DifficultyLevel.valueOf(difficulty) }.getOrDefault(DifficultyLevel.MIXED),
        selectedCategories = cats,
        discussionSeconds = discussionSeconds.coerceIn(20, 300),
        votingSeconds = votingSeconds.coerceIn(15, 120),
        hintsEnabled = hintsEnabled,
        maxHintsPerRound = maxHintsPerRound.coerceIn(0, 3),
        secretMissionsEnabled = secretMissionsEnabled,
        challengeWheelEnabled = challengeWheelEnabled,
        suddenDeathEnabled = suddenDeathEnabled,
        skipWordEnabled = skipWordEnabled,
        secretVotingEnabled = secretVotingEnabled
    )
}
