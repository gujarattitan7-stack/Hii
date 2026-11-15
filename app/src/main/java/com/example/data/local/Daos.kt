package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM player_profiles ORDER BY xp DESC, id ASC")
    fun observeProfiles(): Flow<List<PlayerProfileEntity>>

    @Query("SELECT * FROM player_profiles ORDER BY id ASC")
    suspend fun getAllProfilesOnce(): List<PlayerProfileEntity>

    @Query("SELECT * FROM player_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PlayerProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: PlayerProfileEntity)

    @Query("DELETE FROM player_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Long)
}

@Dao
interface RoundHistoryDao {
    @Query("SELECT * FROM round_history ORDER BY timestamp DESC")
    fun observeHistory(): Flow<List<RoundHistoryEntity>>

    @Query("SELECT COUNT(*) FROM round_history")
    suspend fun getHistoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: RoundHistoryEntity): Long
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun observeAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(items: List<AchievementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: AchievementEntity)
}

@Dao
interface SavedRulesDao {
    @Query("SELECT * FROM saved_rules WHERE id = 1 LIMIT 1")
    fun observeRules(): Flow<SavedRulesEntity?>

    @Query("SELECT * FROM saved_rules WHERE id = 1 LIMIT 1")
    suspend fun getRulesOnce(): SavedRulesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRules(rules: SavedRulesEntity)
}
