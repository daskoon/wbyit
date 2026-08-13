package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEngine
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class IntroPhase {
    PROLOGUE,    // "A long time ago, in a retail galaxy not so far away..."
    TITLE_ZOOM,  // "BEST BUY" logo receding into starfield with fanfare
    TEXT_CRAWL,  // 3D perspective yellow crawl
    DEATH_STAR,  // Pan down to Death Star in space
    FINISHED
}

@Composable
fun IntroScreen(
    soundEngine: SoundEngine,
    onFinishIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableStateOf(IntroPhase.PROLOGUE) }
    var soundEnabled by remember { mutableStateOf(soundEngine.isEnabled) }
    var crawlSpeedMultiplier by remember { mutableFloatStateOf(1.0f) }

    // Start fanfare and progress phases
    LaunchedEffect(Unit) {
        // Phase 1: Prologue text (0s to 5.5s)
        delay(5500L)
        
        // Phase 2: Title Zoom & Star Wars Fanfare
        phase = IntroPhase.TITLE_ZOOM
        soundEngine.playStarWarsFanfare()
        delay(7000L)

        // Phase 3: Text Crawl
        phase = IntroPhase.TEXT_CRAWL
        delay(42000L)

        // Phase 4: Death Star Reveal Pan
        phase = IntroPhase.DEATH_STAR
        delay(7000L)

        // Phase 5: Transition to Main Game
        phase = IntroPhase.FINISHED
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
                // Tap to accelerate crawl or advance
                if (phase == IntroPhase.TEXT_CRAWL) {
                    crawlSpeedMultiplier = if (crawlSpeedMultiplier == 1.0f) 2.5f else 1.0f
                }
            }
            .testTag("intro_screen_container")
    ) {
        // Starfield Canvas Background (visible during Title, Crawl, Death Star)
        if (phase != IntroPhase.PROLOGUE) {
            StarfieldCanvas()
        }

        // 1. PROLOGUE PHASE
        AnimatedVisibility(
            visible = phase == IntroPhase.PROLOGUE,
            enter = fadeIn(tween(1200)),
            exit = fadeOut(tween(1200)),
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

        // 2. LOGO ZOOM PHASE ("BEST BUY")
        if (phase == IntroPhase.TITLE_ZOOM) {
            TitleLogoZoomSection()
        }

        // 3. 3D TEXT CRAWL PHASE
        if (phase == IntroPhase.TEXT_CRAWL) {
            Crawl3DSection(speedMultiplier = crawlSpeedMultiplier)
        }

        // 4. DEATH STAR / SPACE PAN PHASE
        if (phase == IntroPhase.DEATH_STAR) {
            DeathStarPanSection()
        }

        // TOP CONTROLS (Skip Intro & Sound Toggle & Speed Indicator)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed indicator if fast forwarding
            if (crawlSpeedMultiplier > 1f && phase == IntroPhase.TEXT_CRAWL) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.8f))
                        .border(1.dp, Color(0xFFFFE600), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = null,
                            tint = Color(0xFFFFE600),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2.5x SPEED (Tap to normal)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFE600)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Sound Toggle
                IconButton(
                    onClick = {
                        soundEnabled = !soundEnabled
                        soundEngine.isEnabled = soundEnabled
                        if (!soundEnabled) {
                            soundEngine.stopIntroMusic()
                        } else if (phase == IntroPhase.TITLE_ZOOM || phase == IntroPhase.TEXT_CRAWL) {
                            soundEngine.playStarWarsFanfare()
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                        .testTag("intro_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeMute,
                        contentDescription = "Toggle Sound",
                        tint = if (soundEnabled) Color(0xFFFFE600) else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // SKIP INTRO BUTTON
                Button(
                    onClick = {
                        soundEngine.stopIntroMusic()
                        onFinishIntro()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE600)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("skip_intro_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SKIP INTRO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleLogoZoomSection() {
    val scaleAnim = remember { Animatable(1.4f) }
    val alphaAnim = remember { Animatable(1.0f) }

    LaunchedEffect(Unit) {
        // Zoom from 1.4 down to 0.05 while receding into deep space
        scaleAnim.animateTo(
            targetValue = 0.05f,
            animationSpec = tween(durationMillis = 6500, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        delay(4500L)
        alphaAnim.animateTo(
            targetValue = 0.0f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
                .testTag("intro_best_buy_logo")
        ) {
            // BEST BUY Star Wars styled yellow outline title
            Text(
                text = "BEST",
                fontSize = 82.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                lineHeight = 78.sp
            )
            Text(
                text = "BUY",
                fontSize = 82.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                lineHeight = 78.sp
            )
        }
    }
}

@Composable
private fun Crawl3DSection(speedMultiplier: Float) {
    val crawlOffset = remember { Animatable(900f) }

    LaunchedEffect(speedMultiplier) {
        val remainingDistance = crawlOffset.value - (-1400f)
        if (remainingDistance > 0) {
            val duration = ((remainingDistance / 50f) * 1000 / speedMultiplier).toInt()
            crawlOffset.animateTo(
                targetValue = -1400f,
                animationSpec = tween(durationMillis = duration.coerceAtLeast(100), easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                // 3D Perspective Rotation for Star Wars crawling text
                rotationX = 58f
                cameraDistance = 9.0f * density
            }
            .testTag("intro_crawl_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, crawlOffset.value.toInt()) }
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Episode I Header
            Text(
                text = "Episode I",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFE600),
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "THE ETERNAL QUESTION",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                letterSpacing = 3.sp,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Crawl Paragraphs
            CrawlParagraph("It is a time of great uncertainty.")

            CrawlParagraph("The doors slide open, the scanners beep, and weary CUSTOMERS pour into the blue-and-yellow stronghold.")

            CrawlParagraph("Armed with vague intentions, broken electronics, and screenshots from the internet, they seek guidance... and validation.")

            CrawlParagraph("Standing between chaos and clarity is a lone BEST BUY HOST, tasked with asking the question that echoes through the aisles and beyond:")

            Text(
                text = "“What brings you in today?”",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            CrawlParagraph("Some will answer honestly.")

            CrawlParagraph("Some will lie.")

            CrawlParagraph("Some will say “Just looking” and mean everything and nothing.")

            CrawlParagraph("As the line grows longer and patience grows shorter, one truth remains...")

            Text(
                text = "The fate of the transaction rests on your greeting.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 40.dp)
            )
        }

        // Top Gradient Vignette so text fades into deep space at the horizon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Black.copy(alpha = 0.85f),
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
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFFE600),
        textAlign = TextAlign.Justify,
        lineHeight = 28.sp,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .padding(vertical = 10.dp)
    )
}

@Composable
private fun DeathStarPanSection() {
    val panAnim = remember { Animatable(300f) }

    LaunchedEffect(Unit) {
        panAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 6000, easing = LinearEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("intro_death_star_pan")
    ) {
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomEnd)
                .offset { IntOffset(80, (panAnim.value).toInt()) }
        ) {
            val center = Offset(size.width * 0.7f, size.height * 0.7f)
            val radius = size.width * 0.5f

            // Shadowed Space Sphere (Death Star / Superstore Station)
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

            // Superlaser / Radar Dish Concave Lens
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

            // Equatorial Trench Line
            drawLine(
                color = Color(0xFF0F172A),
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 5f
            )

            // Surface panel lines
            drawLine(
                color = Color(0xFF334155),
                start = Offset(center.x - radius * 0.8f, center.y + radius * 0.4f),
                end = Offset(center.x + radius * 0.8f, center.y + radius * 0.4f),
                strokeWidth = 2f
            )
        }

        // Subtitle prompt to enter store
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Text(
                text = "PREPARE TO CLOCK IN...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFFE600),
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StarfieldCanvas() {
    val stars = remember {
        List(140) {
            StarPoint(
                xNorm = Random.nextFloat(),
                yNorm = Random.nextFloat(),
                sizeDp = Random.nextFloat() * 2.5f + 1f,
                baseAlpha = Random.nextFloat() * 0.6f + 0.4f,
                twinkleSpeed = Random.nextInt(800, 2400)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars_twinkle")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        for (star in stars) {
            val x = star.xNorm * size.width
            val y = star.yNorm * size.height
            val alpha = (star.baseAlpha * pulse).coerceIn(0.2f, 1.0f)
            drawCircle(
                color = Color.White.copy(alpha = alpha),
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
    val baseAlpha: Float,
    val twinkleSpeed: Int
)
