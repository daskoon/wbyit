package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import com.example.audio.SoundEngine
import com.example.ui.screens.IntroScreen
import com.example.ui.theme.WhatBringsYouInTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun introScreen_isDisplayed() {
    val soundEngine = SoundEngine().apply { isEnabled = false }
    composeTestRule.setContent {
      WhatBringsYouInTheme(darkTheme = true) {
        IntroScreen(
          soundEngine = soundEngine,
          onFinishIntro = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("intro_screen_container").assertIsDisplayed()
  }
}
