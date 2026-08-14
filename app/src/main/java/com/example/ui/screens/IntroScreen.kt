package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.audio.SoundEngine
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun IntroScreen(
    soundEngine: SoundEngine,
    onFinishIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var soundEnabled by remember { mutableStateOf(soundEngine.isEnabled) }
    var crawlSpeedMultiplier by remember { mutableFloatStateOf(1.0f) }

    // Sequence controller
    // 0: Prologue (0 - 4.5s)
    // 1: Logo Zoom + Fanfare (4.5s - 10.5s)
    // 2: Full Crawl (10.5s - 52s)
    // 3: Death Star Reveal (52s - 58s)
    // 4: Complete
    var stage by remember { mutableStateOf(0) }

    // Start fanfare on stage 1
    LaunchedEffect(stage) {
        if (stage == 1 && soundEnabled) {
            soundEngine.playStarWarsFanfare()
        }
    }

    LaunchedEffect(Unit) {
        // Stage 0: Prologue text
        delay(4500L)
        stage = 1 // Logo zoom
        delay(6000L)
        stage = 2 // Crawl begins
        // Crawl runs until it reaches completion or skip
        val baseCrawlDuration = 44000L
        delay(baseCrawlDuration)
        stage = 3 // Death star pan
        delay(6000L)
        stage = 4
        soundEngine.stopIntroMusic()
        onFinishIntro()
    }

    DisposableEffect(Unit) {
        onDispose {
            soundEngine.stopIntroMusic()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tap anywhere during crawl to toggle fast-forward
                if (stage == 2) {
                    crawlSpeedMultiplier = if (crawlSpeedMultiplier == 1.0f) 2.2f else 1.0f
                }
            }
            .testTag("intro_screen_container")
    ) {
        // Continuous Starfield background
        if (stage >= 1) {
            StarfieldCanvas()
        }

        // STAGE 0: PROLOGUE TEXT
        AnimatedVisibility(
            visible = stage == 0,
            enter = fadeIn(tween(1000)),
            exit = fadeOut(tween(1000)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "A long time ago, in a retail galaxy\nnot so far away...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00B0FF),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier
                    .padding(32.dp)
                    .testTag("intro_prologue_text")
            )
        }

        // STAGE 1: BEST BUY LOGO ZOOM-OUT INTO DEEP SPACE
        if (stage == 1) {
            BestBuyLogoZoom()
        }

        // STAGE 2: 3D PERSPECTIVE CRAWL WITH NO TRUNCATION
        if (stage == 2) {
            FullStarWarsCrawl(
                speedMultiplier = crawlSpeedMultiplier,
                onCrawlFinished = {
                    stage = 3
                }
            )
        }

        // STAGE 3: DEATH STAR SUPERSTORE PAN & REVEAL
        if (stage == 3) {
            DeathStarReveal()
        }

        // TOP HUD BAR: Speed multiplier pill + Sound toggle + SKIP INTRO button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (stage == 2 && crawlSpeedMultiplier > 1.0f) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = null,
                            tint = Color(0xFFFFE600),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2.2X (Tap screen to reset)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFE600)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sound Mute/Unmute
                IconButton(
                    onClick = {
                        soundEnabled = !soundEnabled
                        soundEngine.isEnabled = soundEnabled
                        if (!soundEnabled) {
                            soundEngine.stopIntroMusic()
                        } else if (stage >= 1) {
                            soundEngine.playStarWarsFanfare()
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                        .testTag("intro_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeMute,
                        contentDescription = "Toggle Audio",
                        tint = if (soundEnabled) Color(0xFFFFE600) else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // SKIP INTRO BUTTON (Always functional and accessible)
                Button(
                    onClick = {
                        soundEngine.stopIntroMusic()
                        onFinishIntro()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE600)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("skip_intro_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SKIP INTRO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BestBuyLogoZoom() {
    val scale = remember { Animatable(1.5f) }
    val alpha = remember { Animatable(1.0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 0.04f,
            animationSpec = tween(durationMillis = 5800, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        delay(4000L)
        alpha.animateTo(
            targetValue = 0.0f,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
                .testTag("intro_best_buy_logo")
        ) {
            Text(
                text = "BEST",
                fontSize = 88.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                lineHeight = 84.sp
            )
            Text(
                text = "BUY",
                fontSize = 88.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                lineHeight = 84.sp
            )
        }
    }
}

@Composable
private fun FullStarWarsCrawl(
    speedMultiplier: Float,
    onCrawlFinished: () -> Unit
) {
    // Starting offset well below the screen (850f) and moves all the way past the top (-2400f)
    // ensuring EVERY paragraph, line, and the final climactic sentence finishes cleanly
    val startY = 850f
    val endY = -2400f
    val crawlOffset = remember { Animatable(startY) }

    LaunchedEffect(speedMultiplier) {
        val remaining = crawlOffset.value - endY
        if (remaining > 0) {
            val totalDistance = startY - endY // 3250f
            val baseTimeMs = 42000L
            val targetDuration = ((remaining / totalDistance) * baseTimeMs / speedMultiplier).toLong()
            crawlOffset.animateTo(
                targetValue = endY,
                animationSpec = tween(durationMillis = targetDuration.coerceAtLeast(100).toInt(), easing = LinearEasing)
            )
            onCrawlFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                // Classic Star Wars Crawl 3D Perspective
                rotationX = 56f
                cameraDistance = 8.5f * density
            }
            .testTag("intro_crawl_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, crawlOffset.value.toInt()) }
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Episode I",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFE600),
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "THE ETERNAL QUESTION",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                letterSpacing = 2.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Paragraph 1
            CrawlParagraph("It is a time of great uncertainty.")

            // Paragraph 2
            CrawlParagraph("The doors slide open, the scanners beep, and weary CUSTOMERS pour into the blue-and-yellow stronghold.")

            // Paragraph 3
            CrawlParagraph("Armed with vague intentions, broken electronics, and screenshots from the internet, they seek guidance... and validation.")

            // Paragraph 4
            CrawlParagraph("Standing between chaos and clarity is a lone BEST BUY HOST, tasked with asking the question that echoes through the aisles and beyond:")

            // Quote
            Text(
                text = "“What brings you in today?”",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Paragraph 5
            CrawlParagraph("Some will answer honestly.")

            // Paragraph 6
            CrawlParagraph("Some will lie.")

            // Paragraph 7
            CrawlParagraph("Some will say “Just looking” and mean everything and nothing.")

            // Paragraph 8
            CrawlParagraph("As the line grows longer and patience grows shorter, one truth remains...")

            // Paragraph 9 - Final Punchline
            Text(
                text = "The fate of the transaction rests on your greeting.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier.padding(top = 20.dp, bottom = 120.dp)
            )
        }

        // Top subtle horizon fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Black.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun CrawlParagraph(text: String) {
    Text(
        text = text,
        fontSize = 21.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFFE600),
        textAlign = TextAlign.Justify,
        lineHeight = 30.sp,
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .padding(vertical = 12.dp)
    )
}

@Composable
private fun DeathStarReveal() {
    val panY = remember { Animatable(320f) }

    LaunchedEffect(Unit) {
        panY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 5500, easing = LinearEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("intro_death_star_pan")
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset { IntOffset(90, panY.value.toInt()) }
        ) {
            val center = Offset(size.width * 0.7f, size.height * 0.7f)
            val radius = size.width * 0.5f

            // Superstore Death Star Base
            drawCircle(
                color = Color(0xFF1E293B),
                radius = radius,
                center = center
            )

            // Outer metallic rim
            drawCircle(
                color = Color(0xFF334155),
                radius = radius,
                center = center,
                style = Stroke(width = 3f)
            )

            // Superlaser / High-Gain Dish
            val dishCenter = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
            drawCircle(
                color = Color(0xFF0F172A),
                radius = radius * 0.28f,
                center = dishCenter
            )
            drawCircle(
                color = Color(0xFF64748B),
                radius = radius * 0.28f,
                center = dishCenter,
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = Color(0xFF475569),
                radius = radius * 0.08f,
                center = dishCenter
            )

            // Trench Line
            drawLine(
                color = Color(0xFF0F172A),
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 6f
            )

            // Panel lattice
            drawLine(
                color = Color(0xFF334155),
                start = Offset(center.x - radius * 0.8f, center.y + radius * 0.4f),
                end = Offset(center.x + radius * 0.8f, center.y + radius * 0.4f),
                strokeWidth = 2f
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Text(
                text = "PREPARE TO CLOCK IN...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StarfieldCanvas() {
    val stars = remember {
        List(150) {
            StarPoint(
                xNorm = Random.nextFloat(),
                yNorm = Random.nextFloat(),
                sizeDp = Random.nextFloat() * 2.2f + 1f,
                baseAlpha = Random.nextFloat() * 0.6f + 0.4f
            )
        }
    }

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        for (star in stars) {
            val x = star.xNorm * size.width
            val y = star.yNorm * size.height
            drawCircle(
                color = Color.White.copy(alpha = star.baseAlpha),
                radius = star.sizeDp,
                center = Offset(x, y)
            )
        }
    }
}

private data class StarPoint(
    val xNorm: Float,
    val yNorm: Float,
    val sizeDp: Float,
    val baseAlpha: Float
)
