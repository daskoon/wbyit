package com.example.model

data class ShiftConfig(
    val shiftNumber: Int,
    val title: String,
    val subtitle: String,
    val shiftDurationSeconds: Int,
    val targetRevenue: Int,
    val minCsatPercent: Int,
    val spawnIntervalMs: Long,
    val allowedArchetypes: List<CustomerArchetype>,
    val specialNotes: String,
    val starThresholds: List<Int> // 1-star, 2-star, 3-star revenue
)

object GameShiftConfigs {
    val SHIFTS = listOf(
        ShiftConfig(
            shiftNumber = 1,
            title = "Tuesday Morning Orientation",
            subtitle = "Learn the ropes at the Host Desk",
            shiftDurationSeconds = 50,
            targetRevenue = 1200,
            minCsatPercent = 60,
            spawnIntervalMs = 4500L,
            allowedArchetypes = listOf(
                CustomerArchetype.CONFUSED_SENIOR,
                CustomerArchetype.SPEC_SHEET_DAD,
                CustomerArchetype.LOST_WANDERER,
                CustomerArchetype.QUICK_PICKUP
            ),
            specialNotes = "Greet customers to find out what brings them in, then tap the right department!",
            starThresholds = listOf(1200, 2500, 4000)
        ),
        ShiftConfig(
            shiftNumber = 2,
            title = "Thursday Evening Rush",
            subtitle = "Tech enthusiasts & rapid online pickups",
            shiftDurationSeconds = 65,
            targetRevenue = 3500,
            minCsatPercent = 70,
            spawnIntervalMs = 3600L,
            allowedArchetypes = listOf(
                CustomerArchetype.PC_GAMER_ENTHUSIAST,
                CustomerArchetype.QUICK_PICKUP,
                CustomerArchetype.SMART_HOME_DIYER,
                CustomerArchetype.SPEC_SHEET_DAD,
                CustomerArchetype.AUDIOPHILE_PURIST
            ),
            specialNotes = "Use the Radio Dispatch to send Blue Shirts directly to customers for double speed!",
            starThresholds = listOf(3500, 6000, 9000)
        ),
        ShiftConfig(
            shiftNumber = 3,
            title = "Saturday Afternoon Super-Sale",
            subtitle = "Shoplifters, Karens & high-ticket buyers",
            shiftDurationSeconds = 80,
            targetRevenue = 6500,
            minCsatPercent = 75,
            spawnIntervalMs = 3000L,
            allowedArchetypes = listOf(
                CustomerArchetype.IMPATIENT_KAREN,
                CustomerArchetype.SHADY_SHOPLIFTER,
                CustomerArchetype.APPLIANCE_HOMEOWNER,
                CustomerArchetype.PRO_PHOTOGRAPHER,
                CustomerArchetype.CONFUSED_SENIOR,
                CustomerArchetype.PC_GAMER_ENTHUSIAST
            ),
            specialNotes = "Watch out for shady browsers (Security Alert!) and escalations (Call Manager!).",
            starThresholds = listOf(6500, 11000, 16000)
        ),
        ShiftConfig(
            shiftNumber = 4,
            title = "Black Friday Midnight Frenzy",
            subtitle = "High adrenaline door-buster waves!",
            shiftDurationSeconds = 90,
            targetRevenue = 12000,
            minCsatPercent = 70,
            spawnIntervalMs = 2200L,
            allowedArchetypes = CustomerArchetype.entries,
            specialNotes = "Crowds enter fast! Use Intercom announcements and handouts to prevent walkouts!",
            starThresholds = listOf(12000, 19000, 28000)
        ),
        ShiftConfig(
            shiftNumber = 5,
            title = "Holiday Eve Closing Shift",
            subtitle = "VIP creators & desperate last-minute shoppers",
            shiftDurationSeconds = 95,
            targetRevenue = 18000,
            minCsatPercent = 80,
            spawnIntervalMs = 2000L,
            allowedArchetypes = CustomerArchetype.entries,
            specialNotes = "High spenders in every department. Coordinate departments to maximize VIP sales!",
            starThresholds = listOf(18000, 28000, 42000)
        ),
        ShiftConfig(
            shiftNumber = 6,
            title = "Endless Peak Survival",
            subtitle = "Infinite waves of retail chaos",
            shiftDurationSeconds = 999,
            targetRevenue = 50000,
            minCsatPercent = 75,
            spawnIntervalMs = 1800L,
            allowedArchetypes = CustomerArchetype.entries,
            specialNotes = "Survive as long as you can! Test your ultimate Host desk mastery.",
            starThresholds = listOf(20000, 40000, 80000)
        )
    )
}

enum class HostUpgradeType(
    val upgradeId: String,
    val title: String,
    val description: String,
    val costDollars: Int,
    val iconEmoji: String,
    val tierLevels: Int = 3
) {
    GREETER_PODIUM(
        upgradeId = "up_podium",
        title = "Deluxe Host Podium",
        description = "Gives all arriving customers +10% base patience and faster greeting speed.",
        costDollars = 500,
        iconEmoji = "🎙️"
    ),
    WALKIE_RADIO_PRO(
        upgradeId = "up_radio",
        title = "Dual-Channel Walkie Talkie",
        description = "Radio dispatches give 2.5x sales speed boost instead of 2.0x.",
        costDollars = 750,
        iconEmoji = "📻"
    ),
    SECURITY_CCTV(
        upgradeId = "up_cctv",
        title = "AP Loss Prevention Cam",
        description = "Automatically highlights suspicious shoplifters with a red warning badge.",
        costDollars = 1000,
        iconEmoji = "📹"
    ),
    VIP_LANYARD(
        upgradeId = "up_lanyard",
        title = "Golden Host Lanyard",
        description = "Store Reputation starts at +20% and customer spend modifier increases by 15%.",
        costDollars = 1200,
        iconEmoji = "🎗️"
    ),
    INTERCOM_MEGAPHONE(
        upgradeId = "up_intercom",
        title = "Overhead Store PA System",
        description = "Reduces Intercom cooldown by 50% and boosts all departments +25% service rate.",
        costDollars = 1500,
        iconEmoji = "📢"
    ),
    EXPRESS_COFFEE_MAKER(
        upgradeId = "up_coffee",
        title = "Breakroom Espresso Machine",
        description = "Increases player tap response speed and restores +30 patience on Greeting Handshake.",
        costDollars = 900,
        iconEmoji = "☕"
    )
}
