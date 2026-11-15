package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SynthAudioEngine
import com.example.ui.theme.LocalMotionConfig
import com.example.ui.theme.LocalStudioPalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NeonCyberBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = LocalStudioPalette.current
    val motion = LocalMotionConfig.current

    val infinite = rememberInfiniteTransition(label = "cyber_bg")
    val beamPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beam_phase"
    )
    val particlePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_phase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(palette.bgTop, palette.bgMid, palette.bgBottom)
                )
            )
    ) {
        // Subtle 3D Cyber Room texture layered deep in background
        Image(
            painter = painterResource(id = R.drawable.img_studio_room),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (palette.suddenDeath) 0.10f else 0.16f)
        )

        // Volumetric Cyber Neon Lights, Floor Grid & Floating Particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val phase = if (motion.enableIdleLoops) beamPhase else 0.4f

            // Navy tint vignette
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        palette.bgTop.copy(alpha = 0.78f)
                    ),
                    center = Offset(w * 0.5f, h * 0.42f),
                    radius = w * 0.9f
                )
            )

            // Sweeping Cyan & Purple Volumetric Spotlights
            if (motion.enableAmbientGlow) {
                val leftBeamX = w * (0.22f + 0.08f * sin(phase))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            (if (palette.suddenDeath) palette.danger else palette.cyan).copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = Offset(leftBeamX, h * 0.18f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(leftBeamX, h * 0.18f)
                )

                val rightBeamX = w * (0.78f - 0.08f * cos(phase))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            (if (palette.suddenDeath) palette.primary else palette.tertiary).copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = Offset(rightBeamX, h * 0.26f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(rightBeamX, h * 0.26f)
                )
            }

            // Perspective Neon Floor Horizon Grid
            val horizonY = h * 0.72f
            val gridColor = (if (palette.suddenDeath) palette.danger else palette.cyan).copy(alpha = 0.18f)
            for (i in 0..5) {
                val t = i / 5f
                val y = horizonY + (h - horizonY) * (t * t)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.5f
                )
            }
            for (i in -4..4) {
                val topX = w * 0.5f + i * (w * 0.08f)
                val botX = w * 0.5f + i * (w * 0.25f)
                drawLine(
                    color = gridColor.copy(alpha = 0.13f),
                    start = Offset(topX, horizonY),
                    end = Offset(botX, h),
                    strokeWidth = 1.4f
                )
            }

            // Floating Ambient Particles (scaled by Graphics Quality & Reduce Motion)
            val count = motion.particleCount
            val pTime = if (motion.enableIdleLoops) particlePhase else 0.2f
            for (i in 0 until count) {
                val seedX = ((i * 73 + 19) % 100) / 100f
                val seedY = ((i * 41 + 7) % 100) / 100f
                val yNorm = (seedY - pTime + 1f) % 1f
                val x = w * seedX + sin(pTime * 2 * PI + i).toFloat() * 14f
                val y = h * yNorm
                val particleColor = when (i % 3) {
                    0 -> palette.cyan
                    1 -> palette.tertiary
                    else -> palette.primaryTop
                }
                drawCircle(
                    color = particleColor.copy(alpha = 0.38f),
                    radius = (3f + (i % 4) * 1.4f),
                    center = Offset(x, y)
                )
            }
        }

        content()
    }
}

@Composable
fun ImposterG3DLogo(
    tagline: String,
    offlineBadge: String? = null,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val palette = LocalStudioPalette.current
    val motion = LocalMotionConfig.current
    val infinite = rememberInfiniteTransition(label = "logo_float")
    val floatY by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_y"
    )
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_glow"
    )

    val yOffset = if (motion.enableIdleLoops) floatY else 0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.graphicsLayer { translationY = yOffset }
    ) {
        if (offlineBadge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(palette.surface.copy(alpha = 0.9f))
                    .border(1.5.dp, palette.cyan.copy(alpha = 0.7f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "● $offlineBadge",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = palette.cyan
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 3D Extruded "IMPOSTER"
            Box {
                Text(
                    text = "IMPOSTER ",
                    fontSize = if (compact) 28.sp else 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color(0xFF0A2A66),
                    modifier = Modifier.offset(x = 2.dp, y = 3.dp)
                )
                Text(
                    text = "IMPOSTER ",
                    fontSize = if (compact) 28.sp else 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
            // Glowing Red 3D "G"
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = if (motion.enableAmbientGlow) 14.dp else 4.dp,
                        shape = RoundedCornerShape(10.dp),
                        ambientColor = palette.danger.copy(alpha = glowAlpha),
                        spotColor = palette.danger.copy(alpha = glowAlpha)
                    )
            ) {
                Text(
                    text = "G",
                    fontSize = if (compact) 32.sp else 42.sp,
                    fontWeight = FontWeight.Black,
                    color = palette.dangerLip,
                    modifier = Modifier.offset(x = 2.dp, y = 3.dp)
                )
                Text(
                    text = "G",
                    fontSize = if (compact) 32.sp else 42.sp,
                    fontWeight = FontWeight.Black,
                    color = palette.danger
                )
            }
        }

        Text(
            text = tagline,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = palette.primaryTop
        )
    }
}

@Composable
fun Neon3DPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = LocalStudioPalette.current.cardBorder,
    fillColor: Color = LocalStudioPalette.current.surface,
    cornerRadius: Dp = 20.dp,
    lipHeight: Dp = 5.dp,
    pulseGlow: Boolean = false,
    animateGlow: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(14.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val palette = LocalStudioPalette.current
    val motion = LocalMotionConfig.current
    val effectiveGlow = if (animateGlow && pulseGlow && motion.enableAmbientGlow) {
        val infinite = rememberInfiniteTransition(label = "panel_glow")
        val glowPulse by infinite.animateFloat(
            initialValue = 0.55f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "panel_pulse"
        )
        glowPulse
    } else {
        0.72f
    }
    val shape = RoundedCornerShape(cornerRadius)
    val panelBase = if (animateGlow && motion.enableAmbientGlow) {
        modifier.shadow(
            elevation = 12.dp,
            shape = shape,
            ambientColor = borderColor.copy(alpha = 0.35f * effectiveGlow),
            spotColor = borderColor.copy(alpha = 0.45f * effectiveGlow)
        )
    } else {
        modifier
    }

    Box(
        modifier = panelBase
            .clip(shape)
            .background(palette.cardLip)
            .padding(bottom = lipHeight)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        fillColor.copy(alpha = 0.94f),
                        palette.bgMid.copy(alpha = 0.96f)
                    )
                )
            )
            .border(1.8.dp, borderColor.copy(alpha = effectiveGlow), shape)
            .drawWithContent {
                drawContent()
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.25f
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )
            }
            .padding(contentPadding),
        content = content
    )
}

enum class NeonButtonStyle {
    GOLD_PRIMARY,
    BLUE_GLOSSY,
    GREEN_CONFIRM,
    RED_DANGER,
    PURPLE_MYSTERY,
    SLATE_MUTED
}

@Composable
fun Neon3DButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NeonButtonStyle = NeonButtonStyle.BLUE_GLOSSY,
    enabled: Boolean = true,
    vibrationEnabled: Boolean = true,
    pulseGlow: Boolean = style == NeonButtonStyle.GOLD_PRIMARY,
    animateButton: Boolean = true,
    cornerRadius: Dp = 16.dp,
    height: Dp = 54.dp,
    testTag: String? = null,
    content: @Composable RowScope.() -> Unit
) {
    val palette = LocalStudioPalette.current
    val motion = LocalMotionConfig.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    val (topCol, mainCol, lipCol, borderCol) = when (style) {
        NeonButtonStyle.GOLD_PRIMARY -> listOf(palette.primaryTop, palette.primary, palette.primaryLip, Color(0xFFFFF1A8))
        NeonButtonStyle.BLUE_GLOSSY -> listOf(palette.secondaryTop, palette.secondary, palette.secondaryLip, palette.cyan)
        NeonButtonStyle.GREEN_CONFIRM -> listOf(Color(0xFF4ADE80), palette.success, palette.successLip, Color(0xFF86EFAC))
        NeonButtonStyle.RED_DANGER -> listOf(Color(0xFFFF7A7A), palette.danger, palette.dangerLip, Color(0xFFFCA5A5))
        NeonButtonStyle.PURPLE_MYSTERY -> listOf(Color(0xFFC084FC), palette.tertiary, palette.tertiaryLip, Color(0xFFE9D5FF))
        NeonButtonStyle.SLATE_MUTED -> listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF64748B))
    }

    val effectiveAlpha = if (enabled) 1f else 0.45f
    val shape = RoundedCornerShape(cornerRadius)
    val taggedModifier = if (testTag != null) modifier.testTag(testTag) else modifier

    val (buttonModifier, lipDp) = if (animateButton) {
        val isPressed by interactionSource.collectIsPressedAsState()
        val runMotion = !motion.reduceMotion
        val entranceScale = remember { Animatable(if (runMotion) 0.92f else 1f) }
        val entranceAlpha = remember { Animatable(if (runMotion) 0f else 1f) }
        LaunchedEffect(runMotion) {
            if (runMotion) {
                entranceAlpha.animateTo(1f, tween(260))
                entranceScale.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium))
            } else {
                entranceAlpha.snapTo(1f)
                entranceScale.snapTo(1f)
            }
        }
        val pressScale by animateFloatAsState(
            targetValue = if (isPressed && enabled) 0.94f else 1f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
            label = "btn_press_scale"
        )
        val animatedLip by animateDpAsState(
            targetValue = if (isPressed && enabled) 1.dp else 5.dp,
            animationSpec = tween(70),
            label = "btn_lip"
        )
        val shadowGlow = if (pulseGlow && enabled && motion.enableAmbientGlow) {
            val infinite = rememberInfiniteTransition(label = "btn_glow")
            val anim by infinite.animateFloat(
                initialValue = 0.55f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "btn_glow_alpha"
            )
            anim
        } else {
            0.4f
        }
        val mod = taggedModifier
            .height(height)
            .graphicsLayer {
                scaleX = entranceScale.value * pressScale
                scaleY = entranceScale.value * pressScale
                alpha = entranceAlpha.value * effectiveAlpha
            }
            .shadow(
                elevation = if (motion.enableAmbientGlow && enabled) (10.dp * shadowGlow) else 2.dp,
                shape = shape,
                ambientColor = topCol.copy(alpha = 0.6f),
                spotColor = borderCol.copy(alpha = 0.7f)
            )
        mod to animatedLip
    } else {
        val mod = taggedModifier
            .height(height)
            .alpha(effectiveAlpha)
        mod to 5.dp
    }

    Box(
        modifier = buttonModifier
            .clip(shape)
            .background(lipCol)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button
            ) {
                SynthAudioEngine.playClick()
                if (vibrationEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onClick()
            }
            .padding(bottom = lipDp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(Brush.verticalGradient(colors = listOf(topCol, mainCol)))
                .border(1.5.dp, borderCol.copy(alpha = 0.85f), shape)
                .drawWithContent {
                    drawContent()
                    // Glossy 3D top specular reflection
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.36f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.46f
                        ),
                        topLeft = Offset(3f, 2f),
                        size = Size(size.width - 6f, size.height * 0.45f),
                        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 14.dp),
                content = content
            )
        }
    }
}

@Composable
fun Neon3DIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NeonButtonStyle = NeonButtonStyle.BLUE_GLOSSY,
    vibrationEnabled: Boolean = true,
    enabled: Boolean = true,
    animateButton: Boolean = true,
    sizeDp: Dp = 48.dp,
    testTag: String? = null
) {
    Neon3DButton(
        onClick = onClick,
        modifier = modifier.size(sizeDp),
        style = style,
        enabled = enabled,
        vibrationEnabled = vibrationEnabled,
        pulseGlow = false,
        animateButton = animateButton,
        cornerRadius = 14.dp,
        height = sizeDp,
        testTag = testTag
    ) {
        val tint = if (style == NeonButtonStyle.GOLD_PRIMARY) Color(0xFF1E1300) else Color.White
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun NeonTopHeaderBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onHome: (() -> Unit)? = null,
    vibrationEnabled: Boolean = true,
    animateButtons: Boolean = true,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    backTestTag: String = "nav_back_button",
    homeTestTag: String = "nav_home_button"
) {
    val palette = LocalStudioPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Neon3DIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
                vibrationEnabled = vibrationEnabled,
                animateButton = animateButtons,
                testTag = backTestTag
            )
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = palette.ink,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        if (trailingContent != null) {
            Row(verticalAlignment = Alignment.CenterVertically, content = trailingContent)
        } else if (onHome != null) {
            Neon3DIconButton(
                icon = Icons.Rounded.Home,
                contentDescription = "Home",
                onClick = onHome,
                vibrationEnabled = vibrationEnabled,
                animateButton = animateButtons,
                testTag = homeTestTag
            )
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
fun AnimatedXpProgressBar(
    xpIntoLevel: Int,
    xpPerLevel: Int,
    modifier: Modifier = Modifier
) {
    val palette = LocalStudioPalette.current
    val targetFraction = (xpIntoLevel.toFloat() / xpPerLevel.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(750, easing = FastOutSlowInEasing),
        label = "xp_bar_fill"
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF060E24))
                .border(1.2.dp, palette.cyan.copy(alpha = 0.6f), RoundedCornerShape(50))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction.coerceAtLeast(0.04f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(palette.cyan, palette.primaryTop, palette.primary)
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$xpIntoLevel / $xpPerLevel XP",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = palette.primaryTop
        )
    }
}

@Composable
fun AnimatedVoteBar3D(
    voteCount: Int,
    maxVotes: Int,
    accentColor: Color,
    isHighest: Boolean,
    modifier: Modifier = Modifier
) {
    val fractionTarget = if (maxVotes <= 0) 0.08f else (voteCount.toFloat() / maxVotes).coerceIn(0.08f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fractionTarget,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "vote_bar_height"
    )

    Box(
        modifier = modifier
            .width(38.dp)
            .height(120.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp * animatedFraction)
                .shadow(
                    elevation = if (isHighest) 12.dp else 4.dp,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 6.dp, bottomEnd = 6.dp),
                    ambientColor = accentColor,
                    spotColor = accentColor
                )
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 6.dp, bottomEnd = 6.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(accentColor, accentColor.copy(alpha = 0.45f))
                    )
                )
                .border(
                    width = if (isHighest) 2.dp else 1.dp,
                    color = Color.White.copy(alpha = if (isHighest) 0.9f else 0.4f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 6.dp, bottomEnd = 6.dp)
                )
        )
    }
}

@Composable
fun ConfettiBurstOverlay(
    modifier: Modifier = Modifier
) {
    val motion = LocalMotionConfig.current
    if (motion.reduceMotion) return

    val infinite = rememberInfiniteTransition(label = "confetti")
    val fallProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_fall"
    )

    val colors = listOf(
        Color(0xFFFFD84D),
        Color(0xFF00E5FF),
        Color(0xFFA855F7),
        Color(0xFF4ADE80),
        Color(0xFFFF6B6B)
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val count = if (motion.quality.name == "LOW") 14 else 32
        for (i in 0 until count) {
            val startX = ((i * 37 + 11) % 100) / 100f * size.width
            val yOffset = ((i * 53 % 100) / 100f + fallProgress) % 1f * size.height
            val x = startX + sin(fallProgress * 2 * PI + i).toFloat() * 18f
            val angle = (fallProgress * 360f + i * 29f) % 360f
            rotate(angle, pivot = Offset(x, yOffset)) {
                drawRoundRect(
                    color = colors[i % colors.size].copy(alpha = 0.85f),
                    topLeft = Offset(x - 6f, yOffset - 4f),
                    size = Size(12f, 7f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
        }
    }
}

@Composable
fun BilingualFlagGraphic(
    isIndia: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(width = 48.dp, height = 34.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
    ) {
        val w = size.width
        val h = size.height
        if (isIndia) {
            drawRect(Color(0xFFFF9933), topLeft = Offset(0f, 0f), size = Size(w, h / 3f))
            drawRect(Color.White, topLeft = Offset(0f, h / 3f), size = Size(w, h / 3f))
            drawRect(Color(0xFF138808), topLeft = Offset(0f, 2f * h / 3f), size = Size(w, h / 3f))
            val center = Offset(w / 2f, h / 2f)
            val r = h * 0.13f
            drawCircle(Color(0xFF000080), radius = r, center = center, style = Stroke(width = 2f))
            for (s in 0 until 12) {
                val a = s * (PI / 6.0)
                drawLine(
                    color = Color(0xFF000080),
                    start = center,
                    end = Offset(center.x + (r * cos(a)).toFloat(), center.y + (r * sin(a)).toFloat()),
                    strokeWidth = 1f
                )
            }
        } else {
            drawRect(Color(0xFF012169), topLeft = Offset.Zero, size = size)
            // White diagonals
            drawLine(Color.White, Offset(0f, 0f), Offset(w, h), strokeWidth = 6f)
            drawLine(Color.White, Offset(w, 0f), Offset(0f, h), strokeWidth = 6f)
            // Red diagonals
            drawLine(Color(0xFFC8102E), Offset(0f, 0f), Offset(w, h), strokeWidth = 2.6f)
            drawLine(Color(0xFFC8102E), Offset(w, 0f), Offset(0f, h), strokeWidth = 2.6f)
            // White cross
            drawRect(Color.White, topLeft = Offset(w * 0.40f, 0f), size = Size(w * 0.20f, h))
            drawRect(Color.White, topLeft = Offset(0f, h * 0.36f), size = Size(w, h * 0.28f))
            // Red cross
            drawRect(Color(0xFFC8102E), topLeft = Offset(w * 0.44f, 0f), size = Size(w * 0.12f, h))
            drawRect(Color(0xFFC8102E), topLeft = Offset(0f, h * 0.42f), size = Size(w, h * 0.16f))
        }
    }
}

@Composable
fun NeonSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    val palette = LocalStudioPalette.current
    val thumbBias by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "switch_thumb"
    )
    val taggedMod = if (testTag != null) modifier.testTag(testTag) else modifier

    Row(
        modifier = taggedMod
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                SynthAudioEngine.playClick()
                onCheckedChange(!checked)
            }
            .padding(vertical = 8.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = palette.ink,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(width = 58.dp, height = 32.dp)
                .clip(RoundedCornerShape(50))
                .background(if (checked) palette.cyan.copy(alpha = 0.9f) else Color(0xFF1E293B))
                .border(
                    1.5.dp,
                    if (checked) Color.White.copy(alpha = 0.8f) else palette.cardBorder.copy(alpha = 0.4f),
                    RoundedCornerShape(50)
                )
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = 26.dp * thumbBias)
                    .size(26.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
