package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DepartmentState
import com.example.model.StoreDepartmentType
import com.example.ui.theme.RetailBlue
import com.example.ui.theme.RetailGreen
import com.example.ui.theme.RetailRed
import com.example.ui.theme.RetailYellow

@Composable
fun DepartmentTile(
    deptState: DepartmentState,
    onDirectClick: () -> Unit,
    onRadioClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlightTarget: Boolean = false
) {
    val dept = deptState.departmentType
    val deptColor = Color(dept.primaryColor)
    val isFull = deptState.isFull

    val icon: ImageVector = when (dept) {
        StoreDepartmentType.HOME_THEATER -> Icons.Filled.Tv
        StoreDepartmentType.COMPUTERS -> Icons.Filled.Laptop
        StoreDepartmentType.DIGITAL_IMAGING -> Icons.Filled.PhotoCamera
        StoreDepartmentType.PORTABLE_AUDIO -> Icons.Filled.Headphones
        StoreDepartmentType.SMART_HOME -> Icons.Filled.Smartphone
        StoreDepartmentType.APPLIANCES -> Icons.Filled.Kitchen
        StoreDepartmentType.STORE_PICKUP -> Icons.Filled.Inventory
        StoreDepartmentType.GEEK_TECH_SUPPORT -> Icons.Filled.Build
        StoreDepartmentType.RESTROOMS -> Icons.Filled.Wc
    }

    val borderColor = when {
        isHighlightTarget -> RetailYellow
        isFull -> RetailRed
        else -> Color(0xFF334155)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E293B))
            .border(if (isHighlightTarget) 2.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onDirectClick() }
            .testTag("dept_tile_${dept.id}")
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(deptColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = dept.shortName,
                            tint = deptColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dept.shortName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Radio Blue Shirt Dispatch Mini Button
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(RetailBlue.copy(alpha = 0.3f))
                        .clickable { onRadioClick() }
                        .testTag("dept_radio_${dept.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Radio Blue Shirt",
                        tint = RetailYellow,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Capacity & Occupancy indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shoppers: ${deptState.occupancy}/${deptState.maxCapacity}",
                    fontSize = 10.sp,
                    color = if (isFull) RetailRed else Color(0xFF94A3B8)
                )

                // Blue shirt staff indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "👕 ${deptState.staffCount}",
                        fontSize = 10.sp,
                        color = Color(0xFF60A5FA),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Active shopping progress
            if (deptState.currentCustomers.isNotEmpty()) {
                val topCustomer = deptState.currentCustomers.first()
                val progress = (topCustomer.progressSeconds / topCustomer.totalServiceRequiredSeconds).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = RetailGreen,
                    trackColor = Color(0xFF334155)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF334155))
                )
            }
        }
    }
}
