package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SynthAudioEngine
import com.example.data.words.WordBank
import com.example.domain.AppLanguage
import com.example.domain.ChallengeModifier
import com.example.domain.SecretMission
import com.example.localization.AppStrings
import com.example.ui.theme.LocalMotionConfig
import com.example.ui.theme.LocalStudioPalette
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ChallengeWheel3D(
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onModifierLocked: (ChallengeModifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalStudioPalette.current
    val scope = rememberCoroutineScope()
    val modifiers = WordBank.challengeModifiers
    val rotationAnim = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var selectedIdx by remember { mutableIntStateOf(-1) }

    val wheelColors = listOf(
        Color(0xFF1565F5),
        Color(0xFF9333EA),
        Color(0xFF06B6D4),
        Color(0xFFEF4444),
        Color(0xFFF59E0B),
        Color(0xFF16A34A),
        Color(0xFFEC4899),
        Color(0xFF3B82F6)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(230.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension * 0.44f
                val center = Offset(size.width / 2f, size.height / 2f)
                val sweep = 360f / modifiers.size

                // Outer neon ring
                drawCircle(
                    color = palette.cyan.copy(alpha = 0.5f),
                    radius = r + 14f,
                    center = center,
                    style = Stroke(width = 8f)
                )

                for (i in modifiers.indices) {
                    val startAngle = rotationAnim.value + i * sweep - 90f
                    drawArc(
                        color = wheelColors[i % wheelColors.size],
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(center.x - r, center.y - r),
                        size = Size(r * 2f, r * 2f)
                    )
                    drawArc(
                        color = Color.White.copy(alpha = 0.5f),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(center.x - r, center.y - r),
                        size = Size(r * 2f, r * 2f),
                        style = Stroke(width = 2.5f)
                    )
                }

                // Blinking rim bulbs
                for (b in 0 until 16) {
                    val a = b * (2 * PI / 16.0)
                    val bx = center.x + ((r + 7f) * cos(a)).toFloat()
                    val by = center.y + ((r + 7f) * sin(a)).toFloat()
                    drawCircle(
                        color = if (b % 2 == 0) palette.primaryTop else palette.cyan,
                        radius = 4.5f,
                        center = Offset(bx, by)
                    )
                }

                // Center hub
                drawCircle(Color(0xFF0A1432), radius = r * 0.24f, center = center)
                drawCircle(palette.primaryTop, radius = r * 0.24f, center = center, style = Stroke(width = 4f))

                // Top pointer triangle
                val pointer = Path().apply {
                    moveTo(center.x, center.y - r + 12f)
                    lineTo(center.x - 16f, center.y - r - 18f)
                    lineTo(center.x + 16f, center.y - r - 18f)
                    close()
                }
                drawPath(pointer, palette.primaryTop)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedIdx >= 0) {
            val chosen = modifiers[selectedIdx]
            Neon3DPanel(
                borderColor = palette.primaryTop,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == AppLanguage.HINDI) chosen.titleHi else chosen.titleEn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.primaryTop,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.HINDI) chosen.descHi else chosen.descEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.ink,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Neon3DButton(
                onClick = { onModifierLocked(chosen) },
                style = NeonButtonStyle.GOLD_PRIMARY,
                vibrationEnabled = vibrationEnabled,
                modifier = Modifier.fillMaxWidth(),
                testTag = "wheel_continue_btn"
            ) {
                Text(strings.continueBtn, fontWeight = FontWeight.Black, color = Color(0xFF1E1300))
            }
        } else {
            Neon3DButton(
                onClick = {
                    if (!isSpinning) {
                        isSpinning = true
                        scope.launch {
                            val targetIdx = Random.nextInt(modifiers.size)
                            val sweep = 360f / modifiers.size
                            val targetAngle = rotationAnim.value + 1080f + (360f - (targetIdx * sweep + sweep / 2f))
                            rotationAnim.animateTo(
                                targetValue = targetAngle,
                                animationSpec = tween(2200, easing = FastOutSlowInEasing)
                            )
                            SynthAudioEngine.playAchievement()
                            selectedIdx = targetIdx
                            isSpinning = false
                        }
                    }
                },
                enabled = !isSpinning,
                style = NeonButtonStyle.PURPLE_MYSTERY,
                vibrationEnabled = vibrationEnabled,
                modifier = Modifier.fillMaxWidth(),
                testTag = "spin_wheel_action_btn"
            ) {
                Text(
                    text = if (isSpinning) strings.spinningLabel else strings.spinWheelBtn,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SecretQuestionCard3D(
    modifier: Modifier = Modifier
) {
    val palette = LocalStudioPalette.current
    val motion = LocalMotionConfig.current
    val infinite = rememberInfiniteTransition(label = "secret_q_card")
    val floatY by infinite.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "q_float"
    )
    val qPulse by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "q_pulse"
    )

    val yShift = if (motion.enableIdleLoops) floatY else 0f
    val scale = if (motion.enableIdleLoops) qPulse else 1f
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .size(width = 166.dp, height = 236.dp)
            .graphicsLayer { translationY = yShift }
            .shadow(
                elevation = if (motion.enableAmbientGlow) 22.dp else 6.dp,
                shape = shape,
                ambientColor = palette.tertiary,
                spotColor = palette.cyan
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B), Color(0xFF0E7490))
                )
            )
            .border(3.dp, Brush.linearGradient(listOf(palette.cyan, palette.tertiary, Color(0xFFEC4899))), shape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_secret_card_back),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.32f)
        )
        Box(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize()
                .border(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                fontSize = 86.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
            )
        }
    }
}

@Composable
fun SecretWordFlipCard3D(
    isImposter: Boolean,
    wordText: String,
    categoryText: String,
    hintText: String?,
    mission: SecretMission?,
    language: AppLanguage,
    strings: AppStrings,
    isFlipped: Boolean,
    modifier: Modifier = Modifier
) {
    val palette = LocalStudioPalette.current
    val density = LocalDensity.current.density
    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "card_3d_flip"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 14f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotationY <= 90f) {
            SecretQuestionCard3D()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { this.rotationY = 180f }
            ) {
                val borderCol = if (isImposter) palette.danger else palette.primaryTop
                Neon3DPanel(
                    borderColor = borderCol,
                    pulseGlow = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isImposter) strings.youAreImposterTitle else strings.yourSecretWordLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isImposter) palette.danger else palette.primaryTop,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (isImposter) {
                            Text(
                                text = strings.youAreImposterSub,
                                style = MaterialTheme.typography.bodyLarge,
                                color = palette.ink,
                                textAlign = TextAlign.Center
                            )
                            if (hintText != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(palette.danger.copy(alpha = 0.2f))
                                        .border(1.dp, palette.danger.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${strings.hintPrefix}: $hintText",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = palette.primaryTop,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            // Radiant golden secret word pill (matching Screen 11 in reference)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(14.dp, RoundedCornerShape(18.dp), ambientColor = palette.primary)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(palette.primaryTop, palette.primary)
                                        )
                                    )
                                    .border(2.dp, Color(0xFFFFF7CC), RoundedCornerShape(18.dp))
                                    .padding(vertical = 18.dp, horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = wordText,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1A1000),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${strings.categoryPrefix}: $categoryText",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.cyan,
                            fontWeight = FontWeight.Bold
                        )

                        if (mission != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.tertiary.copy(alpha = 0.22f))
                                    .border(1.dp, palette.tertiary.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                val mText = if (language == AppLanguage.HINDI) mission.titleHi else mission.titleEn
                                Text(
                                    text = "${strings.secretMissionPrefix}: $mText (+${mission.bonusXp} XP)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.ink,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
