package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEngine
import com.example.data.AppDatabase
import com.example.data.GameProgressEntity
import com.example.data.GameRepository
import com.example.model.CustomerArchetype
import com.example.model.CustomerInstance
import com.example.model.CustomerStatus
import com.example.model.DepartmentState
import com.example.model.GameShiftConfigs
import com.example.model.HostUpgradeType
import com.example.model.RadioMessage
import com.example.model.ShiftConfig
import com.example.model.StoreDepartmentType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

sealed interface GameScreen {
    data object Title : GameScreen
    data object ShiftSelect : GameScreen
    data class Gameplay(val shiftNumber: Int) : GameScreen
    data class ShiftSummary(
        val shiftNumber: Int,
        val revenue: Int,
        val stars: Int,
        val customersServed: Int,
        val angryWalkouts: Int,
        val shopliftersCaught: Int,
        val csatPercent: Int,
        val isPassed: Boolean,
        val wageEarned: Int
    ) : GameScreen
    data object BreakroomUpgrades : GameScreen
    data object CustomerCodex : GameScreen
}

data class GameplayUiState(
    val currentShift: ShiftConfig = GameShiftConfigs.SHIFTS.first(),
    val remainingTimeSeconds: Float = 60f,
    val isPaused: Boolean = false,
    val gameSpeed: Float = 1.0f,
    val revenue: Int = 0,
    val customersServedCount: Int = 0,
    val angryWalkoutCount: Int = 0,
    val shopliftersCaughtCount: Int = 0,
    val queue: List<CustomerInstance> = emptyList(),
    val activeDeskCustomer: CustomerInstance? = null,
    val departments: Map<StoreDepartmentType, DepartmentState> = emptyMap(),
    val radioFeed: List<RadioMessage> = emptyList(),
    val isWalkieSheetOpen: Boolean = false,
    val isIntercomActive: Boolean = false,
    val intercomCooldownRemaining: Float = 0f,
    val couponChargesRemaining: Int = 3,
    val managerCallCooldownRemaining: Float = 0f,
    val securityAlertCooldownRemaining: Float = 0f,
    val floatingNotification: String? = null,
    val isShiftEnded: Boolean = false
) {
    val totalProcessed: Int get() = customersServedCount + angryWalkoutCount
    val csatPercent: Int get() {
        if (totalProcessed == 0) return 100
        return ((customersServedCount.toFloat() / totalProcessed) * 100f).toInt().coerceIn(0, 100)
    }
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(AppDatabase.getInstance(application).gameDao())
    val soundEngine = SoundEngine()

    val gameProgress: StateFlow<GameProgressEntity> = repository.gameProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GameProgressEntity()
    )

    private val _currentScreen = MutableStateFlow<GameScreen>(GameScreen.Title)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    private val _gameplayState = MutableStateFlow(GameplayUiState())
    val gameplayState: StateFlow<GameplayUiState> = _gameplayState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var customerSpawnTimer = 0f
    private var lastRadioRepIndex = 0

    init {
        viewModelScope.launch {
            repository.gameProgress.collect { progress ->
                soundEngine.isEnabled = progress.soundEnabled
            }
        }
    }

    fun navigateTo(screen: GameScreen) {
        if (screen !is GameScreen.Gameplay) {
            stopGameLoop()
        }
        _currentScreen.value = screen
    }

    fun startShift(shiftNumber: Int) {
        val config = GameShiftConfigs.SHIFTS.find { it.shiftNumber == shiftNumber }
            ?: GameShiftConfigs.SHIFTS.first()

        val initialDepts = StoreDepartmentType.entries.associateWith { dept ->
            DepartmentState(
                departmentType = dept,
                staffCount = if (dept == StoreDepartmentType.RESTROOMS) 1 else 1,
                maxCapacity = dept.defaultCapacity,
                staffServiceMultiplier = 1.0f
            )
        }

        val upgrades = gameProgress.value.purchasedUpgrades.split(",")
        val hasPodium = upgrades.contains(HostUpgradeType.GREETER_PODIUM.upgradeId)
        val hasLanyard = upgrades.contains(HostUpgradeType.VIP_LANYARD.upgradeId)
        val initialCouponCount = if (hasLanyard) 5 else 3

        _gameplayState.value = GameplayUiState(
            currentShift = config,
            remainingTimeSeconds = config.shiftDurationSeconds.toFloat(),
            isPaused = false,
            gameSpeed = 1.0f,
            revenue = 0,
            customersServedCount = 0,
            angryWalkoutCount = 0,
            shopliftersCaughtCount = 0,
            queue = emptyList(),
            activeDeskCustomer = null,
            departments = initialDepts,
            radioFeed = listOf(
                RadioMessage(
                    senderName = "MOD Dave",
                    department = StoreDepartmentType.STORE_PICKUP,
                    text = "All staff, host desk is online for ${config.title}! Let's hit our $${config.targetRevenue} goal!"
                )
            ),
            couponChargesRemaining = initialCouponCount,
            floatingNotification = "Shift Started! Welcome customers at the Host Desk!"
        )

        _currentScreen.value = GameScreen.Gameplay(shiftNumber)
        soundEngine.playDoorChime()
        startGameLoop()
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            val tickIntervalMs = 50L
            while (isActive) {
                delay(tickIntervalMs)
                val state = _gameplayState.value
                if (state.isPaused || state.isShiftEnded) continue

                val dt = (tickIntervalMs / 1000f) * state.gameSpeed
                updateGameTick(dt)
            }
        }
    }

    private fun stopGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    private fun updateGameTick(dt: Float) {
        _gameplayState.update { state ->
            val newTime = (state.remainingTimeSeconds - dt).coerceAtLeast(0f)
            val isIntercom = state.isIntercomActive
            val newIntercomCooldown = (state.intercomCooldownRemaining - dt).coerceAtLeast(0f)
            val newManagerCooldown = (state.managerCallCooldownRemaining - dt).coerceAtLeast(0f)
            val newSecurityCooldown = (state.securityAlertCooldownRemaining - dt).coerceAtLeast(0f)

            // 1. Check Shift Time Over
            if (newTime <= 0f && !state.isShiftEnded) {
                onShiftComplete(state)
                return@update state.copy(
                    remainingTimeSeconds = 0f,
                    isShiftEnded = true
                )
            }

            // 2. Spawn Customers
            customerSpawnTimer += dt
            var newQueue = state.queue.toMutableList()
            var activeCustomer = state.activeDeskCustomer

            val spawnIntervalSec = (state.currentShift.spawnIntervalMs / 1000f) / state.gameSpeed
            if (customerSpawnTimer >= spawnIntervalSec && (newQueue.size + (if (activeCustomer != null) 1 else 0)) < 6) {
                customerSpawnTimer = 0f
                val spawned = generateNewCustomer(state.currentShift)
                newQueue.add(spawned)
                soundEngine.playDoorChime()
            }

            // 3. Move queue to Active Desk if empty
            if (activeCustomer == null && newQueue.isNotEmpty()) {
                activeCustomer = newQueue.removeAt(0).copy(status = CustomerStatus.AT_HOST_DESK)
            }

            // 4. Update Patience of Active Desk Customer & Queue
            var angryWalkouts = state.angryWalkoutCount
            val upgrades = gameProgress.value.purchasedUpgrades.split(",")
            val hasPodium = upgrades.contains(HostUpgradeType.GREETER_PODIUM.upgradeId)
            val patienceDecayMod = if (hasPodium) 0.85f else 1.0f

            if (activeCustomer != null) {
                val decay = (dt * 6.0f * activeCustomer.patienceDecayRate * patienceDecayMod)
                val updatedPatience = (activeCustomer.currentPatience - decay).coerceAtLeast(0f)
                activeCustomer.currentPatience = updatedPatience

                if (updatedPatience <= 0f) {
                    // Customer storms out!
                    soundEngine.playCustomerAngry()
                    angryWalkouts++
                    activeCustomer = null
                }
            }

            // Decay queue patience slightly
            val updatedQueue = mutableListOf<CustomerInstance>()
            for (c in newQueue) {
                val decay = (dt * 2.5f * patienceDecayMod)
                val newP = (c.currentPatience - decay).coerceAtLeast(0f)
                if (newP > 0f) {
                    updatedQueue.add(c.copy(currentPatience = newP))
                } else {
                    soundEngine.playCustomerAngry()
                    angryWalkouts++
                }
            }

            // 5. Update Departments progress & service
            var newRevenue = state.revenue
            var newServed = state.customersServedCount
            val updatedDepts = state.departments.mapValues { (_, deptState) ->
                val deptCopy = deptState.copy(
                    currentCustomers = deptState.currentCustomers.toMutableList()
                )
                val speedMultiplier = (deptState.staffServiceMultiplier) * (if (isIntercom) 1.4f else 1.0f)

                val finishedList = mutableListOf<CustomerInstance>()
                for (cust in deptCopy.currentCustomers) {
                    val boost = if (cust.isRadioAssisted) 2.2f else 1.0f
                    val couponMult = if (cust.isCouponApplied) 1.25f else 1.0f
                    cust.progressSeconds += dt * speedMultiplier * boost

                    if (cust.progressSeconds >= cust.totalServiceRequiredSeconds) {
                        finishedList.add(cust)
                        if (cust.archetype.isShoplifter && !cust.isSecurityAlerted) {
                            // Shoplifter escaped undetected!
                            soundEngine.playCustomerAngry()
                        } else {
                            // Sale Complete!
                            soundEngine.playCashRegister()
                            val earned = (cust.potentialSpending * couponMult).toInt()
                            newRevenue += earned
                            newServed++
                        }
                    }
                }
                deptCopy.currentCustomers.removeAll(finishedList)
                deptCopy
            }

            state.copy(
                remainingTimeSeconds = newTime,
                revenue = newRevenue,
                customersServedCount = newServed,
                angryWalkoutCount = angryWalkouts,
                activeDeskCustomer = activeCustomer,
                queue = updatedQueue,
                departments = updatedDepts,
                intercomCooldownRemaining = newIntercomCooldown,
                managerCallCooldownRemaining = newManagerCooldown,
                securityAlertCooldownRemaining = newSecurityCooldown
            )
        }
    }

    private fun generateNewCustomer(shift: ShiftConfig): CustomerInstance {
        val archetypes = shift.allowedArchetypes
        val selectedArchetype = archetypes.random()
        val quote = selectedArchetype.initialQuotes.random()
        val intent = selectedArchetype.revealedIntents.random()
        val spend = (selectedArchetype.avgSpending * (0.8f + Random.nextFloat() * 0.5f)).toInt()

        return CustomerInstance(
            id = UUID.randomUUID().toString(),
            archetype = selectedArchetype,
            currentDialogue = quote,
            revealedIntent = intent,
            targetDepartment = selectedArchetype.preferredDepartment,
            currentPatience = selectedArchetype.defaultPatience,
            maxPatience = selectedArchetype.defaultPatience,
            totalServiceRequiredSeconds = selectedArchetype.preferredDepartment.baseServiceSeconds,
            potentialSpending = spend
        )
    }

    // --- Host Interactive Player Actions ---

    fun greetActiveCustomer() {
        val current = _gameplayState.value.activeDeskCustomer ?: return
        if (current.isGreeted) return

        soundEngine.playScannerBeep()
        val upgrades = gameProgress.value.purchasedUpgrades.split(",")
        val hasCoffee = upgrades.contains(HostUpgradeType.EXPRESS_COFFEE_MAKER.upgradeId)
        val patienceBoost = if (hasCoffee) 35f else 20f

        _gameplayState.update { state ->
            val updated = current.copy(
                isGreeted = true,
                isIntentRevealed = true,
                currentDialogue = current.revealedIntent,
                currentPatience = (current.currentPatience + patienceBoost).coerceAtMost(current.maxPatience + 20f)
            )
            state.copy(
                activeDeskCustomer = updated,
                floatingNotification = "Greeted: ${current.archetype.title} revealed what they need!"
            )
        }
    }

    fun directCustomerToDepartment(targetDept: StoreDepartmentType) {
        val current = _gameplayState.value.activeDeskCustomer ?: return
        val correctDept = current.targetDepartment

        if (targetDept == correctDept) {
            // Correct Department Direct!
            val deptState = _gameplayState.value.departments[targetDept]
            if (deptState != null && deptState.isFull) {
                // Dept is full!
                _gameplayState.update { it.copy(floatingNotification = "${targetDept.displayName} is at full capacity! Call Radio for backup!") }
                soundEngine.playCustomerAngry()
                return
            }

            soundEngine.playCustomerHappy()
            _gameplayState.update { state ->
                val depts = state.departments.toMutableMap()
                val targetDeptState = depts[targetDept]
                if (targetDeptState != null) {
                    val updatedCust = current.copy(
                        status = CustomerStatus.WALKING_TO_DEPT
                    )
                    targetDeptState.currentCustomers.add(updatedCust)
                }

                state.copy(
                    activeDeskCustomer = null,
                    departments = depts,
                    floatingNotification = "Direct to ${targetDept.shortName}! Reps are assisting."
                )
            }
        } else {
            // Wrong Department Direct!
            soundEngine.playCustomerAngry()
            _gameplayState.update { state ->
                val updatedPatience = (current.currentPatience - 30f).coerceAtLeast(0f)
                val updatedCust = current.copy(
                    currentDialogue = "Wrong department! I don't want ${targetDept.shortName}, I asked for ${correctDept.shortName}!",
                    currentPatience = updatedPatience
                )
                if (updatedPatience <= 0f) {
                    state.copy(
                        activeDeskCustomer = null,
                        angryWalkoutCount = state.angryWalkoutCount + 1,
                        floatingNotification = "Customer stormed out due to bad directions!"
                    )
                } else {
                    state.copy(
                        activeDeskCustomer = updatedCust,
                        floatingNotification = "Wrong Department! Customer lost patience."
                    )
                }
            }
        }
    }

    fun dispatchRadioCall(targetDept: StoreDepartmentType) {
        val current = _gameplayState.value.activeDeskCustomer
        soundEngine.playWalkieSquelch()

        val reps = listOf("Marcus", "Sarah", "Tyler", "Chloe", "Alex", "Zack")
        val repName = reps[(lastRadioRepIndex++) % reps.size]

        val radioMsg = RadioMessage(
            senderName = "Rep $repName",
            department = targetDept,
            text = "Copy Host Desk! Meeting customer right at the ${targetDept.shortName} entrance with keys ready!"
        )

        _gameplayState.update { state ->
            val updatedFeed = (listOf(radioMsg) + state.radioFeed).take(8)
            var updatedCustomer = state.activeDeskCustomer
            if (updatedCustomer != null && updatedCustomer.targetDepartment == targetDept) {
                updatedCustomer = updatedCustomer.copy(
                    isRadioAssisted = true,
                    currentPatience = (updatedCustomer.currentPatience + 25f).coerceAtMost(updatedCustomer.maxPatience + 20f)
                )
            }
            state.copy(
                activeDeskCustomer = updatedCustomer,
                radioFeed = updatedFeed,
                floatingNotification = "Walkie: Rep $repName dispatched to ${targetDept.shortName}!"
            )
        }
    }

    fun triggerIntercomAnnouncement() {
        val state = _gameplayState.value
        if (state.intercomCooldownRemaining > 0f) return

        soundEngine.playIntercom()
        val upgrades = gameProgress.value.purchasedUpgrades.split(",")
        val hasPA = upgrades.contains(HostUpgradeType.INTERCOM_MEGAPHONE.upgradeId)
        val cooldown = if (hasPA) 15f else 30f

        _gameplayState.update {
            it.copy(
                isIntercomActive = true,
                intercomCooldownRemaining = cooldown,
                floatingNotification = "📢 Overhead PA Announcement: All department speeds boosted +40%!"
            )
        }

        viewModelScope.launch {
            delay(12000L)
            _gameplayState.update { it.copy(isIntercomActive = false) }
        }
    }

    fun applyCoupon() {
        val current = _gameplayState.value.activeDeskCustomer ?: return
        val state = _gameplayState.value
        if (state.couponChargesRemaining <= 0 || current.isCouponApplied) return

        soundEngine.playCustomerHappy()
        _gameplayState.update {
            val updated = current.copy(
                isCouponApplied = true,
                currentPatience = (current.currentPatience + 35f).coerceAtMost(current.maxPatience + 25f),
                potentialSpending = (current.potentialSpending * 1.25f).toInt()
            )
            it.copy(
                activeDeskCustomer = updated,
                couponChargesRemaining = it.couponChargesRemaining - 1,
                floatingNotification = "🎟️ 10% VIP Coupon applied! Spending +25%!"
            )
        }
    }

    fun callManagerToDesk() {
        val current = _gameplayState.value.activeDeskCustomer ?: return
        val state = _gameplayState.value
        if (state.managerCallCooldownRemaining > 0f) return

        soundEngine.playWalkieSquelch()
        _gameplayState.update {
            val updated = current.copy(
                isManagerAssisted = true,
                currentPatience = current.maxPatience,
                currentDialogue = "Manager Dave: 'I apologize for the inconvenience! Let me authorize that price match right away.'"
            )
            it.copy(
                activeDeskCustomer = updated,
                managerCallCooldownRemaining = 20f,
                floatingNotification = "👔 Manager on Duty stepped in to calm customer!"
            )
        }
    }

    fun triggerSecurityAlert() {
        val current = _gameplayState.value.activeDeskCustomer ?: return
        val state = _gameplayState.value
        if (state.securityAlertCooldownRemaining > 0f) return

        if (current.archetype.isShoplifter) {
            // Caught shoplifter!
            soundEngine.playSecurityAlarm()
            val recoveredValue = 1500
            _gameplayState.update {
                it.copy(
                    activeDeskCustomer = null,
                    revenue = it.revenue + recoveredValue,
                    shopliftersCaughtCount = it.shopliftersCaughtCount + 1,
                    securityAlertCooldownRemaining = 15f,
                    floatingNotification = "🚨 AP Security stopped shoplifter! +$$recoveredValue inventory saved!"
                )
            }
        } else {
            // False alarm!
            soundEngine.playCustomerAngry()
            _gameplayState.update {
                val updated = current.copy(
                    currentPatience = (current.currentPatience - 40f).coerceAtLeast(0f),
                    currentDialogue = "Excuse me?! Why is security staring at my shopping bag?!"
                )
                it.copy(
                    activeDeskCustomer = updated,
                    securityAlertCooldownRemaining = 15f,
                    floatingNotification = "⚠️ False Alarm! Innocent customer was insulted!"
                )
            }
        }
    }

    fun togglePause() {
        _gameplayState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun toggleGameSpeed() {
        _gameplayState.update {
            val nextSpeed = when (it.gameSpeed) {
                1.0f -> 1.5f
                1.5f -> 2.0f
                else -> 1.0f
            }
            it.copy(gameSpeed = nextSpeed)
        }
    }

    fun toggleWalkieSheet(open: Boolean) {
        _gameplayState.update { it.copy(isWalkieSheetOpen = open) }
    }

    private fun onShiftComplete(state: GameplayUiState) {
        stopGameLoop()
        soundEngine.playVictoryFanfare()

        val shift = state.currentShift
        val rev = state.revenue
        val stars = when {
            rev >= shift.starThresholds[2] -> 3
            rev >= shift.starThresholds[1] -> 2
            rev >= shift.starThresholds[0] -> 1
            else -> 0
        }
        val isPassed = stars >= 1
        val wageEarned = (rev * 0.10f).toInt() + (stars * 150)

        val discovered = shift.allowedArchetypes.map { it.archetypeId }

        viewModelScope.launch {
            repository.recordShiftResult(
                shiftNumber = shift.shiftNumber,
                starsEarned = stars,
                revenueEarned = wageEarned,
                customersServed = state.customersServedCount,
                shopliftersCaught = state.shopliftersCaughtCount,
                shiftCsat = state.csatPercent,
                newDiscoveredArchetypes = discovered
            )
        }

        _currentScreen.value = GameScreen.ShiftSummary(
            shiftNumber = shift.shiftNumber,
            revenue = rev,
            stars = stars,
            customersServed = state.customersServedCount,
            angryWalkouts = state.angryWalkoutCount,
            shopliftersCaught = state.shopliftersCaughtCount,
            csatPercent = state.csatPercent,
            isPassed = isPassed,
            wageEarned = wageEarned
        )
    }

    fun purchaseUpgrade(upgrade: HostUpgradeType) {
        viewModelScope.launch {
            val success = repository.purchaseUpgrade(upgrade.upgradeId, upgrade.costDollars)
            if (success) {
                soundEngine.playCashRegister()
            } else {
                soundEngine.playCustomerAngry()
            }
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            val current = repository.getProgress()
            repository.saveProgress(current.copy(soundEnabled = enabled))
        }
    }
}
