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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.model.HostUpgradeType
import com.example.ui.theme.RetailBlue
import com.example.ui.theme.RetailBlueDark
import com.example.ui.theme.RetailGreen
import com.example.ui.theme.RetailYellow

@Composable
fun BreakroomScreen(
    progress: GameProgressEntity,
    onPurchaseUpgrade: (HostUpgradeType) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ownedUpgrades = progress.purchasedUpgrades.split(",").filter { it.isNotEmpty() }.toSet()

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
                    modifier = Modifier.testTag("back_from_breakroom_btn")
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
                        text = "STAFF BREAKROOM & PERKS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = RetailYellow
                    )
                    Text(
                        text = "Spend career wages on host equipment",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Wallet & Career Rank Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, RetailYellow)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RetailBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Coffee,
                                contentDescription = null,
                                tint = RetailYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Current Career Savings:",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "$${progress.totalCareerEarnings}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = RetailGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = progress.careerRank,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RetailYellow
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Upgrades List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(HostUpgradeType.entries) { upgrade ->
                    val isOwned = ownedUpgrades.contains(upgrade.upgradeId)
                    val canAfford = progress.totalCareerEarnings >= upgrade.costDollars

                    UpgradeItemCard(
                        upgrade = upgrade,
                        isOwned = isOwned,
                        canAfford = canAfford,
                        onBuyClick = { onPurchaseUpgrade(upgrade) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UpgradeItemCard(
    upgrade: HostUpgradeType,
    isOwned: Boolean,
    canAfford: Boolean,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isOwned) RetailGreen else Color(0xFF334155),
                RoundedCornerShape(12.dp)
            )
            .testTag("upgrade_card_${upgrade.upgradeId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned) Color(0xFF0F291E) else Color(0xFF1E293B)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = upgrade.iconEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = upgrade.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = upgrade.description,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Buy / Owned Button
            if (isOwned) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RetailGreen.copy(alpha = 0.2f))
                        .border(1.dp, RetailGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Owned",
                            tint = RetailGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "OWNED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = RetailGreen
                        )
                    }
                }
            } else {
                Button(
                    onClick = onBuyClick,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) RetailYellow else Color(0xFF334155),
                        disabledContainerColor = Color(0xFF334155)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("buy_btn_${upgrade.upgradeId}")
                ) {
                    Text(
                        text = "$${upgrade.costDollars}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (canAfford) RetailBlueDark else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
