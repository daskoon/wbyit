package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_progress WHERE id = 1 LIMIT 1")
    fun getGameProgress(): Flow<GameProgressEntity?>

    @Query("SELECT * FROM game_progress WHERE id = 1 LIMIT 1")
    suspend fun getGameProgressSync(): GameProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: GameProgressEntity)

    @Query("UPDATE game_progress SET totalCareerEarnings = :earnings WHERE id = 1")
    suspend fun updateEarnings(earnings: Int)
}
