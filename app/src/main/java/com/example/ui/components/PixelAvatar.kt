package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerArchetype

@Composable
fun PixelAvatar(
    archetype: CustomerArchetype,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    isAngry: Boolean = false,
    isHappy: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(archetype.shirtColorHex).copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Head / Hair base
            val skinColor = Color(0xFFFFCC80)
            val shirtColor = Color(archetype.shirtColorHex)

            // Shoulders & Shirt
            drawRect(
                color = shirtColor,
                topLeft = Offset(w * 0.15f, h * 0.65f),
                size = Size(w * 0.70f, h * 0.35f)
            )

            // Shirt collar
            drawRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(w * 0.40f, h * 0.65f),
                size = Size(w * 0.20f, h * 0.15f)
            )

            // Head / Face
            drawRect(
                color = skinColor,
                topLeft = Offset(w * 0.25f, h * 0.22f),
                size = Size(w * 0.50f, h * 0.45f)
            )

            // Eyes
            val eyeColor = if (isAngry) Color(0xFFFF1744) else Color(0xFF1E293B)
            drawRect(
                color = eyeColor,
                topLeft = Offset(w * 0.35f, h * 0.35f),
                size = Size(w * 0.08f, if (isAngry) h * 0.04f else h * 0.08f)
            )
            drawRect(
                color = eyeColor,
                topLeft = Offset(w * 0.57f, h * 0.35f),
                size = Size(w * 0.08f, if (isAngry) h * 0.04f else h * 0.08f)
            )

            // Eyebrows
            if (isAngry) {
                drawLine(
                    color = Color.Black,
                    start = Offset(w * 0.32f, h * 0.30f),
                    end = Offset(w * 0.44f, h * 0.34f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(w * 0.68f, h * 0.30f),
                    end = Offset(w * 0.56f, h * 0.34f),
                    strokeWidth = 3f
                )
            }

            // Mouth
            val mouthColor = Color(0xFF795548)
            if (isAngry) {
                drawRect(
                    color = mouthColor,
                    topLeft = Offset(w * 0.40f, h * 0.54f),
                    size = Size(w * 0.20f, h * 0.04f)
                )
            } else if (isHappy) {
                drawRect(
                    color = mouthColor,
                    topLeft = Offset(w * 0.38f, h * 0.50f),
                    size = Size(w * 0.24f, h * 0.08f)
                )
            } else {
                drawRect(
                    color = mouthColor,
                    topLeft = Offset(w * 0.42f, h * 0.52f),
                    size = Size(w * 0.16f, h * 0.04f)
                )
            }
        }

        // Archetype overlay emoji badge
        Text(
            text = archetype.avatarEmoji,
            fontSize = (size.value * 0.38f).sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
