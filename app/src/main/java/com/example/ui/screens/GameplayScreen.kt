package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerInstance
import com.example.model.CustomerStatus
import com.example.model.StoreDepartmentType
import com.example.ui.components.DepartmentTile
import com.example.ui.components.PatienceBar
import com.example.ui.components.PixelAvatar
import com.example.ui.components.WalkieTalkieSheet
import com.example.ui.theme.RetailBlue
import com.example.ui.theme.RetailBlueDark
import com.example.ui.theme.RetailCyan
import com.example.ui.theme.RetailGreen
import com.example.ui.theme.RetailOrange
import com.example.ui.theme.RetailRed
import com.example.ui.theme.RetailYellow
import com.example.viewmodel.GameplayUiState

@Composable
fun GameplayScreen(
    state: GameplayUiState,
    onGreet: () -> Unit,
    onDirectDept: (StoreDepartmentType) -> Unit,
    onRadioDispatch: (StoreDepartmentType) -> Unit,
    onIntercom: () -> Unit,
    onCoupon: () -> Unit,
    onManager: () -> Unit,
    onSecurity: () -> Unit,
    onTogglePause: () -> Unit,
    onToggleSpeed: () -> Unit,
    onToggleWalkie: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RetailBlueDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (state.isWalkieSheetOpen) 220.dp else 0.dp)
        ) {
            // 1. TOP HUD BAR
            TopHudBar(
                state = state,
                onTogglePause = onTogglePause,
                onToggleSpeed = onToggleSpeed,
                onIntercom = onIntercom,
                onOpenWalkie = { onToggleWalkie(true) }
            )

            // Scrollable Content: Host Desk Podium + Line + Departments
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // 2. ACTIVE CUSTOMER AT HOST DESK PODIUM
                ActiveHostDeskSection(
                    customer = state.activeDeskCustomer,
                    couponCount = state.couponChargesRemaining,
                    managerCooldown = state.managerCallCooldownRemaining,
                    securityCooldown = state.securityAlertCooldownRemaining,
                    onGreet = onGreet,
                    onDirectDept = onDirectDept,
                    onCoupon = onCoupon,
                    onManager = onManager,
                    onSecurity = onSecurity
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. STORE ENTRANCE QUEUE LINE
                EntranceQueueSection(queue = state.queue)

                Spacer(modifier = Modifier.height(10.dp))

                // 4. LIVE DEPARTMENT TILES
                Text(
                    text = "STORE DEPARTMENTS (LIVE STATUS)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = RetailYellow
                )

                Spacer(modifier = Modifier.height(6.dp))

                val deptList = StoreDepartmentType.entries.filter { it != StoreDepartmentType.RESTROOMS }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = true
                ) {
                    items(deptList) { deptType ->
                        val deptState = state.departments[deptType]
                        if (deptState != null) {
                            DepartmentTile(
                                deptState = deptState,
                                onDirectClick = { onDirectDept(deptType) },
                                onRadioClick = { onRadioDispatch(deptType) },
                                isHighlightTarget = state.activeDeskCustomer?.targetDepartment == deptType
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Floating Notification Toast
        AnimatedVisibility(
            visible = state.floatingNotification != null,
            enter = fadeIn() + slideInVertically { -40 },
            exit = fadeOut() + slideOutVertically { -40 },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                    .border(1.dp, RetailYellow, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = state.floatingNotification ?: "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RetailYellow,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Walkie Talkie Bottom Sheet Overlay
        AnimatedVisibility(
            visible = state.isWalkieSheetOpen,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            WalkieTalkieSheet(
                radioFeed = state.radioFeed,
                onDispatchDept = { dept ->
                    onRadioDispatch(dept)
                    onToggleWalkie(false)
                },
                onClose = { onToggleWalkie(false) }
            )
        }

        // Pause Modal Overlay
        if (state.isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, RetailYellow)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SHIFT PAUSED",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = RetailYellow
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Take a breather! Clock is stopped.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onTogglePause,
                            colors = ButtonDefaults.buttonColors(containerColor = RetailYellow),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("RESUME SHIFT", fontWeight = FontWeight.Bold, color = RetailBlueDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopHudBar(
    state: GameplayUiState,
    onTogglePause: () -> Unit,
    onToggleSpeed: () -> Unit,
    onIntercom: () -> Unit,
    onOpenWalkie: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift Clock Timer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LockClock,
                    contentDescription = null,
                    tint = RetailYellow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val minutes = (state.remainingTimeSeconds.toInt() / 60)
                val seconds = (state.remainingTimeSeconds.toInt() % 60)
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (state.remainingTimeSeconds <= 15f) RetailRed else Color.White
                )
            }

            // Revenue Meter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$${state.revenue}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = RetailGreen
                )
                Text(
                    text = "TARGET: $${state.currentShift.targetRevenue}",
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // CSAT % Meter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.csatPercent}%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (state.csatPercent >= 75) RetailCyan else RetailOrange
                )
                Text(
                    text = "CSAT",
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Quick Control Buttons (PA, Walkie, Speed, Pause)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Intercom PA Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (state.intercomCooldownRemaining > 0f) Color(0xFF334155) else RetailOrange)
                        .clickable(enabled = state.intercomCooldownRemaining <= 0f) { onIntercom() }
                        .testTag("intercom_hud_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Campaign,
                        contentDescription = "Overhead PA",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Walkie Talkie Sheet Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(RetailYellow)
                        .clickable { onOpenWalkie() }
                        .testTag("walkie_hud_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Radio,
                        contentDescription = "Walkie Talkie",
                        tint = RetailBlueDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Speed Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(RetailBlue)
                        .clickable { onToggleSpeed() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("speed_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.gameSpeed}x",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Pause Button
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("pause_button")
                ) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveHostDeskSection(
    customer: CustomerInstance?,
    couponCount: Int,
    managerCooldown: Float,
    securityCooldown: Float,
    onGreet: () -> Unit,
    onDirectDept: (StoreDepartmentType) -> Unit,
    onCoupon: () -> Unit,
    onManager: () -> Unit,
    onSecurity: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("host_podium_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (customer?.archetype?.isShoplifter == true) RetailRed else RetailBlue
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Podium Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HOST DESK PODIUM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = RetailYellow
                    )
                    if (customer?.archetype?.isVIP == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(RetailYellow)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VIP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = RetailBlueDark
                            )
                        }
                    }
                }

                if (customer != null) {
                    Text(
                        text = "Potential: $${customer.potentialSpending}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RetailGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (customer != null) {
                // Active Customer Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelAvatar(
                        archetype = customer.archetype,
                        size = 56.dp,
                        isAngry = customer.currentPatience < 25f,
                        isHappy = customer.isCouponApplied || customer.isGreeted
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customer.archetype.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Patience Meter
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Patience:",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            PatienceBar(
                                current = customer.currentPatience,
                                max = customer.maxPatience,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Comic Speech Bubble
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "“${customer.currentDialogue}”",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFF1F5F9)
                        )

                        if (customer.isGreeted) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TARGET: ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = RetailYellow
                                )
                                Text(
                                    text = customer.targetDepartment.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(customer.targetDepartment.primaryColor)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // PRIMARY GREET BUTTON ("WHAT BRINGS YOU IN TODAY?")
                if (!customer.isGreeted) {
                    Button(
                        onClick = onGreet,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("greet_customer_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RetailYellow)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QuestionAnswer,
                            contentDescription = null,
                            tint = RetailBlueDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "“WHAT BRINGS YOU IN TODAY?” (GREET)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = RetailBlueDark
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // DIRECT BUTTONS FOR QUICK ROUTING
                Text(
                    text = "DIRECT TO DEPARTMENT:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        StoreDepartmentType.HOME_THEATER,
                        StoreDepartmentType.COMPUTERS,
                        StoreDepartmentType.STORE_PICKUP,
                        StoreDepartmentType.GEEK_TECH_SUPPORT
                    ).forEach { dept ->
                        val isTarget = customer.isGreeted && customer.targetDepartment == dept
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTarget) RetailYellow else RetailBlue)
                                .clickable { onDirectDept(dept) }
                                .padding(vertical = 8.dp)
                                .testTag("quick_direct_${dept.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dept.shortName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTarget) RetailBlueDark else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        StoreDepartmentType.APPLIANCES,
                        StoreDepartmentType.DIGITAL_IMAGING,
                        StoreDepartmentType.PORTABLE_AUDIO,
                        StoreDepartmentType.RESTROOMS
                    ).forEach { dept ->
                        val isTarget = customer.isGreeted && customer.targetDepartment == dept
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTarget) RetailYellow else Color(0xFF334155))
                                .clickable { onDirectDept(dept) }
                                .padding(vertical = 8.dp)
                                .testTag("quick_direct_${dept.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dept.shortName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTarget) RetailBlueDark else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // INTERVENTION ACTION BUTTONS ROW (Coupon, Manager, Security)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Coupon button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (couponCount > 0) RetailCyan.copy(alpha = 0.2f) else Color(0xFF334155))
                            .border(1.dp, if (couponCount > 0) RetailCyan else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable(enabled = couponCount > 0) { onCoupon() }
                            .padding(vertical = 6.dp)
                            .testTag("coupon_action_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.ConfirmationNumber, contentDescription = null, tint = RetailCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Coupon ($couponCount)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RetailCyan)
                        }
                    }

                    // Call Manager button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (managerCooldown <= 0f) RetailOrange.copy(alpha = 0.2f) else Color(0xFF334155))
                            .border(1.dp, if (managerCooldown <= 0f) RetailOrange else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable(enabled = managerCooldown <= 0f) { onManager() }
                            .padding(vertical = 6.dp)
                            .testTag("manager_action_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.SupervisorAccount, contentDescription = null, tint = RetailOrange, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (managerCooldown > 0f) "${managerCooldown.toInt()}s" else "Call MOD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RetailOrange
                            )
                        }
                    }

                    // Security alert button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (securityCooldown <= 0f) RetailRed.copy(alpha = 0.2f) else Color(0xFF334155))
                            .border(1.dp, if (securityCooldown <= 0f) RetailRed else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable(enabled = securityCooldown <= 0f) { onSecurity() }
                            .padding(vertical = 6.dp)
                            .testTag("security_action_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = RetailRed, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (securityCooldown > 0f) "${securityCooldown.toInt()}s" else "AP Security",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RetailRed
                            )
                        }
                    }
                }
            } else {
                // Empty Desk state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Podium clear! Next customer walking up...",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun EntranceQueueSection(queue: List<CustomerInstance>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STORE ENTRANCE QUEUE (${queue.size}/5)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = RetailYellow
            )
            Text(
                text = if (queue.isEmpty()) "Doors Quiet" else "Waiting in Line",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (queue.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(queue) { customer ->
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PixelAvatar(archetype = customer.archetype, size = 36.dp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = customer.archetype.avatarEmoji,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            PatienceBar(
                                current = customer.currentPatience,
                                max = customer.maxPatience,
                                height = 4.dp
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Automatic sliding doors ready for shoppers",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
