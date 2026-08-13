package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
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
import com.example.model.CustomerArchetype
import com.example.ui.components.PixelAvatar
import com.example.ui.theme.RetailBlueDark
import com.example.ui.theme.RetailGreen
import com.example.ui.theme.RetailYellow

@Composable
fun CustomerCodexScreen(
    progress: GameProgressEntity,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discoveredSet = progress.discoveredArchetypes.split(",").filter { it.isNotEmpty() }.toSet()

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
                    modifier = Modifier.testTag("back_from_codex_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = RetailYellow
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Text(
                        text = "CUSTOMER CODEX",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = RetailYellow
                    )
                    Text(
                        text = "Shopper Rogues Gallery (${discoveredSet.size}/${CustomerArchetype.entries.size} Discovered)",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Codex Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(CustomerArchetype.entries) { archetype ->
                    val isDiscovered = discoveredSet.contains(archetype.archetypeId)
                    CodexCustomerCard(archetype = archetype, isDiscovered = isDiscovered)
                }
            }
        }
    }
}

@Composable
private fun CodexCustomerCard(
    archetype: CustomerArchetype,
    isDiscovered: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isDiscovered) Color(0xFF334155) else Color(0xFF1E293B),
                RoundedCornerShape(12.dp)
            )
            .testTag("codex_card_${archetype.archetypeId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDiscovered) Color(0xFF1E293B) else Color(0xFF0F172A)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDiscovered) {
                        PixelAvatar(archetype = archetype, size = 44.dp)
                    } else {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HelpOutline,
                                contentDescription = "Undiscovered",
                                tint = Color(0xFF475569)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isDiscovered) archetype.title else "??? Unknown Shopper",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDiscovered) Color.White else Color(0xFF64748B)
                        )
                        if (isDiscovered) {
                            Text(
                                text = "Preferred: ${archetype.preferredDepartment.shortName}",
                                fontSize = 11.sp,
                                color = Color(archetype.preferredDepartment.primaryColor),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (isDiscovered) {
                    Text(
                        text = "Avg: $${archetype.avgSpending}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RetailGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (isDiscovered) {
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "“${archetype.initialQuotes.first()}”",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Base Patience: ${archetype.defaultPatience.toInt()}s",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    if (archetype.requiresManager) {
                        Text(
                            text = "⚠️ Requires MOD on dispute",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9100)
                        )
                    } else if (archetype.isShoplifter) {
                        Text(
                            text = "🚨 Shoplifter Risk",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF3366)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Encounter this customer during store shifts to unlock their dossier.",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }
        }
    }
}
