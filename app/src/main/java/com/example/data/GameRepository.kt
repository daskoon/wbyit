package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val gameDao: GameDao) {

    val gameProgress: Flow<GameProgressEntity> = gameDao.getGameProgress().map { entity ->
        entity ?: GameProgressEntity()
    }

    suspend fun getProgress(): GameProgressEntity {
        return gameDao.getGameProgressSync() ?: GameProgressEntity()
    }

    suspend fun saveProgress(progress: GameProgressEntity) {
        gameDao.insertOrUpdateProgress(progress)
    }

    suspend fun recordShiftResult(
        shiftNumber: Int,
        starsEarned: Int,
        revenueEarned: Int,
        customersServed: Int,
        shopliftersCaught: Int,
        shiftCsat: Int,
        newDiscoveredArchetypes: List<String>
    ) {
        val current = getProgress()
        val currentStars = current.shiftStarsJson.split(",").map { it.toIntOrNull() ?: 0 }.toMutableList()
        while (currentStars.size < 6) currentStars.add(0)
        
        val index = (shiftNumber - 1).coerceIn(0, 5)
        if (starsEarned > currentStars[index]) {
            currentStars[index] = starsEarned
        }

        val currentHighScores = current.shiftHighScoresJson.split(",").map { it.toIntOrNull() ?: 0 }.toMutableList()
        while (currentHighScores.size < 6) currentHighScores.add(0)
        if (revenueEarned > currentHighScores[index]) {
            currentHighScores[index] = revenueEarned
        }

        val nextUnlocked = if (starsEarned >= 1 && current.unlockedShift <= shiftNumber) {
            (shiftNumber + 1).coerceAtMost(6)
        } else {
            current.unlockedShift
        }

        val knownSet = current.discoveredArchetypes.split(",").toMutableSet()
        knownSet.addAll(newDiscoveredArchetypes)

        val newTotalEarnings = current.totalCareerEarnings + revenueEarned
        val newRank = calculateRank(newTotalEarnings, currentStars.sum())

        val updated = current.copy(
            careerRank = newRank,
            totalCareerEarnings = newTotalEarnings,
            unlockedShift = nextUnlocked,
            shiftStarsJson = currentStars.joinToString(","),
            shiftHighScoresJson = currentHighScores.joinToString(","),
            discoveredArchetypes = knownSet.joinToString(","),
            totalCustomersServed = current.totalCustomersServed + customersServed,
            totalShopliftersStopped = current.totalShopliftersStopped + shopliftersCaught,
            totalRevenueGenerated = current.totalRevenueGenerated + revenueEarned
        )
        gameDao.insertOrUpdateProgress(updated)
    }

    suspend fun purchaseUpgrade(upgradeId: String, cost: Int): Boolean {
        val current = getProgress()
        if (current.totalCareerEarnings < cost) return false
        val owned = current.purchasedUpgrades.split(",").filter { it.isNotEmpty() }.toMutableSet()
        if (owned.contains(upgradeId)) return false
        owned.add(upgradeId)
        
        val updated = current.copy(
            totalCareerEarnings = current.totalCareerEarnings - cost,
            purchasedUpgrades = owned.joinToString(",")
        )
        gameDao.insertOrUpdateProgress(updated)
        return true
    }

    private fun calculateRank(totalEarnings: Int, totalStars: Int): String {
        return when {
            totalStars >= 15 || totalEarnings >= 75000 -> "Store General Manager"
            totalStars >= 12 || totalEarnings >= 40000 -> "Assistant Store Manager"
            totalStars >= 8 || totalEarnings >= 20000 -> "Front End Floor Lead"
            totalStars >= 4 || totalEarnings >= 8000 -> "Blue Shirt Senior Host"
            totalStars >= 2 || totalEarnings >= 2500 -> "Customer Greeter Specialist"
            else -> "Trainee Host Greeter"
        }
    }
}
