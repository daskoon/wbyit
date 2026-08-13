package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameProgressEntity
import com.example.model.GameShiftConfigs
import com.example.model.ShiftConfig
import com.example.ui.theme.RetailBlue
import com.example.ui.theme.RetailBlueDark
import com.example.ui.theme.RetailGreen
import com.example.ui.theme.RetailRed
import com.example.ui.theme.RetailYellow

@Composable
fun ShiftSelectScreen(
    progress: GameProgressEntity,
    onSelectShift: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val starsList = progress.shiftStarsJson.split(",").map { it.toIntOrNull() ?: 0 }
    val highScores = progress.shiftHighScoresJson.split(",").map { it.toIntOrNull() ?: 0 }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RetailBlueDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_to_title_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = RetailYellow
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "STORE WORK SCHEDULE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = RetailYellow
                    )
                    Text(
                        text = "Select your host shift to clock in",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shift List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(GameShiftConfigs.SHIFTS) { shift ->
                    val isUnlocked = shift.shiftNumber <= progress.unlockedShift
                    val stars = starsList.getOrElse(shift.shiftNumber - 1) { 0 }
                    val highScore = highScores.getOrElse(shift.shiftNumber - 1) { 0 }

                    ShiftCard(
                        shift = shift,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        highScore = highScore,
                        onPlayClick = { if (isUnlocked) onSelectShift(shift.shiftNumber) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShiftCard(
    shift: ShiftConfig,
    isUnlocked: Boolean,
    stars: Int,
    highScore: Int,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isUnlocked) (if (stars > 0) RetailGreen else RetailBlue) else Color(0xFF334155),
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isUnlocked) { onPlayClick() }
            .testTag("shift_card_${shift.shiftNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF0F172A)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) RetailBlue else Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(
                                text = "#${shift.shiftNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = shift.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color.White else Color(0xFF64748B)
                        )
                        Text(
                            text = shift.subtitle,
                            fontSize = 11.sp,
                            color = if (isUnlocked) Color(0xFF94A3B8) else Color(0xFF475569)
                        )
                    }
                }

                // Stars display
                if (isUnlocked) {
                    Row {
                        repeat(3) { index ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (index < stars) RetailYellow else Color(0xFF334155),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Shift specs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TARGET REVENUE",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "$${shift.targetRevenue}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RetailGreen
                    )
                }

                Column {
                    Text(
                        text = "DURATION",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = if (shift.shiftDurationSeconds > 300) "Endless" else "${shift.shiftDurationSeconds}s",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RetailYellow
                    )
                }

                Column {
                    Text(
                        text = "HIGH SCORE",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "$$highScore",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = shift.specialNotes,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1)
            )

            if (isUnlocked) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("play_shift_${shift.shiftNumber}_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RetailYellow)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = RetailBlueDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "START THIS SHIFT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = RetailBlueDark
                    )
                }
            }
        }
    }
}
