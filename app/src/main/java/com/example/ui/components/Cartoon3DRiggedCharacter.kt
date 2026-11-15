package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.AccessoryStyle
import com.example.domain.AvatarConfig
import com.example.domain.CharacterPose
import com.example.domain.GlassesStyle
import com.example.domain.HairStyle
import com.example.domain.HatStyle
import com.example.domain.OutfitStyle
import com.example.ui.theme.CharacterColorSwatches
import com.example.ui.theme.LocalMotionConfig
import com.example.ui.theme.PedestalColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Realistic Cartoon 3D Rigged Character Engine.
 * Supports 20 base presets, hair/glasses/hats/outfits/accessories, glowing neon pedestals,
 * and 11 live animated poses (breathing, eye blinking, head motion, gestures, walking/running, sitting, victory/defeat).
 */
@Composable
fun Cartoon3DRiggedCharacter(
    avatar: AvatarConfig,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    pose: CharacterPose = CharacterPose.IDLE,
    isPortraitOnly: Boolean = false,
    showPedestal: Boolean = true,
    isImposterAura: Boolean = false,
    seedOffset: Int = 0
) {
    val motion = LocalMotionConfig.current
    val infinite = rememberInfiniteTransition(label = "char_anim_$seedOffset")

    val breathPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2100 + (seedOffset % 3) * 250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "breath"
    )
    val blinkCycle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400 + (seedOffset % 4) * 300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )
    val actionPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (pose) {
                    CharacterPose.RUNNING -> 520
                    CharacterPose.WALKING -> 820
                    CharacterPose.TALKING -> 620
                    CharacterPose.VICTORY -> 740
                    else -> 1600
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "action"
    )

    val effBreath = if (motion.enableIdleLoops) breathPhase + seedOffset * 0.7f else 0f
    val effBlink = if (motion.enableIdleLoops) blinkCycle else 0f
    val effAction = if (motion.enableIdleLoops) actionPhase + seedOffset * 0.5f else 0.4f

    val isBlinking = effBlink > 0.92f && effBlink < 0.97f

    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            val breathY = sin(effBreath) * h * 0.012f
            val headBobY = when (pose) {
                CharacterPose.VICTORY -> -kotlin.math.abs(sin(effAction)) * h * 0.04f
                CharacterPose.DEFEAT -> h * 0.03f
                CharacterPose.RUNNING -> -kotlin.math.abs(sin(effAction)) * h * 0.03f
                CharacterPose.WALKING -> -kotlin.math.abs(sin(effAction)) * h * 0.015f
                else -> breathY * 0.8f
            }
            val headTilt = when (pose) {
                CharacterPose.THINKING -> 8f + sin(effAction) * 3f
                CharacterPose.SURPRISE -> -6f
                CharacterPose.DEFEAT -> 10f
                CharacterPose.TALKING -> sin(effAction) * 4f
                else -> sin(effBreath * 0.5f) * 3.2f
            }

            val pedestalColor = if (isImposterAura) {
                Color(0xFFEF4444)
            } else {
                PedestalColors[avatar.pedestalColorIndex.coerceIn(0, PedestalColors.lastIndex)]
            }
            val primarySwatch = CharacterColorSwatches[avatar.colorIndex.coerceIn(0, CharacterColorSwatches.lastIndex)]

            val skinColor = Color(avatar.preset.skinHex)
            val skinShadow = Color(
                red = (skinColor.red * 0.76f).coerceIn(0f, 1f),
                green = (skinColor.green * 0.72f).coerceIn(0f, 1f),
                blue = (skinColor.blue * 0.72f).coerceIn(0f, 1f),
                alpha = 1f
            )
            val hairColor = Color(avatar.preset.hairHex)

            if (isPortraitOnly) {
                drawPortraitAvatar(
                    w = w,
                    h = h,
                    avatar = avatar,
                    pose = pose,
                    headBobY = headBobY,
                    headTilt = headTilt,
                    isBlinking = isBlinking,
                    actionPhase = effAction,
                    skinColor = skinColor,
                    skinShadow = skinShadow,
                    hairColor = hairColor,
                    primarySwatch = primarySwatch,
                    pedestalColor = pedestalColor
                )
            } else {
                drawFullBodyRiggedCharacter(
                    w = w,
                    h = h,
                    avatar = avatar,
                    pose = pose,
                    breathY = breathY,
                    headBobY = headBobY,
                    headTilt = headTilt,
                    isBlinking = isBlinking,
                    actionPhase = effAction,
                    skinColor = skinColor,
                    skinShadow = skinShadow,
                    hairColor = hairColor,
                    primarySwatch = primarySwatch,
                    pedestalColor = pedestalColor,
                    showPedestal = showPedestal
                )
            }
        }
    }
}

private fun DrawScope.drawFullBodyRiggedCharacter(
    w: Float,
    h: Float,
    avatar: AvatarConfig,
    pose: CharacterPose,
    breathY: Float,
    headBobY: Float,
    headTilt: Float,
    isBlinking: Boolean,
    actionPhase: Float,
    skinColor: Color,
    skinShadow: Color,
    hairColor: Color,
    primarySwatch: Color,
    pedestalColor: Color,
    showPedestal: Boolean
) {
    val cx = w * 0.5f

    // 1. Glowing 3D Studio Pedestal
    if (showPedestal) {
        val pedTop = h * 0.855f
        val pedW = w * 0.64f
        val pedH = h * 0.11f
        // Floor aura reflection
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(pedestalColor.copy(alpha = 0.45f), Color.Transparent),
                center = Offset(cx, pedTop + pedH * 0.55f),
                radius = pedW * 0.72f
            ),
            topLeft = Offset(cx - pedW * 0.7f, pedTop - pedH * 0.1f),
            size = Size(pedW * 1.4f, pedH * 1.3f)
        )
        // Pedestal cylinder base
        drawOval(
            brush = Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF090F24))),
            topLeft = Offset(cx - pedW * 0.5f, pedTop + pedH * 0.25f),
            size = Size(pedW, pedH * 0.75f)
        )
        // Neon top disc
        drawOval(
            brush = Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0B1433))),
            topLeft = Offset(cx - pedW * 0.5f, pedTop),
            size = Size(pedW, pedH * 0.65f)
        )
        drawOval(
            color = pedestalColor,
            topLeft = Offset(cx - pedW * 0.5f, pedTop),
            size = Size(pedW, pedH * 0.65f),
            style = Stroke(width = w * 0.025f)
        )
    }

    val outfitColors = resolveOutfitPalette(avatar.outfit, primarySwatch)
    val jumpOffset = if (pose == CharacterPose.VICTORY) -kotlin.math.abs(sin(actionPhase)) * h * 0.045f else 0f

    // 2. Hero Cape behind torso if equipped
    if (avatar.accessory == AccessoryStyle.CAPE) {
        val capeSway = sin(actionPhase) * w * 0.04f
        val capePath = Path().apply {
            moveTo(cx - w * 0.14f, h * 0.44f + breathY + jumpOffset)
            lineTo(cx + w * 0.14f, h * 0.44f + breathY + jumpOffset)
            lineTo(cx + w * 0.24f + capeSway, h * 0.78f + jumpOffset)
            lineTo(cx - w * 0.24f + capeSway, h * 0.78f + jumpOffset)
            close()
        }
        drawPath(
            path = capePath,
            brush = Brush.verticalGradient(listOf(Color(0xFFDC2626), Color(0xFF7F1D1D)))
        )
    }

    // 3. Legs & Shoes (supports Walking/Running swing & Sitting pose)
    val legTopY = h * 0.66f + breathY * 0.4f + jumpOffset
    val leftLegSwing = when (pose) {
        CharacterPose.WALKING -> sin(actionPhase) * h * 0.045f
        CharacterPose.RUNNING -> sin(actionPhase) * h * 0.07f
        else -> 0f
    }
    val rightLegSwing = -leftLegSwing
    val isSitting = pose == CharacterPose.SITTING
    val legLen = if (isSitting) h * 0.11f else h * 0.17f

    val pantsBrush = Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0F172A)))
    // Left leg
    drawRoundRect(
        brush = pantsBrush,
        topLeft = Offset(cx - w * 0.125f, legTopY + leftLegSwing * 0.3f),
        size = Size(w * 0.105f, legLen),
        cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
    )
    // Right leg
    drawRoundRect(
        brush = pantsBrush,
        topLeft = Offset(cx + w * 0.02f, legTopY + rightLegSwing * 0.3f),
        size = Size(w * 0.105f, legLen),
        cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
    )

    if (!isSitting) {
        // 3D Sneakers
        val shoeYLeft = legTopY + legLen - h * 0.02f + leftLegSwing * 0.35f
        val shoeYRight = legTopY + legLen - h * 0.02f + rightLegSwing * 0.35f
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFCBD5E1))),
            topLeft = Offset(cx - w * 0.145f, shoeYLeft),
            size = Size(w * 0.125f, h * 0.055f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
        )
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFCBD5E1))),
            topLeft = Offset(cx + w * 0.02f, shoeYRight),
            size = Size(w * 0.125f, h * 0.055f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
        )
    }

    // 4. Articulated Left & Right Arms (Pose-driven!)
    val shoulderY = h * 0.46f + breathY + jumpOffset
    drawArticulatedArms(
        cx = cx,
        w = w,
        h = h,
        shoulderY = shoulderY,
        pose = pose,
        actionPhase = actionPhase,
        sleeveColor = outfitColors.first,
        skinColor = skinColor,
        accessory = avatar.accessory
    )

    // 5. 3D Shaded Torso & Outfit
    val torsoW = w * 0.28f
    val torsoH = h * 0.24f
    val torsoTop = h * 0.43f + breathY + jumpOffset
    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(outfitColors.second, outfitColors.first, outfitColors.second),
            startX = cx - torsoW * 0.5f,
            endX = cx + torsoW * 0.5f
        ),
        topLeft = Offset(cx - torsoW * 0.5f, torsoTop),
        size = Size(torsoW, torsoH),
        cornerRadius = CornerRadius(w * 0.07f, w * 0.07f)
    )
    // Outfit detailing (Hoodie strings / Zipper / Tie / Neon Stripe)
    drawOutfitDetails(
        cx = cx,
        torsoTop = torsoTop,
        torsoW = torsoW,
        torsoH = torsoH,
        outfit = avatar.outfit,
        accent = outfitColors.third
    )

    // Star Badge on chest
    if (avatar.accessory == AccessoryStyle.STAR_BADGE) {
        drawCircle(
            color = Color(0xFFFFD84D),
            radius = w * 0.035f,
            center = Offset(cx + torsoW * 0.24f, torsoTop + torsoH * 0.34f)
        )
    }

    // 6. Neck + 3D Shaded Head + Hair + Face + Glasses + Hat
    val headCenterY = h * 0.28f + headBobY + jumpOffset
    val headRx = w * 0.165f
    val headRy = h * 0.15f

    // Neck
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(skinShadow, skinColor)),
        topLeft = Offset(cx - w * 0.045f, torsoTop - h * 0.035f),
        size = Size(w * 0.09f, h * 0.06f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    rotate(degrees = headTilt, pivot = Offset(cx, headCenterY)) {
        drawCharacterHeadAndAccessories(
            cx = cx,
            cy = headCenterY,
            rx = headRx,
            ry = headRy,
            avatar = avatar,
            pose = pose,
            isBlinking = isBlinking,
            actionPhase = actionPhase,
            skinColor = skinColor,
            skinShadow = skinShadow,
            hairColor = hairColor,
            accentColor = primarySwatch
        )
    }
}

private fun DrawScope.drawPortraitAvatar(
    w: Float,
    h: Float,
    avatar: AvatarConfig,
    pose: CharacterPose,
    headBobY: Float,
    headTilt: Float,
    isBlinking: Boolean,
    actionPhase: Float,
    skinColor: Color,
    skinShadow: Color,
    hairColor: Color,
    primarySwatch: Color,
    pedestalColor: Color
) {
    val cx = w * 0.5f
    val outfitColors = resolveOutfitPalette(avatar.outfit, primarySwatch)

    // Soft studio glow behind head
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(pedestalColor.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(cx, h * 0.5f),
            radius = w * 0.48f
        ),
        radius = w * 0.48f,
        center = Offset(cx, h * 0.5f)
    )

    // Shoulders
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(outfitColors.first, outfitColors.second)),
        topLeft = Offset(cx - w * 0.30f, h * 0.69f + headBobY * 0.4f),
        size = Size(w * 0.60f, h * 0.34f),
        cornerRadius = CornerRadius(w * 0.16f, w * 0.16f)
    )

    val headCy = h * 0.46f + headBobY
    val rx = w * 0.27f
    val ry = h * 0.25f

    rotate(degrees = headTilt, pivot = Offset(cx, headCy)) {
        drawCharacterHeadAndAccessories(
            cx = cx,
            cy = headCy,
            rx = rx,
            ry = ry,
            avatar = avatar,
            pose = pose,
            isBlinking = isBlinking,
            actionPhase = actionPhase,
            skinColor = skinColor,
            skinShadow = skinShadow,
            hairColor = hairColor,
            accentColor = primarySwatch
        )
    }
}

private fun DrawScope.drawCharacterHeadAndAccessories(
    cx: Float,
    cy: Float,
    rx: Float,
    ry: Float,
    avatar: AvatarConfig,
    pose: CharacterPose,
    isBlinking: Boolean,
    actionPhase: Float,
    skinColor: Color,
    skinShadow: Color,
    hairColor: Color,
    accentColor: Color
) {
    val hairDark = Color(
        red = (hairColor.red * 0.62f).coerceIn(0f, 1f),
        green = (hairColor.green * 0.62f).coerceIn(0f, 1f),
        blue = (hairColor.blue * 0.62f).coerceIn(0f, 1f),
        alpha = 1f
    )

    // Back hair layer for long / ponytail / twin buns / bob
    when (avatar.hairStyle) {
        HairStyle.SLEEK_LONG -> {
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(hairColor, hairDark)),
                topLeft = Offset(cx - rx * 1.18f, cy - ry * 0.4f),
                size = Size(rx * 2.36f, ry * 2.0f),
                cornerRadius = CornerRadius(rx * 0.5f, rx * 0.5f)
            )
        }
        HairStyle.PONYTAIL -> {
            drawOval(
                brush = Brush.verticalGradient(listOf(hairColor, hairDark)),
                topLeft = Offset(cx + rx * 0.62f, cy - ry * 0.45f),
                size = Size(rx * 0.72f, ry * 1.45f)
            )
        }
        HairStyle.TWIN_BUNS -> {
            drawCircle(hairColor, radius = rx * 0.42f, center = Offset(cx - rx * 0.92f, cy - ry * 0.85f))
            drawCircle(hairColor, radius = rx * 0.42f, center = Offset(cx + rx * 0.92f, cy - ry * 0.85f))
        }
        HairStyle.BOB -> {
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(hairColor, hairDark)),
                topLeft = Offset(cx - rx * 1.12f, cy - ry * 0.5f),
                size = Size(rx * 2.24f, ry * 1.35f),
                cornerRadius = CornerRadius(rx * 0.45f, rx * 0.45f)
            )
        }
        else -> Unit
    }

    // Ears
    drawCircle(skinShadow, radius = rx * 0.20f, center = Offset(cx - rx * 0.96f, cy + ry * 0.08f))
    drawCircle(skinShadow, radius = rx * 0.20f, center = Offset(cx + rx * 0.96f, cy + ry * 0.08f))

    // 3D Volumetric Face Sphere
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.28f),
                skinColor,
                skinShadow
            ),
            center = Offset(cx - rx * 0.25f, cy - ry * 0.25f),
            radius = rx * 1.35f
        ),
        topLeft = Offset(cx - rx, cy - ry),
        size = Size(rx * 2f, ry * 2f)
    )

    // Rosy cartoon cheeks
    drawOval(
        color = Color(0xFFFF7A8A).copy(alpha = 0.24f),
        topLeft = Offset(cx - rx * 0.70f, cy + ry * 0.18f),
        size = Size(rx * 0.34f, ry * 0.20f)
    )
    drawOval(
        color = Color(0xFFFF7A8A).copy(alpha = 0.24f),
        topLeft = Offset(cx + rx * 0.36f, cy + ry * 0.18f),
        size = Size(rx * 0.34f, ry * 0.20f)
    )

    // Eyes (with natural blink & expressions!)
    val eyeOffsetX = rx * 0.36f
    val eyeY = cy - ry * 0.02f
    val eyeR = rx * 0.19f
    val irisColor = Color(0xFF1E3A5F)

    if (isBlinking) {
        drawLine(Color(0xFF231815), Offset(cx - eyeOffsetX - eyeR, eyeY), Offset(cx - eyeOffsetX + eyeR, eyeY), strokeWidth = rx * 0.08f, cap = StrokeCap.Round)
        drawLine(Color(0xFF231815), Offset(cx + eyeOffsetX - eyeR, eyeY), Offset(cx + eyeOffsetX + eyeR, eyeY), strokeWidth = rx * 0.08f, cap = StrokeCap.Round)
    } else {
        for (side in listOf(-1f, 1f)) {
            val ex = cx + side * eyeOffsetX
            drawCircle(Color.White, radius = eyeR, center = Offset(ex, eyeY))
            drawCircle(irisColor, radius = eyeR * 0.65f, center = Offset(ex, eyeY))
            drawCircle(Color(0xFF0B0F1A), radius = eyeR * 0.38f, center = Offset(ex, eyeY))
            // Specular Pixar catchlight
            drawCircle(Color.White, radius = eyeR * 0.24f, center = Offset(ex - eyeR * 0.28f, eyeY - eyeR * 0.28f))
            if (avatar.preset.isFeminine) {
                drawLine(
                    color = Color(0xFF1F1614),
                    start = Offset(ex - eyeR * 0.9f, eyeY - eyeR * 0.8f),
                    end = Offset(ex + eyeR * 0.95f, eyeY - eyeR * 0.9f),
                    strokeWidth = rx * 0.06f,
                    cap = StrokeCap.Round
                )
            }
        }
    }

    // Eyebrows
    val browY = eyeY - eyeR * 1.45f
    val browTilt = when (pose) {
        CharacterPose.SURPRISE -> -rx * 0.12f
        CharacterPose.DEFEAT -> rx * 0.08f
        else -> 0f
    }
    drawLine(hairDark, Offset(cx - eyeOffsetX - eyeR, browY), Offset(cx - eyeOffsetX + eyeR, browY + browTilt), strokeWidth = rx * 0.09f, cap = StrokeCap.Round)
    drawLine(hairDark, Offset(cx + eyeOffsetX - eyeR, browY + browTilt), Offset(cx + eyeOffsetX + eyeR, browY), strokeWidth = rx * 0.09f, cap = StrokeCap.Round)

    // Cartoon Nose
    drawOval(
        color = skinShadow.copy(alpha = 0.8f),
        topLeft = Offset(cx - rx * 0.09f, cy + ry * 0.08f),
        size = Size(rx * 0.18f, ry * 0.16f)
    )

    // Expressive Mouth (Talks, smiles, gasps, frowns)
    val mouthY = cy + ry * 0.44f
    when (pose) {
        CharacterPose.SURPRISE -> {
            drawOval(
                color = Color(0xFF7F1D1D),
                topLeft = Offset(cx - rx * 0.16f, mouthY - ry * 0.06f),
                size = Size(rx * 0.32f, ry * 0.26f)
            )
        }
        CharacterPose.DEFEAT -> {
            drawArc(
                color = Color(0xFF4B2420),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(cx - rx * 0.24f, mouthY),
                size = Size(rx * 0.48f, ry * 0.24f),
                style = Stroke(width = rx * 0.07f, cap = StrokeCap.Round)
            )
        }
        CharacterPose.TALKING -> {
            val openH = (0.10f + 0.14f * kotlin.math.abs(sin(actionPhase))) * ry
            drawOval(
                color = Color(0xFF7F1D1D),
                topLeft = Offset(cx - rx * 0.20f, mouthY - openH * 0.3f),
                size = Size(rx * 0.40f, openH.coerceAtLeast(ry * 0.08f))
            )
        }
        else -> {
            // Happy toothy grin
            drawArc(
                color = Color.White,
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = true,
                topLeft = Offset(cx - rx * 0.26f, mouthY - ry * 0.10f),
                size = Size(rx * 0.52f, ry * 0.26f)
            )
        }
    }

    // Front Hair Sculpture
    drawFrontHair(cx, cy, rx, ry, avatar.hairStyle, hairColor, hairDark)

    // Glasses / Visor
    drawGlassesOverlay(cx, eyeY, rx, eyeR, eyeOffsetX, avatar.glasses, accentColor)

    // Hats / Crown / Headphones
    drawHatOverlay(cx, cy, rx, ry, avatar.hat, accentColor)
}

private fun DrawScope.drawFrontHair(
    cx: Float,
    cy: Float,
    rx: Float,
    ry: Float,
    style: HairStyle,
    hairColor: Color,
    hairDark: Color
) {
    val brush = Brush.verticalGradient(listOf(hairColor, hairDark))
    when (style) {
        HairStyle.SPIKY -> {
            val path = Path().apply {
                moveTo(cx - rx * 1.02f, cy - ry * 0.25f)
                lineTo(cx - rx * 0.90f, cy - ry * 1.15f)
                lineTo(cx - rx * 0.42f, cy - ry * 0.85f)
                lineTo(cx - rx * 0.05f, cy - ry * 1.32f)
                lineTo(cx + rx * 0.38f, cy - ry * 0.88f)
                lineTo(cx + rx * 0.88f, cy - ry * 1.12f)
                lineTo(cx + rx * 1.02f, cy - ry * 0.25f)
                quadraticTo(cx, cy - ry * 0.65f, cx - rx * 1.02f, cy - ry * 0.25f)
                close()
            }
            drawPath(path, brush)
        }
        HairStyle.CURLY -> {
            for (i in -3..3) {
                drawCircle(
                    brush = brush,
                    radius = rx * 0.36f,
                    center = Offset(cx + i * rx * 0.26f, cy - ry * (0.88f - kotlin.math.abs(i) * 0.08f))
                )
            }
        }
        HairStyle.MOHAWK -> {
            drawRoundRect(
                brush = brush,
                topLeft = Offset(cx - rx * 0.22f, cy - ry * 1.48f),
                size = Size(rx * 0.44f, ry * 0.92f),
                cornerRadius = CornerRadius(rx * 0.2f, rx * 0.2f)
            )
        }
        else -> {
            drawArc(
                brush = brush,
                startAngle = 185f,
                sweepAngle = 170f,
                useCenter = true,
                topLeft = Offset(cx - rx * 1.06f, cy - ry * 1.12f),
                size = Size(rx * 2.12f, ry * 1.24f)
            )
        }
    }
}

private fun DrawScope.drawGlassesOverlay(
    cx: Float,
    eyeY: Float,
    rx: Float,
    eyeR: Float,
    eyeOffsetX: Float,
    glasses: GlassesStyle,
    accent: Color
) {
    when (glasses) {
        GlassesStyle.NONE -> Unit
        GlassesStyle.DARK_SHADES -> {
            drawRoundRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(cx - eyeOffsetX - eyeR * 1.3f, eyeY - eyeR * 1.05f),
                size = Size(eyeR * 2.6f, eyeR * 1.9f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(cx + eyeOffsetX - eyeR * 1.3f, eyeY - eyeR * 1.05f),
                size = Size(eyeR * 2.6f, eyeR * 1.9f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawLine(Color(0xFF0F172A), Offset(cx - eyeR * 0.4f, eyeY - eyeR * 0.4f), Offset(cx + eyeR * 0.4f, eyeY - eyeR * 0.4f), strokeWidth = rx * 0.08f)
        }
        GlassesStyle.ROUND_SPECS -> {
            drawCircle(Color(0xFF1E293B), radius = eyeR * 1.32f, center = Offset(cx - eyeOffsetX, eyeY), style = Stroke(width = rx * 0.08f))
            drawCircle(Color(0xFF1E293B), radius = eyeR * 1.32f, center = Offset(cx + eyeOffsetX, eyeY), style = Stroke(width = rx * 0.08f))
            drawLine(Color(0xFF1E293B), Offset(cx - eyeR * 0.35f, eyeY), Offset(cx + eyeR * 0.35f, eyeY), strokeWidth = rx * 0.06f)
        }
        GlassesStyle.CYBER_VISOR -> {
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), accent)),
                topLeft = Offset(cx - rx * 0.82f, eyeY - eyeR * 0.95f),
                size = Size(rx * 1.64f, eyeR * 1.85f),
                cornerRadius = CornerRadius(12f, 12f),
                alpha = 0.78f
            )
        }
        GlassesStyle.MONOCLE -> {
            drawCircle(Color(0xFFFFD84D), radius = eyeR * 1.35f, center = Offset(cx + eyeOffsetX, eyeY), style = Stroke(width = rx * 0.08f))
        }
        GlassesStyle.STAR_FRAMES -> {
            drawCircle(Color(0xFFEC4899), radius = eyeR * 1.35f, center = Offset(cx - eyeOffsetX, eyeY), style = Stroke(width = rx * 0.09f))
            drawCircle(Color(0xFFEC4899), radius = eyeR * 1.35f, center = Offset(cx + eyeOffsetX, eyeY), style = Stroke(width = rx * 0.09f))
        }
    }
}

private fun DrawScope.drawHatOverlay(
    cx: Float,
    cy: Float,
    rx: Float,
    ry: Float,
    hat: HatStyle,
    accent: Color
) {
    when (hat) {
        HatStyle.NONE -> Unit
        HatStyle.CROWN -> {
            val topY = cy - ry * 1.05f
            val crownPath = Path().apply {
                moveTo(cx - rx * 0.56f, topY)
                lineTo(cx - rx * 0.62f, topY - ry * 0.58f)
                lineTo(cx - rx * 0.22f, topY - ry * 0.25f)
                lineTo(cx, topY - ry * 0.72f)
                lineTo(cx + rx * 0.22f, topY - ry * 0.25f)
                lineTo(cx + rx * 0.62f, topY - ry * 0.58f)
                lineTo(cx + rx * 0.56f, topY)
                close()
            }
            drawPath(crownPath, Brush.verticalGradient(listOf(Color(0xFFFFE066), Color(0xFFD97706))))
        }
        HatStyle.FEDORA -> {
            drawOval(Color(0xFF334155), topLeft = Offset(cx - rx * 1.15f, cy - ry * 1.02f), size = Size(rx * 2.3f, ry * 0.34f))
            drawRoundRect(Color(0xFF1E293B), topLeft = Offset(cx - rx * 0.68f, cy - ry * 1.50f), size = Size(rx * 1.36f, ry * 0.60f), cornerRadius = CornerRadius(14f, 14f))
        }
        HatStyle.CAP -> {
            drawArc(
                color = accent,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - rx * 0.92f, cy - ry * 1.42f),
                size = Size(rx * 1.84f, ry * 1.04f)
            )
            drawRoundRect(Color(0xFF0F172A), topLeft = Offset(cx - rx * 0.2f, cy - ry * 0.96f), size = Size(rx * 1.18f, ry * 0.20f), cornerRadius = CornerRadius(10f, 10f))
        }
        HatStyle.BEANIE -> {
            drawArc(
                color = accent,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - rx * 0.95f, cy - ry * 1.45f),
                size = Size(rx * 1.9f, ry * 1.1f)
            )
            drawCircle(Color.White, radius = rx * 0.22f, center = Offset(cx, cy - ry * 1.50f))
        }
        HatStyle.HEADPHONES -> {
            drawArc(
                color = Color(0xFF00E5FF),
                startAngle = 185f,
                sweepAngle = 170f,
                useCenter = false,
                topLeft = Offset(cx - rx * 1.08f, cy - ry * 1.18f),
                size = Size(rx * 2.16f, ry * 1.5f),
                style = Stroke(width = rx * 0.14f, cap = StrokeCap.Round)
            )
            drawRoundRect(Color(0xFF1565F5), topLeft = Offset(cx - rx * 1.24f, cy - ry * 0.28f), size = Size(rx * 0.28f, ry * 0.64f), cornerRadius = CornerRadius(12f, 12f))
            drawRoundRect(Color(0xFF1565F5), topLeft = Offset(cx + rx * 0.96f, cy - ry * 0.28f), size = Size(rx * 0.28f, ry * 0.64f), cornerRadius = CornerRadius(12f, 12f))
        }
        HatStyle.HELMET -> {
            drawArc(
                brush = Brush.verticalGradient(listOf(Color(0xFF94A3B8), Color(0xFF334155))),
                startAngle = 175f,
                sweepAngle = 190f,
                useCenter = true,
                topLeft = Offset(cx - rx * 1.12f, cy - ry * 1.46f),
                size = Size(rx * 2.24f, ry * 1.32f)
            )
        }
        HatStyle.HALO -> {
            drawOval(
                color = Color(0xFFFFD84D),
                topLeft = Offset(cx - rx * 0.68f, cy - ry * 1.52f),
                size = Size(rx * 1.36f, ry * 0.26f),
                style = Stroke(width = rx * 0.11f)
            )
        }
    }
}

private fun DrawScope.drawArticulatedArms(
    cx: Float,
    w: Float,
    h: Float,
    shoulderY: Float,
    pose: CharacterPose,
    actionPhase: Float,
    sleeveColor: Color,
    skinColor: Color,
    accessory: AccessoryStyle
) {
    val leftShoulder = Offset(cx - w * 0.15f, shoulderY)
    val rightShoulder = Offset(cx + w * 0.15f, shoulderY)
    val armStroke = w * 0.085f
    val handR = w * 0.048f

    val (leftHand, rightHand) = when (pose) {
        CharacterPose.VICTORY -> Pair(
            Offset(cx - w * 0.30f, shoulderY - h * 0.14f + sin(actionPhase) * h * 0.02f),
            Offset(cx + w * 0.30f, shoulderY - h * 0.14f - sin(actionPhase) * h * 0.02f)
        )
        CharacterPose.POINTING -> Pair(
            Offset(cx - w * 0.26f, shoulderY + h * 0.12f),
            Offset(cx + w * 0.31f, shoulderY - h * 0.03f + sin(actionPhase) * h * 0.015f)
        )
        CharacterPose.THUMBS_UP -> Pair(
            Offset(cx - w * 0.25f, shoulderY + h * 0.13f),
            Offset(cx + w * 0.30f, shoulderY - h * 0.04f + sin(actionPhase) * h * 0.012f)
        )
        CharacterPose.THINKING -> Pair(
            Offset(cx - w * 0.25f, shoulderY + h * 0.13f),
            Offset(cx + w * 0.08f, shoulderY - h * 0.08f)
        )
        CharacterPose.TALKING -> Pair(
            Offset(cx - w * 0.28f, shoulderY + h * 0.05f + sin(actionPhase) * h * 0.03f),
            Offset(cx + w * 0.28f, shoulderY + h * 0.05f - sin(actionPhase) * h * 0.03f)
        )
        CharacterPose.WALKING, CharacterPose.RUNNING -> Pair(
            Offset(cx - w * 0.26f, shoulderY + h * 0.12f + sin(actionPhase) * h * 0.05f),
            Offset(cx + w * 0.26f, shoulderY + h * 0.12f - sin(actionPhase) * h * 0.05f)
        )
        else -> Pair(
            Offset(cx - w * 0.25f, shoulderY + h * 0.13f + sin(actionPhase) * h * 0.01f),
            Offset(cx + w * 0.25f, shoulderY + h * 0.13f - sin(actionPhase) * h * 0.01f)
        )
    }

    drawLine(sleeveColor, leftShoulder, leftHand, strokeWidth = armStroke, cap = StrokeCap.Round)
    drawLine(sleeveColor, rightShoulder, rightHand, strokeWidth = armStroke, cap = StrokeCap.Round)

    drawCircle(skinColor, radius = handR, center = leftHand)
    drawCircle(skinColor, radius = handR, center = rightHand)

    // Hand Accessories (Magnifier / Mic / Watch)
    when (accessory) {
        AccessoryStyle.MAGNIFIER -> {
            drawCircle(Color(0xFF38BDF8).copy(alpha = 0.45f), radius = w * 0.06f, center = Offset(rightHand.x + w * 0.04f, rightHand.y - h * 0.04f))
            drawCircle(Color(0xFFFFD84D), radius = w * 0.06f, center = Offset(rightHand.x + w * 0.04f, rightHand.y - h * 0.04f), style = Stroke(width = 4f))
        }
        AccessoryStyle.MIC -> {
            drawLine(Color(0xFF334155), rightHand, Offset(rightHand.x, rightHand.y - h * 0.06f), strokeWidth = 5f, cap = StrokeCap.Round)
            drawCircle(Color(0xFF94A3B8), radius = w * 0.032f, center = Offset(rightHand.x, rightHand.y - h * 0.065f))
        }
        AccessoryStyle.WATCH -> {
            drawCircle(Color(0xFF00E5FF), radius = w * 0.025f, center = Offset(leftHand.x, leftHand.y - h * 0.02f))
        }
        else -> Unit
    }
}

private fun DrawScope.drawOutfitDetails(
    cx: Float,
    torsoTop: Float,
    torsoW: Float,
    torsoH: Float,
    outfit: OutfitStyle,
    accent: Color
) {
    when (outfit) {
        OutfitStyle.RED_HOODIE -> {
            drawLine(Color.White, Offset(cx - torsoW * 0.12f, torsoTop + torsoH * 0.08f), Offset(cx - torsoW * 0.12f, torsoTop + torsoH * 0.44f), strokeWidth = 3f, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(cx + torsoW * 0.12f, torsoTop + torsoH * 0.08f), Offset(cx + torsoW * 0.12f, torsoTop + torsoH * 0.44f), strokeWidth = 3f, cap = StrokeCap.Round)
        }
        OutfitStyle.CYBER_ARMOR, OutfitStyle.NEON_TRACK -> {
            drawLine(Color(0xFF00E5FF), Offset(cx, torsoTop + torsoH * 0.12f), Offset(cx, torsoTop + torsoH * 0.90f), strokeWidth = 4f)
            drawCircle(accent, radius = torsoW * 0.12f, center = Offset(cx, torsoTop + torsoH * 0.36f))
        }
        OutfitStyle.TUXEDO -> {
            val tiePath = Path().apply {
                moveTo(cx - torsoW * 0.08f, torsoTop + torsoH * 0.08f)
                lineTo(cx + torsoW * 0.08f, torsoTop + torsoH * 0.08f)
                lineTo(cx, torsoTop + torsoH * 0.56f)
                close()
            }
            drawPath(tiePath, Color(0xFFEF4444))
        }
        OutfitStyle.DETECTIVE_COAT -> {
            drawLine(Color(0xFF475569), Offset(cx, torsoTop + torsoH * 0.08f), Offset(cx, torsoTop + torsoH * 0.94f), strokeWidth = 3f)
        }
        else -> Unit
    }
}

private fun resolveOutfitPalette(
    outfit: OutfitStyle,
    swatch: Color
): Triple<Color, Color, Color> = when (outfit) {
    OutfitStyle.RED_HOODIE -> Triple(Color(0xFFEF4444), Color(0xFF991B1B), swatch)
    OutfitStyle.PURPLE_JACKET -> Triple(Color(0xFF9333EA), Color(0xFF581C87), Color(0xFF00E5FF))
    OutfitStyle.DETECTIVE_COAT -> Triple(Color(0xFF64748B), Color(0xFF334155), Color(0xFFFFD84D))
    OutfitStyle.YELLOW_TEE -> Triple(Color(0xFFFACC15), Color(0xFFCA8A04), Color.White)
    OutfitStyle.CYBER_ARMOR -> Triple(Color(0xFF0EA5E9), Color(0xFF0369A1), Color(0xFF00E5FF))
    OutfitStyle.JERSEY -> Triple(swatch, Color(0xFF1E293B), Color(0xFFFFD84D))
    OutfitStyle.TUXEDO -> Triple(Color(0xFF1E293B), Color(0xFF090F1F), Color.White)
    OutfitStyle.NEON_TRACK -> Triple(Color(0xFFEC4899), Color(0xFF831843), Color(0xFF00E5FF))
}
