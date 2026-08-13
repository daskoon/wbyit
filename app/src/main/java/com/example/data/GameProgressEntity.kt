package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey val id: Int = 1,
    val careerRank: String = "Trainee Greeter",
    val totalCareerEarnings: Int = 0,
    val unlockedShift: Int = 1,
    val shiftStarsJson: String = "0,0,0,0,0,0",
    val shiftHighScoresJson: String = "0,0,0,0,0,0",
    val purchasedUpgrades: String = "",
    val discoveredArchetypes: String = "confused_senior,spec_dad,lost_wanderer,quick_pickup",
    val totalCustomersServed: Int = 0,
    val totalShopliftersStopped: Int = 0,
    val totalRevenueGenerated: Long = 0L,
    val averageCsatPercent: Int = 85,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)
