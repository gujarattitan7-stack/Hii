package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Icecream
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.SportsCricket
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.words.WordBank
import com.example.domain.AccessoryStyle
import com.example.domain.ActiveMatchState
import com.example.domain.AppLanguage
import com.example.domain.AvatarConfig
import com.example.domain.AvatarPreset
import com.example.domain.CategoryId
import com.example.domain.CharacterPose
import com.example.domain.DifficultyLevel
import com.example.domain.GameModeType
import com.example.domain.GlassesStyle
import com.example.domain.HairStyle
import com.example.domain.HatStyle
import com.example.domain.MatchConfig
import com.example.domain.OutfitStyle
import com.example.domain.PlayerProfile
import com.example.domain.PlayerSlot
import com.example.localization.AppStrings
import com.example.ui.components.Cartoon3DRiggedCharacter
import com.example.ui.components.ChallengeWheel3D
import com.example.ui.components.Neon3DButton
import com.example.ui.components.Neon3DIconButton
import com.example.ui.components.Neon3DPanel
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonCyberBackground
import com.example.ui.components.NeonSwitchRow
import com.example.ui.components.NeonTopHeaderBar
import com.example.ui.theme.CategoryAccentColors
import com.example.ui.theme.CharacterColorSwatches
import com.example.ui.theme.LocalStudioPalette
import com.example.ui.theme.PedestalColors
import kotlinx.coroutines.delay

// ============================================================
// SCREEN 4: PLAYER SELECTION
// ============================================================
@Composable
fun PlayerSelectionScreen(
    state: ActiveMatchState,
    profiles: List<PlayerProfile>,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPlayerCountChange: (Int) -> Unit,
    onUpdateSlotName: (String, String) -> Unit,
    onToggleSlotBot: (String) -> Unit,
    onAssignProfile: (String, PlayerProfile) -> Unit,
    onUpdateConfig: ((MatchConfig) -> MatchConfig) -> Unit,
    maxImpostersForCount: (Int) -> Int,
    onNext: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val cfg = state.config
    var editingSlot by remember { mutableStateOf<PlayerSlot?>(null) }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.playerSelectionTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = strings.selectPlayersLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = palette.cyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Stepper: - N +
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Neon3DIconButton(
                            icon = Icons.Rounded.Remove,
                            contentDescription = "Decrease players",
                            onClick = { onPlayerCountChange(cfg.playerCount - 1) },
                            enabled = cfg.playerCount > 1,
                            vibrationEnabled = vibrationEnabled,
                            testTag = "player_count_minus"
                        )
                        Neon3DPanel(
                            borderColor = palette.cyan,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${cfg.playerCount}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = palette.ink
                            )
                        }
                        Neon3DIconButton(
                            icon = Icons.Rounded.Add,
                            contentDescription = "Increase players",
                            onClick = { onPlayerCountChange(cfg.playerCount + 1) },
                            enabled = cfg.playerCount < 10,
                            vibrationEnabled = vibrationEnabled,
                            testTag = "player_count_plus"
                        )
                    }

                    if (cfg.playerCount <= 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.tertiary.copy(alpha = 0.25f))
                                .border(1.dp, palette.tertiary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = strings.practiceHintBadge,
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.primaryTop,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal 3D Player Cards + Add Slot Card
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(state.players, key = { _, p -> p.slotId }) { index, slot ->
                            Neon3DPanel(
                                borderColor = if (slot.isBot) palette.tertiary else palette.cyan,
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier
                                    .width(96.dp)
                                    .testTag("player_slot_card_$index")
                                    .clickable { editingSlot = slot }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Cartoon3DRiggedCharacter(
                                        avatar = slot.avatar,
                                        size = 76.dp,
                                        isPortraitOnly = true,
                                        pose = CharacterPose.IDLE,
                                        seedOffset = index
                                    )
                                    Text(
                                        text = slot.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.ink,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (slot.isBot) strings.botTag else if (slot.isHost) strings.hostTag else strings.humanTag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (slot.isBot) palette.tertiary else palette.primaryTop
                                    )
                                }
                            }
                        }

                        if (cfg.playerCount < 10) {
                            item {
                                Neon3DButton(
                                    onClick = { onPlayerCountChange(cfg.playerCount + 1) },
                                    style = NeonButtonStyle.BLUE_GLOSSY,
                                    vibrationEnabled = vibrationEnabled,
                                    pulseGlow = false,
                                    height = 122.dp,
                                    testTag = "player_add_slot",
                                    modifier = Modifier.width(78.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Add player",
                                        tint = Color.White,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Imposter Count Stepper & Guide Character
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${strings.impostersLabel}: ${cfg.imposterCount}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = palette.ink
                            )
                            val maxImp = maxImpostersForCount(cfg.playerCount)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Neon3DIconButton(
                                    icon = Icons.Rounded.Remove,
                                    contentDescription = "Decrease imposters",
                                    style = NeonButtonStyle.GREEN_CONFIRM,
                                    onClick = {
                                        onUpdateConfig { it.copy(imposterCount = (it.imposterCount - 1).coerceAtLeast(1), randomImposterCount = false) }
                                    },
                                    enabled = cfg.imposterCount > 1,
                                    vibrationEnabled = vibrationEnabled,
                                    testTag = "imposter_count_minus"
                                )
                                Text(
                                    text = "${cfg.imposterCount}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = palette.primaryTop
                                )
                                Neon3DIconButton(
                                    icon = Icons.Rounded.Add,
                                    contentDescription = "Increase imposters",
                                    style = NeonButtonStyle.BLUE_GLOSSY,
                                    onClick = {
                                        onUpdateConfig { it.copy(imposterCount = (it.imposterCount + 1).coerceAtMost(maxImp), randomImposterCount = false) }
                                    },
                                    enabled = cfg.imposterCount < maxImp,
                                    vibrationEnabled = vibrationEnabled,
                                    testTag = "imposter_count_plus"
                                )
                            }
                        }

                        Cartoon3DRiggedCharacter(
                            avatar = AvatarConfig.fromPreset(AvatarPreset.ANANYA),
                            size = 135.dp,
                            pose = CharacterPose.POINTING,
                            showPedestal = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Neon3DButton(
                        onClick = onNext,
                        style = NeonButtonStyle.BLUE_GLOSSY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 58.dp,
                        testTag = "player_next_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.nextBtn, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }

    editingSlot?.let { slot ->
        var draftName by remember(slot.slotId) { mutableStateOf(slot.name) }
        AlertDialog(
            onDismissRequest = { editingSlot = null },
            containerColor = palette.surface,
            titleContentColor = palette.ink,
            textContentColor = palette.ink,
            title = { Text(slot.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = draftName,
                        onValueChange = { draftName = it },
                        label = { Text(strings.playerNameHint) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Neon3DButton(
                        onClick = {
                            onToggleSlotBot(slot.slotId)
                            editingSlot = null
                        },
                        style = NeonButtonStyle.PURPLE_MYSTERY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = false,
                        height = 46.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (slot.isBot) strings.humanTag else strings.botTag,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    if (profiles.isNotEmpty()) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            profiles.forEach { profile ->
                                TextButton(
                                    onClick = {
                                        onAssignProfile(slot.slotId, profile)
                                        editingSlot = null
                                    }
                                ) {
                                    Text(profile.name, color = palette.cyan)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateSlotName(slot.slotId, draftName)
                    editingSlot = null
                }) {
                    Text(strings.saveBtn, color = palette.primaryTop, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSlot = null }) {
                    Text(strings.backBtn, color = palette.mutedInk)
                }
            }
        )
    }
}

// ============================================================
// SCREEN 5: AVATAR CUSTOMIZATION (20+ Avatars & Live 3D Rig)
// ============================================================
@Composable
fun AvatarCustomizationScreen(
    activeProfile: PlayerProfile?,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onSaveAvatar: (Long, String, AvatarConfig) -> Unit
) {
    val palette = LocalStudioPalette.current
    val initialAvatar = activeProfile?.avatar ?: AvatarConfig.fromPreset(AvatarPreset.LEO)
    var draftAvatar by remember(activeProfile?.id) { mutableStateOf(initialAvatar) }
    var draftName by remember(activeProfile?.id) { mutableStateOf(activeProfile?.name ?: "GamerPro") }
    var activeTab by remember { mutableIntStateOf(0) } // 0=Presets, 1=Hair, 2=Glasses, 3=Hat, 4=Outfit, 5=Acc

    val tabTitles = listOf(
        strings.presetAvatarLabel,
        strings.hairStyleLabel,
        strings.glassesLabel,
        strings.hatLabel,
        strings.outfitLabel,
        strings.accessoryLabel
    )

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.avatarCustomTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 580.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Live 3D Character Stage
                        Neon3DPanel(
                            borderColor = PedestalColors[draftAvatar.pedestalColorIndex.coerceIn(0, PedestalColors.lastIndex)],
                            pulseGlow = true,
                            contentPadding = PaddingValues(10.dp),
                            modifier = Modifier.weight(0.95f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Cartoon3DRiggedCharacter(
                                    avatar = draftAvatar,
                                    size = 175.dp,
                                    pose = if (activeTab == 5) CharacterPose.THUMBS_UP else CharacterPose.IDLE,
                                    showPedestal = true
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                // 8 Color Swatches
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                ) {
                                    CharacterColorSwatches.forEachIndexed { idx, col ->
                                        val isPicked = draftAvatar.colorIndex == idx
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(col)
                                                .border(
                                                    width = if (isPicked) 2.5.dp else 1.dp,
                                                    color = if (isPicked) Color.White else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .testTag("avatar_color_$idx")
                                                .clickable {
                                                    draftAvatar = draftAvatar.copy(colorIndex = idx, pedestalColorIndex = idx)
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        // Right Feature Selector Menu
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            tabTitles.forEachIndexed { idx, label ->
                                val selected = activeTab == idx
                                Neon3DButton(
                                    onClick = { activeTab = idx },
                                    style = if (selected) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.BLUE_GLOSSY,
                                    vibrationEnabled = vibrationEnabled,
                                    pulseGlow = false,
                                    height = 42.dp,
                                    testTag = "avatar_tab_$idx",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color(0xFF1E1300) else Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Options Strip for active tab
                    Neon3DPanel(
                        borderColor = palette.cyan,
                        contentPadding = PaddingValues(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = draftName,
                                onValueChange = { draftName = it.take(18) },
                                label = { Text(strings.playerNameHint) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (activeTab) {
                                    0 -> items(AvatarPreset.entries) { preset ->
                                        val sel = draftAvatar.preset == preset
                                        Neon3DButton(
                                            onClick = { draftAvatar = AvatarConfig.fromPreset(preset) },
                                            style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                            vibrationEnabled = vibrationEnabled,
                                            pulseGlow = false,
                                            height = 44.dp
                                        ) {
                                            Text(
                                                preset.displayName,
                                                color = if (sel) Color(0xFF1E1300) else Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    1 -> items(HairStyle.entries) { h ->
                                        val sel = draftAvatar.hairStyle == h
                                        Neon3DButton(
                                            onClick = { draftAvatar = draftAvatar.copy(hairStyle = h) },
                                            style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                            vibrationEnabled = vibrationEnabled,
                                            pulseGlow = false,
                                            height = 44.dp
                                        ) {
                                            Text(strings.hairName(h, language), color = if (sel) Color(0xFF1E1300) else Color.White)
                                        }
                                    }
                                    2 -> items(GlassesStyle.entries) { g ->
                                        val sel = draftAvatar.glasses == g
                                        Neon3DButton(
                                            onClick = { draftAvatar = draftAvatar.copy(glasses = g) },
                                            style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                            vibrationEnabled = vibrationEnabled,
                                            pulseGlow = false,
                                            height = 44.dp
                                        ) {
                                            Text(strings.glassesName(g, language), color = if (sel) Color(0xFF1E1300) else Color.White)
                                        }
                                    }
                                    3 -> items(HatStyle.entries) { hat ->
                                        val sel = draftAvatar.hat == hat
                                        Neon3DButton(
                                            onClick = { draftAvatar = draftAvatar.copy(hat = hat) },
                                            style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                            vibrationEnabled = vibrationEnabled,
                                            pulseGlow = false,
                                            height = 44.dp
                                        ) {
                                            Text(strings.hatName(hat, language), color = if (sel) Color(0xFF1E1300) else Color.White)
                                        }
                                    }
                                    4 -> items(OutfitStyle.entries) { o ->
                                        val sel = draftAvatar.outfit == o
                                        Neon3DButton(
                                            onClick = { draftAvatar = draftAvatar.copy(outfit = o) },
                                            style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                            vibrationEnabled = vibrationEnabled,
                                            pulseGlow = false,
                                            height = 44.dp
                                        ) {
                                            Text(strings.outfitName(o, language), color = if (sel) Color(0xFF1E1300) else Color.White)
                                        }
                                    }
                                    else -> items(AccessoryStyle.entries) { acc ->
                                        val sel = draftAvatar.accessory == acc
                                        Neon3DButton(
                                            onClick = { draftAvatar = draftAvatar.copy(accessory = acc) },
                                            style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                            vibrationEnabled = vibrationEnabled,
                                            pulseGlow = false,
                                            height = 44.dp
                                        ) {
                                            Text(strings.accessoryName(acc, language), color = if (sel) Color(0xFF1E1300) else Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Neon3DButton(
                            onClick = { draftAvatar = AvatarConfig.fromPreset(draftAvatar.preset) },
                            style = NeonButtonStyle.BLUE_GLOSSY,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = false,
                            testTag = "avatar_reset_btn",
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(strings.resetBtn, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        Neon3DButton(
                            onClick = {
                                val targetId = activeProfile?.id ?: 1L
                                onSaveAvatar(targetId, draftName, draftAvatar)
                                onBack()
                            },
                            style = NeonButtonStyle.GREEN_CONFIRM,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = true,
                            testTag = "avatar_save_btn",
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(strings.saveBtn, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 6: CATEGORY SELECTION (10 Categories + 1000+ Words DB)
// ============================================================
@Composable
fun CategorySelectionScreen(
    state: ActiveMatchState,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onToggleCategory: (CategoryId) -> Unit,
    onSelectAll: () -> Unit,
    onUpdateConfig: ((MatchConfig) -> MatchConfig) -> Unit,
    onNext: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val cfg = state.config
    var showWordDbDialog by remember { mutableStateOf(false) }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.categorySelectionTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    Text(
                        text = strings.categoryMixHint,
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.cyan,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 2 rows x 5 columns on wide screens / 2 columns x 5 rows responsive grid
                    val rows = CategoryId.entries.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        rows.forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { cat ->
                                    val selected = cat in cfg.selectedCategories
                                    val accent = CategoryAccentColors[(cat.id - 1).coerceIn(0, CategoryAccentColors.lastIndex)]
                                    Neon3DPanel(
                                        borderColor = if (selected) palette.primaryTop else accent.copy(alpha = 0.5f),
                                        fillColor = if (selected) accent.copy(alpha = 0.35f) else palette.surface,
                                        pulseGlow = selected,
                                        contentPadding = PaddingValues(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("category_card_${cat.id}")
                                            .clickable { onToggleCategory(cat) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(accent.copy(alpha = 0.85f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = categoryIcon(cat),
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                            Text(
                                                text = strings.categoryName(cat, language),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = palette.ink,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (selected) {
                                                Icon(
                                                    imageVector = Icons.Rounded.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = palette.primaryTop,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Difficulty + Select All + Skip Word + Word Database
                    Neon3DPanel(borderColor = palette.cyan, modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Neon3DButton(
                                    onClick = onSelectAll,
                                    style = NeonButtonStyle.BLUE_GLOSSY,
                                    vibrationEnabled = vibrationEnabled,
                                    pulseGlow = false,
                                    height = 44.dp,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(strings.selectAllBtn, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Neon3DButton(
                                    onClick = { showWordDbDialog = true },
                                    style = NeonButtonStyle.PURPLE_MYSTERY,
                                    vibrationEnabled = vibrationEnabled,
                                    pulseGlow = false,
                                    height = 44.dp,
                                    testTag = "category_word_db_btn",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.browseWordsBtn, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DifficultyLevel.entries.forEach { diff ->
                                    val sel = cfg.difficulty == diff
                                    Neon3DButton(
                                        onClick = { onUpdateConfig { it.copy(difficulty = diff) } },
                                        style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                        vibrationEnabled = vibrationEnabled,
                                        pulseGlow = false,
                                        height = 42.dp,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            strings.difficultyName(diff, language),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (sel) Color(0xFF1E1300) else Color.White,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            NeonSwitchRow(
                                label = strings.skipWordToggle,
                                checked = cfg.skipWordEnabled,
                                onCheckedChange = { enabled -> onUpdateConfig { it.copy(skipWordEnabled = enabled) } }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Neon3DButton(
                        onClick = onNext,
                        style = NeonButtonStyle.BLUE_GLOSSY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 58.dp,
                        testTag = "category_next_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.nextBtn, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }

    if (showWordDbDialog) {
        var filterCat by remember { mutableStateOf(CategoryId.CINEMA_ACTORS) }
        val previewWords = remember(filterCat) { WordBank.allWords.filter { it.category == filterCat } }
        AlertDialog(
            onDismissRequest = { showWordDbDialog = false },
            containerColor = palette.surface,
            titleContentColor = palette.ink,
            textContentColor = palette.ink,
            title = { Text("${strings.localWordsTitle} (${WordBank.allWords.size})", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.heightIn(max = 380.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CategoryId.entries) { cat ->
                            val active = filterCat == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (active) palette.primaryTop else palette.card)
                                    .clickable { filterCat = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    strings.categoryName(cat, language),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (active) Color(0xFF1E1300) else palette.ink,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(previewWords) { w ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(palette.bgMid)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(w.wordEn, fontWeight = FontWeight.Bold, color = palette.ink)
                                Text(w.wordHi, fontWeight = FontWeight.Medium, color = palette.cyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWordDbDialog = false }) {
                    Text(strings.backBtn, color = palette.primaryTop, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

private fun categoryIcon(cat: CategoryId): ImageVector = when (cat) {
    CategoryId.CINEMA_ACTORS -> Icons.Rounded.Movie
    CategoryId.FOOD -> Icons.Rounded.Fastfood
    CategoryId.SWEET_DISHES -> Icons.Rounded.Icecream
    CategoryId.CRICKET_PLAYERS -> Icons.Rounded.SportsCricket
    CategoryId.ANIMALS -> Icons.Rounded.Pets
    CategoryId.COUNTRIES -> Icons.Rounded.Public
    CategoryId.VEHICLES -> Icons.Rounded.DirectionsCar
    CategoryId.GAMES -> Icons.Rounded.SportsEsports
    CategoryId.TECHNOLOGY -> Icons.Rounded.Memory
    CategoryId.SUPERHEROES -> Icons.Rounded.FlashOn
}

// ============================================================
// SCREEN 7: GAME MODE SELECTION
// ============================================================
@Composable
fun GameModeSelectionScreen(
    state: ActiveMatchState,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onSelectMode: (GameModeType) -> Unit,
    onUpdateConfig: ((MatchConfig) -> MatchConfig) -> Unit,
    onNext: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val cfg = state.config
    val modes = listOf(
        Triple(GameModeType.CLASSIC, strings.modeClassicTitle, strings.modeClassicSub),
        Triple(GameModeType.PRACTICE, strings.modePracticeTitle, strings.modePracticeSub),
        Triple(GameModeType.SUDDEN_DEATH, strings.modeSuddenDeathTitle, strings.modeSuddenDeathSub),
        Triple(GameModeType.CHALLENGE, strings.modeChallengeTitle, strings.modeChallengeSub),
        Triple(GameModeType.CUSTOM, strings.modeCustomTitle, strings.modeCustomSub)
    )

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.gameModeTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    modes.forEach { (modeType, title, sub) ->
                        val selected = cfg.modeType == modeType
                        Neon3DPanel(
                            borderColor = if (selected) palette.primaryTop else palette.cardBorder,
                            fillColor = if (selected) palette.secondary else palette.surface,
                            pulseGlow = selected,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mode_${modeType.id}")
                                .clickable { onSelectMode(modeType) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) palette.primaryTop else palette.card),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (modeType == GameModeType.CHALLENGE || modeType == GameModeType.CLASSIC) {
                                            Icons.Rounded.EmojiEvents
                                        } else {
                                            Icons.Rounded.SportsEsports
                                        },
                                        contentDescription = null,
                                        tint = if (selected) Color(0xFF1E1300) else palette.cyan
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = palette.ink)
                                    Text(sub, style = MaterialTheme.typography.labelMedium, color = palette.mutedInk)
                                }
                                if (selected) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = "Selected", tint = palette.primaryTop)
                                }
                            }
                        }
                    }

                    // Timers, Hints, Missions & Challenge Wheel controls
                    Neon3DPanel(borderColor = palette.cyan, modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(strings.discussionTimerLabel, style = MaterialTheme.typography.labelLarge, color = palette.cyan)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf(30, 45, 60, 90).forEach { sec ->
                                    val sel = cfg.discussionSeconds == sec
                                    Neon3DButton(
                                        onClick = { onUpdateConfig { it.copy(discussionSeconds = sec) } },
                                        style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                        vibrationEnabled = vibrationEnabled,
                                        pulseGlow = false,
                                        height = 40.dp,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("${sec}s", color = if (sel) Color(0xFF1E1300) else Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text(strings.votingTimerLabel, style = MaterialTheme.typography.labelLarge, color = palette.cyan)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf(15, 30, 45).forEach { sec ->
                                    val sel = cfg.votingSeconds == sec
                                    Neon3DButton(
                                        onClick = { onUpdateConfig { it.copy(votingSeconds = sec) } },
                                        style = if (sel) NeonButtonStyle.GOLD_PRIMARY else NeonButtonStyle.SLATE_MUTED,
                                        vibrationEnabled = vibrationEnabled,
                                        pulseGlow = false,
                                        height = 40.dp,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("${sec}s", color = if (sel) Color(0xFF1E1300) else Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            NeonSwitchRow(
                                label = "${strings.imposterHintsLabel} (${cfg.maxHintsPerRound})",
                                checked = cfg.hintsEnabled,
                                onCheckedChange = { on -> onUpdateConfig { it.copy(hintsEnabled = on, maxHintsPerRound = if (on) 1 else 0) } }
                            )
                            NeonSwitchRow(
                                label = strings.secretMissionsLabel,
                                checked = cfg.secretMissionsEnabled,
                                onCheckedChange = { on -> onUpdateConfig { it.copy(secretMissionsEnabled = on) } }
                            )
                            NeonSwitchRow(
                                label = strings.challengeWheelLabel,
                                checked = cfg.challengeWheelEnabled,
                                onCheckedChange = { on -> onUpdateConfig { it.copy(challengeWheelEnabled = on) } }
                            )
                        }
                    }

                    Neon3DButton(
                        onClick = onNext,
                        style = NeonButtonStyle.BLUE_GLOSSY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 58.dp,
                        testTag = "mode_next_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.nextBtn, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 8: GAME RULES
// ============================================================
@Composable
fun GameRulesScreen(
    state: ActiveMatchState,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onStartLobby: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val rules = listOf(strings.rule1, strings.rule2, strings.rule3, strings.rule4)
    var revealedCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        for (i in 1..4) {
            delay(130)
            revealedCount = i
        }
    }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.gameRulesTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Neon3DPanel(
                            borderColor = palette.cyan,
                            modifier = Modifier.weight(1.25f)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                rules.forEachIndexed { idx, ruleText ->
                                    AnimatedVisibility(
                                        visible = idx < revealedCount,
                                        enter = fadeIn() + slideInHorizontally { -30 }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(palette.success),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Text(
                                                text = ruleText,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = palette.ink
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Cartoon3DRiggedCharacter(
                            avatar = AvatarConfig.fromPreset(AvatarPreset.LEO),
                            size = 152.dp,
                            pose = CharacterPose.POINTING,
                            showPedestal = true
                        )
                    }

                    Neon3DPanel(borderColor = palette.tertiary, modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${strings.modeName(state.config.modeType, language)} • ${state.config.playerCount} ${strings.playersCountFormat} • ${state.config.imposterCount} ${strings.impostersLabel}",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.primaryTop,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${strings.discussionTimerLabel}: ${state.config.discussionSeconds}s • ${strings.votingTimerLabel}: ${state.config.votingSeconds}s • ${strings.difficultyName(state.config.difficulty, language)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.mutedInk
                            )
                        }
                    }

                    Neon3DButton(
                        onClick = onStartLobby,
                        style = NeonButtonStyle.GOLD_PRIMARY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 60.dp,
                        testTag = "rules_start_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.startGameBtn, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E1300))
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 9: GAME LOBBY
// ============================================================
@Composable
fun GameLobbyScreen(
    state: ActiveMatchState,
    previewPlayers: List<PlayerSlot>,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onStartRound: () -> Unit
) {
    val palette = LocalStudioPalette.current
    var showInlineWheel by remember { mutableStateOf(false) }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.gameLobbyTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 600.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${strings.playersCountFormat}: ${previewPlayers.size}/10",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = palette.cyan
                        )
                        Text(
                            text = "${strings.roomBadgePrefix}: ${state.config.roomCode}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = palette.primaryTop
                        )
                    }

                    // 3D Stage showing all active lobby characters on neon pedestals
                    Neon3DPanel(
                        borderColor = palette.cyan,
                        pulseGlow = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(previewPlayers, key = { _, p -> p.slotId }) { index, slot ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(88.dp)
                                ) {
                                    Cartoon3DRiggedCharacter(
                                        avatar = slot.avatar,
                                        size = 112.dp,
                                        pose = if (index % 2 == 0) CharacterPose.IDLE else CharacterPose.THUMBS_UP,
                                        showPedestal = true,
                                        seedOffset = index
                                    )
                                    Text(
                                        text = slot.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.ink,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (slot.isHost) {
                                        Text(
                                            text = strings.hostTag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = palette.primaryTop
                                        )
                                    } else if (slot.isBot) {
                                        Text(
                                            text = strings.botTag,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = palette.tertiary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.config.challengeWheelEnabled && showInlineWheel) {
                        Neon3DPanel(borderColor = palette.tertiary, modifier = Modifier.fillMaxWidth()) {
                            ChallengeWheel3D(
                                language = language,
                                strings = strings,
                                vibrationEnabled = vibrationEnabled,
                                onModifierLocked = {
                                    showInlineWheel = false
                                    onStartRound()
                                }
                            )
                        }
                    }

                    Neon3DButton(
                        onClick = onStartRound,
                        style = NeonButtonStyle.GOLD_PRIMARY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 60.dp,
                        testTag = "lobby_start_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.startGameBtn, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E1300))
                    }
                }
            }
        }
    }
}
