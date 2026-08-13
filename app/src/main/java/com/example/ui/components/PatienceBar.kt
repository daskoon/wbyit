package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RetailGreen
import com.example.ui.theme.RetailRed
import com.example.ui.theme.RetailYellow

@Composable
fun PatienceBar(
    current: Float,
    max: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp
) {
    val fraction = (current / max.coerceAtLeast(1f)).coerceIn(0f, 1f)

    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "patience_fraction"
    )

    val barColor by animateColorAsState(
        targetValue = when {
            fraction > 0.55f -> RetailGreen
            fraction > 0.25f -> RetailYellow
            else -> RetailRed
        },
        label = "patience_color"
    )

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedFraction)
                .clip(RoundedCornerShape(3.dp))
                .background(barColor)
        )
    }
}
