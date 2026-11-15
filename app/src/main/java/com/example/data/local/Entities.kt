package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profiles")
data class PlayerProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val presetId: String,
    val hairStyleId: String,
    val glassesId: String,
    val hatId: String,
    val outfitId: String,
    val accessoryId: String,
    val colorIndex: Int,
    val pedestalColorIndex: Int,
    val xp: Int,
    val level: Int,
    val gamesPlayed: Int,
    val crewWins: Int,
    val imposterWins: Int,
    val losses: Int,
    val correctVotes: Int,
    val missionsCompleted: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val createdAt: Long
)

@Entity(tableName = "round_history")
data class RoundHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val modeType: String,
    val categorySummary: String,
    val difficulty: String,
    val secretWordEn: String,
    val secretWordHi: String,
    val imposterNames: String,
    val winnerSide: String,
    val suddenDeathTriggered: Boolean,
    val challengeTitleEn: String?,
    val challengeTitleHi: String?,
    val xpAwarded: Int,
    val playerCount: Int
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val progress: Int,
    val target: Int,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)

@Entity(tableName = "saved_rules")
data class SavedRulesEntity(
    @PrimaryKey val id: Int = 1,
    val modeType: String,
    val playerCount: Int,
    val imposterCount: Int,
    val randomImposterCount: Boolean,
    val discussionSeconds: Int,
    val votingSeconds: Int,
    val difficulty: String,
    val selectedCategoriesCsv: String,
    val hintsEnabled: Boolean,
    val maxHintsPerRound: Int,
    val secretMissionsEnabled: Boolean,
    val challengeWheelEnabled: Boolean,
    val suddenDeathEnabled: Boolean,
    val skipWordEnabled: Boolean,
    val secretVotingEnabled: Boolean
)
