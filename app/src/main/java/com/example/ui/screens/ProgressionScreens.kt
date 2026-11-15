package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TheaterComedy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.AchievementProgress
import com.example.domain.AppLanguage
import com.example.domain.AvatarConfig
import com.example.domain.AvatarPreset
import com.example.domain.CharacterPose
import com.example.domain.PlayerProfile
import com.example.domain.RoundHistoryItem
import com.example.domain.WinnerSide
import com.example.localization.AppStrings
import com.example.ui.components.AnimatedXpProgressBar
import com.example.ui.components.Cartoon3DRiggedCharacter
import com.example.ui.components.Neon3DButton
import com.example.ui.components.Neon3DPanel
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonCyberBackground
import com.example.ui.components.NeonTopHeaderBar
import com.example.ui.theme.LocalStudioPalette

// ============================================================
// SCREEN 17: PLAYER PROFILE (+ Game History Sheet)
// ============================================================
@Composable
fun PlayerProfileScreen(
    profiles: List<PlayerProfile>,
    activeProfile: PlayerProfile?,
    history: List<RoundHistoryItem>,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onSetActiveProfile: (Long) -> Unit,
    onCreateProfile: (String, AvatarConfig) -> Unit,
    onDeleteProfile: (Long) -> Unit,
    onOpenAvatarCustomization: () -> Unit,
    onOpenAchievements: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val shown = activeProfile ?: profiles.firstOrNull()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.playerProfileBtn,
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
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (shown != null) {
                        Neon3DPanel(
                            borderColor = palette.cyan,
                            pulseGlow = true,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Cartoon3DRiggedCharacter(
                                    avatar = shown.avatar,
                                    size = 112.dp,
                                    isPortraitOnly = true,
                                    pose = CharacterPose.THUMBS_UP,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(palette.bgMid)
                                        .border(2.dp, palette.cyan, RoundedCornerShape(18.dp))
                                        .clickable { onOpenAvatarCustomization() }
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = shown.name,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = palette.ink
                                    )
                                    Text(
                                        text = "${strings.levelLabel}: ${shown.level}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.cyan
                                    )
                                    AnimatedXpProgressBar(
                                        xpIntoLevel = shown.xpIntoCurrentLevel,
                                        xpPerLevel = shown.xpPerLevel
                                    )
                                }
                            }
                        }

                        // Stats Grid matching Reference Screen 17
                        Neon3DPanel(borderColor = palette.cardBorderAlt, modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    StatTile(strings.winsLabel, "${shown.totalWins}", palette.success)
                                    StatTile(strings.lossesLabel, "${shown.losses}", palette.danger)
                                    StatTile(strings.impWinsLabel, "${shown.imposterWins}", palette.primaryTop)
                                    StatTile(strings.crewWinsLabel, "${shown.crewWins}", palette.cyan)
                                }
                            }
                        }
                    }

                    // Switch / Add Profiles Strip
                    Neon3DPanel(borderColor = palette.cardBorder, modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${strings.playerProfileBtn} (${profiles.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.ink
                                )
                                Neon3DButton(
                                    onClick = { showCreateDialog = true },
                                    style = NeonButtonStyle.GREEN_CONFIRM,
                                    vibrationEnabled = vibrationEnabled,
                                    pulseGlow = false,
                                    height = 38.dp,
                                    testTag = "profile_create_btn"
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.createProfileBtn, style = MaterialTheme.typography.labelLarge, color = Color.White)
                                }
                            }

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                profiles.forEach { prof ->
                                    val isAct = prof.id == shown?.id
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isAct) palette.secondary else palette.bgMid)
                                            .border(
                                                1.5.dp,
                                                if (isAct) palette.primaryTop else palette.cardBorder.copy(alpha = 0.4f),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable { onSetActiveProfile(prof.id) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Cartoon3DRiggedCharacter(
                                            avatar = prof.avatar,
                                            size = 34.dp,
                                            isPortraitOnly = true
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = prof.name,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.ink
                                        )
                                        if (profiles.size > 1) {
                                            IconButton(
                                                onClick = { onDeleteProfile(prof.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Delete,
                                                    contentDescription = "Delete profile",
                                                    tint = palette.danger,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Neon3DButton(
                            onClick = onOpenAchievements,
                            style = NeonButtonStyle.BLUE_GLOSSY,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = false,
                            testTag = "profile_achievements_btn",
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = palette.primaryTop)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.achievementsBtn, fontWeight = FontWeight.Black, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        Neon3DButton(
                            onClick = { showHistoryDialog = true },
                            style = NeonButtonStyle.BLUE_GLOSSY,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = false,
                            testTag = "profile_history_btn",
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.History, contentDescription = null, tint = palette.cyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.historyBtn, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var newName by remember { mutableStateOf("") }
        var pickedPreset by remember { mutableStateOf(AvatarPreset.ROHAN) }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = palette.surface,
            titleContentColor = palette.ink,
            textContentColor = palette.ink,
            title = { Text(strings.createProfileBtn, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it.take(18) },
                        label = { Text(strings.playerNameHint) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AvatarPreset.entries.take(8).forEach { preset ->
                            val sel = pickedPreset == preset
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (sel) palette.primaryTop else palette.bgMid)
                                    .clickable { pickedPreset = preset }
                                    .padding(6.dp)
                            ) {
                                Cartoon3DRiggedCharacter(
                                    avatar = AvatarConfig.fromPreset(preset),
                                    size = 44.dp,
                                    isPortraitOnly = true
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onCreateProfile(newName.ifBlank { pickedPreset.displayName }, AvatarConfig.fromPreset(pickedPreset))
                    showCreateDialog = false
                }) {
                    Text(strings.saveBtn, color = palette.primaryTop, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(strings.backBtn, color = palette.mutedInk)
                }
            }
        )
    }

    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            containerColor = palette.surface,
            titleContentColor = palette.ink,
            textContentColor = palette.ink,
            title = { Text(strings.gameHistoryTitle, fontWeight = FontWeight.Black) },
            text = {
                if (history.isEmpty()) {
                    Text(strings.emptyHistoryMsg, color = palette.mutedInk)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 360.dp)
                    ) {
                        items(history, key = { it.id }) { item ->
                            val isCrew = item.winnerSide == WinnerSide.CREW
                            val word = if (language == AppLanguage.HINDI) item.secretWordHi else item.secretWordEn
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.bgMid)
                                    .border(1.dp, if (isCrew) palette.success else palette.danger, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isCrew) strings.crewWinsHeadline else strings.imposterWinsHeadline,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCrew) palette.success else palette.danger
                                    )
                                    Text("+${item.xpAwarded} XP", color = palette.primaryTop, fontWeight = FontWeight.Bold)
                                }
                                Text("${strings.secretWordPrefix}: $word", color = palette.ink, style = MaterialTheme.typography.bodyMedium)
                                Text("${strings.impostersLabel}: ${item.imposterNames} (${item.playerCount}p)", color = palette.mutedInk, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text(strings.backBtn, color = palette.primaryTop, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = LocalStudioPalette.current.mutedInk)
    }
}

// ============================================================
// SCREEN 18: ACHIEVEMENTS (10 Animated 3D Trophy Badges)
// ============================================================
@Composable
fun AchievementsScreen(
    achievements: List<AchievementProgress>,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size.coerceAtLeast(10)
    val animatedProgress by animateFloatAsState(
        targetValue = unlockedCount.toFloat() / totalCount.toFloat(),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "ach_overall_prog"
    )

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.achievementsTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(achievements, key = { it.def.id }) { item ->
                            val title = if (language == AppLanguage.HINDI) item.def.titleHi else item.def.titleEn
                            val desc = if (language == AppLanguage.HINDI) item.def.descHi else item.def.descEn
                            val progFrac = (item.progress.toFloat() / item.def.target.coerceAtLeast(1)).coerceIn(0f, 1f)

                            Neon3DPanel(
                                borderColor = if (item.isUnlocked) palette.primaryTop else palette.cardBorder.copy(alpha = 0.5f),
                                pulseGlow = item.isUnlocked,
                                contentPadding = PaddingValues(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .shadow(if (item.isUnlocked) 12.dp else 0.dp, CircleShape, ambientColor = palette.primaryTop)
                                            .clip(CircleShape)
                                            .background(
                                                if (item.isUnlocked) {
                                                    Brush.verticalGradient(listOf(palette.primaryTop, palette.primary))
                                                } else {
                                                    Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                                                }
                                            )
                                            .border(1.5.dp, if (item.isUnlocked) Color.White else palette.mutedInk.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = achievementIcon(item.def.iconKey),
                                            contentDescription = title,
                                            tint = if (item.isUnlocked) Color(0xFF1E1300) else palette.mutedInk,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black,
                                                color = if (item.isUnlocked) palette.primaryTop else palette.ink
                                            )
                                            Text(
                                                text = if (item.isUnlocked) "★ ${strings.unlockedBadge}" else "${item.progress}/${item.def.target}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.isUnlocked) palette.success else palette.cyan
                                            )
                                        }
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = palette.mutedInk
                                        )
                                        LinearProgressIndicator(
                                            progress = { progFrac },
                                            color = if (item.isUnlocked) palette.primaryTop else palette.cyan,
                                            trackColor = palette.bgTop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(50))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Overall X/10 Badge Counter Bar matching Reference Screen 18
                    Neon3DPanel(
                        borderColor = palette.primaryTop,
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                color = palette.primaryTop,
                                trackColor = palette.bgTop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$unlockedCount/$totalCount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = palette.primaryTop,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun achievementIcon(key: String): ImageVector = when (key) {
    "trophy" -> Icons.Rounded.EmojiEvents
    "shield" -> Icons.Rounded.Shield
    "mask" -> Icons.Rounded.TheaterComedy
    "star" -> Icons.Rounded.Star
    "crown" -> Icons.Rounded.MilitaryTech
    "bot" -> Icons.Rounded.SmartToy
    "flame" -> Icons.Rounded.LocalFireDepartment
    "globe" -> Icons.Rounded.Public
    "party" -> Icons.Rounded.CheckCircle
    else -> Icons.Rounded.EmojiEvents
}
