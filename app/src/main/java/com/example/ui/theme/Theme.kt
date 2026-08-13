package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RetailDarkColorScheme = darkColorScheme(
    primary = RetailYellow,
    onPrimary = RetailBlueDark,
    primaryContainer = RetailBlue,
    onPrimaryContainer = Color.White,
    secondary = RetailCyan,
    onSecondary = RetailBlueDark,
    tertiary = RetailGreen,
    onTertiary = RetailBlueDark,
    background = RetailBlueDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondaryDark,
    error = RetailRed,
    onError = Color.White
)

private val RetailLightColorScheme = lightColorScheme(
    primary = RetailBlue,
    onPrimary = Color.White,
    primaryContainer = RetailYellow,
    onPrimaryContainer = RetailBlueDark,
    secondary = RetailCyan,
    onSecondary = RetailBlueDark,
    tertiary = RetailGreen,
    onTertiary = RetailBlueDark,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    error = RetailRed,
    onError = Color.White
)

@Composable
fun WhatBringsYouInTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RetailDarkColorScheme else RetailLightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

