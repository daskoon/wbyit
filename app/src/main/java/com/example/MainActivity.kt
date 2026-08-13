package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.BreakroomScreen
import com.example.ui.screens.CustomerCodexScreen
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.ShiftSelectScreen
import com.example.ui.screens.ShiftSummaryScreen
import com.example.ui.screens.TitleScreen
import com.example.ui.theme.RetailBlueDark
import com.example.ui.theme.WhatBringsYouInTheme
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatBringsYouInTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RetailBlueDark
                ) {
                    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                        WhatBringsYouInApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun WhatBringsYouInApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val progress by viewModel.gameProgress.collectAsStateWithLifecycle()
    val gameplayState by viewModel.gameplayState.collectAsStateWithLifecycle()

    BackHandler(enabled = currentScreen !is GameScreen.Title) {
        when (currentScreen) {
            is GameScreen.Gameplay -> viewModel.navigateTo(GameScreen.ShiftSelect)
            is GameScreen.ShiftSummary -> viewModel.navigateTo(GameScreen.ShiftSelect)
            is GameScreen.ShiftSelect -> viewModel.navigateTo(GameScreen.Title)
            is GameScreen.BreakroomUpgrades -> viewModel.navigateTo(GameScreen.Title)
            is GameScreen.CustomerCodex -> viewModel.navigateTo(GameScreen.Title)
            GameScreen.Title -> {}
        }
    }

    when (val screen = currentScreen) {
        GameScreen.Title -> {
            TitleScreen(
                progress = progress,
                onStartClick = { viewModel.navigateTo(GameScreen.ShiftSelect) },
                onBreakroomClick = { viewModel.navigateTo(GameScreen.BreakroomUpgrades) },
                onCodexClick = { viewModel.navigateTo(GameScreen.CustomerCodex) },
                onToggleSound = { viewModel.toggleSound(it) }
            )
        }
        GameScreen.ShiftSelect -> {
            ShiftSelectScreen(
                progress = progress,
                onSelectShift = { shiftNum -> viewModel.startShift(shiftNum) },
                onBack = { viewModel.navigateTo(GameScreen.Title) }
            )
        }
        is GameScreen.Gameplay -> {
            GameplayScreen(
                state = gameplayState,
                onGreet = { viewModel.greetActiveCustomer() },
                onDirectDept = { dept -> viewModel.directCustomerToDepartment(dept) },
                onRadioDispatch = { dept -> viewModel.dispatchRadioCall(dept) },
                onIntercom = { viewModel.triggerIntercomAnnouncement() },
                onCoupon = { viewModel.applyCoupon() },
                onManager = { viewModel.callManagerToDesk() },
                onSecurity = { viewModel.triggerSecurityAlert() },
                onTogglePause = { viewModel.togglePause() },
                onToggleSpeed = { viewModel.toggleGameSpeed() },
                onToggleWalkie = { viewModel.toggleWalkieSheet(it) }
            )
        }
        is GameScreen.ShiftSummary -> {
            ShiftSummaryScreen(
                summary = screen,
                onNextShift = { nextShiftNum -> viewModel.startShift(nextShiftNum) },
                onRetryShift = { shiftNum -> viewModel.startShift(shiftNum) },
                onScheduleClick = { viewModel.navigateTo(GameScreen.ShiftSelect) }
            )
        }
        GameScreen.BreakroomUpgrades -> {
            BreakroomScreen(
                progress = progress,
                onPurchaseUpgrade = { upgrade -> viewModel.purchaseUpgrade(upgrade) },
                onBack = { viewModel.navigateTo(GameScreen.Title) }
            )
        }
        GameScreen.CustomerCodex -> {
            CustomerCodexScreen(
                progress = progress,
                onBack = { viewModel.navigateTo(GameScreen.Title) }
            )
        }
    }
}
