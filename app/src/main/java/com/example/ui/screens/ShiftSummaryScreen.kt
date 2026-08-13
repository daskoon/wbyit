package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RetailBlue
import com.example.ui.theme.RetailBlueDark
import com.example.ui.theme.RetailGreen
import com.example.ui.theme.RetailRed
import com.example.ui.theme.RetailYellow
import com.example.viewmodel.GameScreen

@Composable
fun ShiftSummaryScreen(
    summary: GameScreen.ShiftSummary,
    onNextShift: (Int) -> Unit,
    onRetryShift: (Int) -> Unit,
    onScheduleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gmQuote = when (summary.stars) {
        3 -> "“Incredible performance at the Host Desk! Corporate sent a congratulatory memo and you've unlocked the employee of the month lounge!”"
        2 -> "“Great shift! You kept the blue shirts moving and the front door lines under control. Solid numbers across the board.”"
        1 -> "“You made quota by the skin of your teeth! A few customers stormed out, but we still turned a profit.”"
        else -> "“The District Manager walked in right as 4 customers stormed out in rage... let's review basic greeting protocols and try again!”"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        RetailBlueDark,
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (summary.isPassed) RetailYellow else RetailRed)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (summary.isPassed) "★ SHIFT COMPLETED ★" else "⚠️ SHIFT GOAL MISSED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = RetailBlueDark,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SHIFT #${summary.shiftNumber} EVALUATION",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Gold Stars Rating Display
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isLit = index < summary.stars
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (isLit) RetailYellow else Color(0xFF334155),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // GM Performance Evaluation Quote
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(RetailBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👔", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "General Manager Dave:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RetailYellow
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = gmQuote,
                            fontSize = 12.sp,
                            color = Color(0xFFF1F5F9),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Metrics Grid Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        StatRow(label = "Store Revenue Generated:", value = "$${summary.revenue}", valueColor = RetailGreen)
                        StatRow(label = "Customer Satisfaction (CSAT):", value = "${summary.csatPercent}%", valueColor = Color(0xFF00E5FF))
                        StatRow(label = "Customers Assisted:", value = "${summary.customersServed}", valueColor = Color.White)
                        StatRow(label = "Angry Walkouts:", value = "${summary.angryWalkouts}", valueColor = if (summary.angryWalkouts > 0) RetailRed else RetailGreen)
                        StatRow(label = "Shoplifters Caught:", value = "${summary.shopliftersCaught}", valueColor = RetailYellow)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF334155))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        StatRow(
                            label = "Personal Wages + Bonus Earned:",
                            value = "+$${summary.wageEarned}",
                            valueColor = RetailGreen,
                            isBold = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (summary.isPassed && summary.shiftNumber < 6) {
                    Button(
                        onClick = { onNextShift(summary.shiftNumber + 1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("next_shift_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RetailYellow)
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, tint = RetailBlueDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "PROCEED TO SHIFT #${summary.shiftNumber + 1}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = RetailBlueDark)
                    }
                }

                Button(
                    onClick = { onRetryShift(summary.shiftNumber) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("retry_shift_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RetailBlue)
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "REPLAY THIS SHIFT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = onScheduleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("back_to_schedule_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Icon(imageVector = Icons.Filled.List, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "VIEW WORK SCHEDULE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontFamily = FontFamily.Monospace
        )
    }
}
