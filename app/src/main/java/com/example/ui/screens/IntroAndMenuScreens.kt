package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserSettings
import com.example.domain.AppLanguage
import com.example.domain.AvatarConfig
import com.example.domain.AvatarPreset
import com.example.domain.CharacterPose
import com.example.domain.GraphicsQuality
import com.example.domain.PlayerProfile
import com.example.localization.AppStrings
import com.example.ui.components.BilingualFlagGraphic
import com.example.ui.components.Cartoon3DRiggedCharacter
import com.example.ui.components.ImposterG3DLogo
import com.example.ui.components.Neon3DButton
import com.example.ui.components.Neon3DPanel
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonCyberBackground
import com.example.ui.components.NeonSwitchRow
import com.example.ui.components.NeonTopHeaderBar
import com.example.ui.theme.LocalMotionConfig
import com.example.ui.theme.LocalStudioPalette
import kotlinx.coroutines.delay

// ============================================================
// SCREEN 1: SPLASH SCREEN
// ============================================================
@Composable
fun SplashScreen(
    strings: AppStrings,
    onFinished: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val motion = LocalMotionConfig.current
    val scaleAnim = remember { Animatable(if (motion.reduceMotion) 1f else 0.72f) }
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow))
        progressAnim.animateTo(1f, tween(1350, easing = FastOutSlowInEasing))
        delay(220)
        onFinished()
    }

    NeonCyberBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .clickable { onFinished() }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.widthIn(max = 520.dp)
            ) {
                ImposterG3DLogo(
                    tagline = strings.tagline,
                    offlineBadge = strings.offlineBadge,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                    }
                )
                Spacer(modifier = Modifier.height(22.dp))
                Cartoon3DRiggedCharacter(
                    avatar = AvatarConfig.fromPreset(AvatarPreset.LEO),
                    size = 210.dp,
                    pose = CharacterPose.VICTORY,
                    showPedestal = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                LinearProgressIndicator(
                    progress = { progressAnim.value },
                    color = palette.primaryTop,
                    trackColor = palette.surface,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                )
            }
        }
    }
}

// ============================================================
// SCREEN 2: MAIN MENU
// ============================================================
@Composable
fun MainMenuScreen(
    activeProfile: PlayerProfile?,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onPlayOffline: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenAvatarCustom: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLanguage: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val leadAvatar = activeProfile?.avatar ?: AvatarConfig.fromPreset(AvatarPreset.LEO)
    val stageCrew = remember(leadAvatar) {
        listOf(
            Triple(AvatarConfig.fromPreset(AvatarPreset.ARJUN), CharacterPose.POINTING, 118.dp),
            Triple(AvatarConfig.fromPreset(AvatarPreset.RIYA), CharacterPose.IDLE, 124.dp),
            Triple(leadAvatar, CharacterPose.THUMBS_UP, 146.dp),
            Triple(AvatarConfig.fromPreset(AvatarPreset.ROHAN), CharacterPose.TALKING, 124.dp),
            Triple(AvatarConfig.fromPreset(AvatarPreset.NEHA), CharacterPose.VICTORY, 118.dp)
        )
    }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 580.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImposterG3DLogo(
                    tagline = strings.tagline,
                    offlineBadge = strings.offlineBadge
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3D Cyber Stage with 5 live animated cartoon characters
                Neon3DPanel(
                    borderColor = palette.cyan,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        stageCrew.forEachIndexed { index, (av, pose, charSize) ->
                            Cartoon3DRiggedCharacter(
                                avatar = av,
                                size = charSize * 0.62f,
                                pose = pose,
                                showPedestal = true,
                                seedOffset = index,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bright Yellow Primary CTA: Play Offline
                Neon3DButton(
                    onClick = onPlayOffline,
                    style = NeonButtonStyle.GOLD_PRIMARY,
                    vibrationEnabled = vibrationEnabled,
                    pulseGlow = true,
                    height = 62.dp,
                    cornerRadius = 20.dp,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "menu_play_offline"
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF1E1300),
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.playOffline,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1300)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2-Column Grid of 6 Glossy Cobalt Blue Menu Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuTileButton(
                        icon = Icons.Rounded.Person,
                        label = strings.playerProfileBtn,
                        onClick = onOpenProfile,
                        vibrationEnabled = vibrationEnabled,
                        testTag = "menu_profile",
                        modifier = Modifier.weight(1f)
                    )
                    MenuTileButton(
                        icon = Icons.Rounded.EmojiEvents,
                        label = strings.achievementsBtn,
                        onClick = onOpenAchievements,
                        vibrationEnabled = vibrationEnabled,
                        testTag = "menu_achievements",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuTileButton(
                        icon = Icons.Rounded.Face,
                        label = strings.avatarCustomBtn,
                        onClick = onOpenAvatarCustom,
                        vibrationEnabled = vibrationEnabled,
                        testTag = "menu_avatar",
                        modifier = Modifier.weight(1f)
                    )
                    MenuTileButton(
                        icon = Icons.AutoMirrored.Rounded.HelpOutline,
                        label = strings.howToPlayBtn,
                        onClick = onOpenHowToPlay,
                        vibrationEnabled = vibrationEnabled,
                        testTag = "menu_how_to_play",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuTileButton(
                        icon = Icons.Rounded.Settings,
                        label = strings.settingsBtn,
                        onClick = onOpenSettings,
                        vibrationEnabled = vibrationEnabled,
                        testTag = "menu_settings",
                        modifier = Modifier.weight(1f)
                    )
                    MenuTileButton(
                        icon = Icons.Rounded.Language,
                        label = strings.languageBtn,
                        onClick = onOpenLanguage,
                        vibrationEnabled = vibrationEnabled,
                        testTag = "menu_language",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuTileButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    vibrationEnabled: Boolean,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Neon3DButton(
        onClick = onClick,
        style = NeonButtonStyle.BLUE_GLOSSY,
        vibrationEnabled = vibrationEnabled,
        pulseGlow = false,
        height = 56.dp,
        cornerRadius = 16.dp,
        testTag = testTag,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ============================================================
// SCREEN 3: LANGUAGE SELECTION
// ============================================================
@Composable
fun LanguageSelectionScreen(
    selectedLanguage: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onSelectLanguage: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val palette = LocalStudioPalette.current

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.languageScreenTitle,
                onBack = onBack,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 580.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Cartoon3DRiggedCharacter(
                        avatar = AvatarConfig.fromPreset(AvatarPreset.LEO),
                        size = 165.dp,
                        pose = CharacterPose.THUMBS_UP,
                        showPedestal = true
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        LanguageOptionCard(
                            label = "हिन्दी",
                            subLabel = "Hindi",
                            isIndiaFlag = true,
                            selected = selectedLanguage == AppLanguage.HINDI,
                            onClick = { onSelectLanguage(AppLanguage.HINDI) },
                            testTag = "lang_card_hi"
                        )
                        LanguageOptionCard(
                            label = "English",
                            subLabel = "अंग्रेज़ी",
                            isIndiaFlag = false,
                            selected = selectedLanguage == AppLanguage.ENGLISH,
                            onClick = { onSelectLanguage(AppLanguage.ENGLISH) },
                            testTag = "lang_card_en"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Neon3DButton(
                    onClick = onNext,
                    style = NeonButtonStyle.BLUE_GLOSSY,
                    vibrationEnabled = vibrationEnabled,
                    pulseGlow = true,
                    height = 58.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp),
                    testTag = "lang_next_btn"
                ) {
                    Text(
                        text = strings.nextBtn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = palette.ink
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionCard(
    label: String,
    subLabel: String,
    isIndiaFlag: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val palette = LocalStudioPalette.current
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "lang_scale"
    )

    Neon3DPanel(
        borderColor = if (selected) palette.primaryTop else palette.cardBorder,
        fillColor = if (selected) palette.secondary else palette.surface,
        pulseGlow = selected,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BilingualFlagGraphic(isIndia = isIndiaFlag)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = palette.ink
                )
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.mutedInk
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = palette.primaryTop,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ============================================================
// SCREEN 19: SETTINGS SCREEN
// ============================================================
@Composable
fun SettingsScreen(
    settings: UserSettings,
    strings: AppStrings,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onMusicChange: (Boolean) -> Unit,
    onSfxChange: (Boolean) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onQualityChange: (GraphicsQuality) -> Unit,
    onReduceMotionChange: (Boolean) -> Unit,
    onToggleLanguage: () -> Unit,
    onReset: () -> Unit
) {
    val palette = LocalStudioPalette.current

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.settingsTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = settings.vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 580.dp)) {
                    Neon3DPanel(
                        borderColor = palette.cyan,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            NeonSwitchRow(
                                label = strings.musicToggle,
                                checked = settings.musicEnabled,
                                onCheckedChange = onMusicChange,
                                testTag = "setting_music"
                            )
                            NeonSwitchRow(
                                label = strings.sfxToggle,
                                checked = settings.sfxEnabled,
                                onCheckedChange = onSfxChange,
                                testTag = "setting_sfx"
                            )
                            NeonSwitchRow(
                                label = strings.vibrationToggle,
                                checked = settings.vibrationEnabled,
                                onCheckedChange = onVibrationChange,
                                testTag = "setting_vibration"
                            )
                            NeonSwitchRow(
                                label = strings.reduceMotionToggle,
                                checked = settings.reduceMotion,
                                onCheckedChange = onReduceMotionChange,
                                testTag = "setting_reduce_motion"
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.graphicsQualityLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = palette.ink
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GraphicsQuality.entries.forEach { q ->
                                    val active = settings.graphicsQuality == q
                                    Neon3DButton(
                                        onClick = { onQualityChange(q) },
                                        style = if (active) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                        vibrationEnabled = settings.vibrationEnabled,
                                        pulseGlow = false,
                                        height = 48.dp,
                                        testTag = "setting_quality_${q.id}",
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = strings.qualityName(q, settings.language),
                                            fontWeight = FontWeight.Bold,
                                            color = if (active) Color(0xFF1E1300) else Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Neon3DButton(
                                onClick = onToggleLanguage,
                                style = NeonButtonStyle.BLUE_GLOSSY,
                                vibrationEnabled = settings.vibrationEnabled,
                                pulseGlow = false,
                                height = 50.dp,
                                testTag = "setting_language_toggle",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.Language, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${strings.languageBtn}: ${settings.language.displayLabel}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Neon3DButton(
                                onClick = onReset,
                                style = NeonButtonStyle.BLUE_GLOSSY,
                                vibrationEnabled = settings.vibrationEnabled,
                                pulseGlow = false,
                                testTag = "setting_reset_btn",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(strings.resetBtn, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Neon3DButton(
                                onClick = onBack,
                                style = NeonButtonStyle.SLATE_MUTED,
                                vibrationEnabled = settings.vibrationEnabled,
                                pulseGlow = false,
                                testTag = "setting_back_btn",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(strings.backBtn, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Cartoon3DRiggedCharacter(
                            avatar = AvatarConfig.fromPreset(AvatarPreset.GAMER_PRO),
                            size = 135.dp,
                            pose = CharacterPose.THUMBS_UP,
                            showPedestal = true
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 20: HOW TO PLAY
// ============================================================
@Composable
fun HowToPlayScreen(
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    val palette = LocalStudioPalette.current
    var selectedStep by remember { mutableIntStateOf(0) }
    val steps = listOf(
        Triple("1", strings.step1Title, strings.step1Desc),
        Triple("2", strings.step2Title, strings.step2Desc),
        Triple("3", strings.step3Title, strings.step3Desc),
        Triple("4", strings.step4Title, strings.step4Desc)
    )
    val poses = listOf(
        CharacterPose.THINKING,
        CharacterPose.TALKING,
        CharacterPose.POINTING,
        CharacterPose.VICTORY
    )

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.howToPlayTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 580.dp)) {
                    steps.forEachIndexed { idx, (num, title, _) ->
                        val active = idx == selectedStep
                        Neon3DButton(
                            onClick = { selectedStep = idx },
                            style = if (active) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.BLUE_GLOSSY,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = active,
                            height = 54.dp,
                            testTag = "howto_step_$idx",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (active) Color(0xFF1E1300) else palette.cyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = num,
                                    fontWeight = FontWeight.Black,
                                    color = if (active) palette.primaryTop else Color(0xFF050A18)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = title,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (active) Color(0xFF1E1300) else Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = if (active) Color(0xFF1E1300) else palette.primaryTop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Neon3DPanel(
                        borderColor = palette.primaryTop,
                        pulseGlow = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .shadow(10.dp, CircleShape, ambientColor = palette.primaryTop)
                                        .clip(CircleShape)
                                        .background(palette.primaryTop.copy(alpha = 0.2f))
                                        .border(1.5.dp, palette.primaryTop, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Lightbulb,
                                        contentDescription = "Tip",
                                        tint = palette.primaryTop,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Cartoon3DRiggedCharacter(
                                    avatar = AvatarConfig.fromPreset(AvatarPreset.LEO),
                                    size = 112.dp,
                                    pose = poses[selectedStep],
                                    showPedestal = true
                                )
                            }
                            AnimatedContent(
                                targetState = selectedStep,
                                label = "howto_desc",
                                modifier = Modifier.weight(1f)
                            ) { stepIdx ->
                                Column {
                                    Text(
                                        text = "${steps[stepIdx].first}. ${steps[stepIdx].second}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = palette.primaryTop
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = steps[stepIdx].third,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = palette.ink
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Neon3DButton(
                        onClick = onBack,
                        style = NeonButtonStyle.BLUE_GLOSSY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = false,
                        testTag = "howto_back_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.backBtn, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}
