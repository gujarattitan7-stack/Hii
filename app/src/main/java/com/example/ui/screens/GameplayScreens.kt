package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.HowToVote
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ActiveMatchState
import com.example.domain.AppLanguage
import com.example.domain.ChallengeModifier
import com.example.domain.CharacterPose
import com.example.domain.GamePhase
import com.example.domain.WinnerSide
import com.example.localization.AppStrings
import com.example.ui.components.AnimatedVoteBar3D
import com.example.ui.components.Cartoon3DRiggedCharacter
import com.example.ui.components.ChallengeWheel3D
import com.example.ui.components.ConfettiBurstOverlay
import com.example.ui.components.Neon3DButton
import com.example.ui.components.Neon3DPanel
import com.example.ui.components.NeonButtonStyle
import com.example.ui.components.NeonCyberBackground
import com.example.ui.components.NeonTopHeaderBar
import com.example.ui.components.SecretWordFlipCard3D
import com.example.ui.theme.LocalStudioPalette
import com.example.ui.theme.PedestalColors
import java.util.Locale

@Composable
fun ChallengeWheelOverlayScreen(
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onModifierLocked: (ChallengeModifier) -> Unit
) {
    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.wheelTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ChallengeWheel3D(
                    language = language,
                    strings = strings,
                    vibrationEnabled = vibrationEnabled,
                    onModifierLocked = onModifierLocked,
                    modifier = Modifier.widthIn(max = 520.dp)
                )
            }
        }
    }
}

// ============================================================
// SCREENS 10 & 11: SECRET CARD & SECRET WORD REVEAL
// ============================================================
@Composable
fun SecretCardAndRevealScreen(
    state: ActiveMatchState,
    phase: GamePhase.SecretCardPass,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onDismissPassCurtain: () -> Unit,
    onFlipOpen: () -> Unit,
    onHideCard: () -> Unit,
    onNextPlayer: () -> Unit,
    onSkipWord: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val humanPlayers = remember(state.players) { state.players.filter { !it.isBot } }
    val currentPlayer = humanPlayers.getOrNull(phase.currentHumanIndex) ?: state.players.firstOrNull()
    val word = state.currentWord
    val isLastHuman = phase.currentHumanIndex >= humanPlayers.lastIndex

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = if (phase.isCardFlipped) strings.secretWordRevealTitle else strings.secretCardTitle,
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (phase.isPassPhoneCurtain && currentPlayer != null) {
                        Neon3DPanel(
                            borderColor = palette.primaryTop,
                            pulseGlow = true,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = palette.primaryTop,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = strings.passPhoneTitle,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = palette.ink,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${strings.playerPrefix}: ${currentPlayer.name}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = palette.cyan
                                )
                                Text(
                                    text = strings.passPhoneSub,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = palette.mutedInk,
                                    textAlign = TextAlign.Center
                                )
                                Cartoon3DRiggedCharacter(
                                    avatar = currentPlayer.avatar,
                                    size = 135.dp,
                                    pose = CharacterPose.IDLE,
                                    showPedestal = true
                                )
                                Neon3DButton(
                                    onClick = onDismissPassCurtain,
                                    style = NeonButtonStyle.GOLD_PRIMARY,
                                    vibrationEnabled = vibrationEnabled,
                                    pulseGlow = true,
                                    testTag = "secret_pass_ready_btn",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${strings.playerPrefix}: ${currentPlayer.name} — ${strings.continueBtn}",
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1E1300)
                                    )
                                }
                            }
                        }
                    } else if (currentPlayer != null && word != null) {
                        // Player pill & 3D Character alongside Secret Card
                        Neon3DPanel(
                            borderColor = palette.cyan,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${strings.playerPrefix}: ${currentPlayer.name}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = palette.ink
                                    )
                                    Text(
                                        text = "${phase.currentHumanIndex + 1} / ${humanPlayers.size.coerceAtLeast(1)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = palette.cyan
                                    )
                                }
                                Cartoon3DRiggedCharacter(
                                    avatar = currentPlayer.avatar,
                                    size = 105.dp,
                                    pose = when {
                                        !phase.isCardFlipped -> CharacterPose.POINTING
                                        currentPlayer.isImposter -> CharacterPose.SURPRISE
                                        else -> CharacterPose.THUMBS_UP
                                    },
                                    showPedestal = true,
                                    isImposterAura = phase.isCardFlipped && currentPlayer.isImposter
                                )
                            }
                        }

                        // Full 3D Flip Card (Screen 10 '?' back -> Screen 11 Secret Word / Imposter front)
                        val wordText = if (language == AppLanguage.HINDI) word.wordHi else word.wordEn
                        val catText = strings.categoryName(word.category, language)
                        val hintText = if (state.config.hintsEnabled && state.activeChallenge?.extraHintBlocked != true) {
                            if (language == AppLanguage.HINDI) word.hintHi else word.hintEn
                        } else null

                        SecretWordFlipCard3D(
                            isImposter = currentPlayer.isImposter,
                            wordText = wordText,
                            categoryText = catText,
                            hintText = hintText,
                            mission = currentPlayer.secretMission,
                            language = language,
                            strings = strings,
                            isFlipped = phase.isCardFlipped
                        )

                        // Action Buttons
                        if (!phase.isCardFlipped) {
                            Neon3DButton(
                                onClick = onFlipOpen,
                                style = NeonButtonStyle.BLUE_GLOSSY,
                                vibrationEnabled = vibrationEnabled,
                                pulseGlow = true,
                                height = 58.dp,
                                testTag = "secret_reveal_btn",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.Visibility, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.revealCardBtn, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        } else {
                            Neon3DButton(
                                onClick = onHideCard,
                                style = NeonButtonStyle.GOLD_PRIMARY,
                                vibrationEnabled = vibrationEnabled,
                                pulseGlow = false,
                                height = 52.dp,
                                testTag = "secret_hide_btn",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.VisibilityOff, contentDescription = null, tint = Color(0xFF1E1300))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.hideCardBtn, fontWeight = FontWeight.Black, color = Color(0xFF1E1300))
                            }
                        }

                        if (phase.cardRevealedOnce) {
                            Neon3DButton(
                                onClick = onNextPlayer,
                                style = NeonButtonStyle.GREEN_CONFIRM,
                                vibrationEnabled = vibrationEnabled,
                                pulseGlow = true,
                                height = 58.dp,
                                testTag = "secret_next_btn",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isLastHuman) strings.startDiscussionBtn else strings.nextPlayerBtn,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        if (state.config.skipWordEnabled && state.skipsRemaining > 0) {
                            Neon3DButton(
                                onClick = onSkipWord,
                                style = NeonButtonStyle.SLATE_MUTED,
                                vibrationEnabled = vibrationEnabled,
                                pulseGlow = false,
                                height = 46.dp,
                                testTag = "secret_skip_word_btn",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.SkipNext, contentDescription = null, tint = palette.primaryTop)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${strings.skipWordBtn} (${state.skipsRemaining})", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 12: DISCUSSION PHASE (3D Table Scene + Red Timer)
// ============================================================
@Composable
fun DiscussionPhaseScreen(
    state: ActiveMatchState,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onTogglePause: () -> Unit,
    onUseHint: () -> Unit,
    onMoreBotClues: () -> Unit,
    onStartVoting: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val mins = state.secondsRemaining / 60
    val secs = state.secondsRemaining % 60
    val formattedTimer = String.format(Locale.US, "%02d:%02d", mins, secs)
    val isUrgent = state.secondsRemaining <= 10

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.discussionTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled,
                trailingContent = {
                    // Glowing Red Countdown Timer Badge (Screen 12)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isUrgent) palette.danger else Color(0xFF450A0A))
                            .border(2.dp, palette.danger, RoundedCornerShape(50))
                            .testTag("discussion_timer_pill")
                            .clickable { onTogglePause() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = if (state.isTimerRunning) Icons.Rounded.Timer else Icons.Rounded.Pause,
                                contentDescription = "Timer",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = formattedTimer,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 3D Conference Room Table with Seated Talking Characters
                    Neon3DPanel(
                        borderColor = palette.cyan,
                        pulseGlow = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(172.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Glowing Cyber Table Surface
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val tableW = size.width * 0.72f
                                    val tableH = size.height * 0.34f
                                    val left = (size.width - tableW) / 2f
                                    val top = size.height * 0.60f
                                    drawOval(
                                        brush = Brush.radialGradient(
                                            listOf(Color(0xFF1D4ED8), Color(0xFF0A193D))
                                        ),
                                        topLeft = Offset(left, top),
                                        size = Size(tableW, tableH)
                                    )
                                    drawOval(
                                        color = palette.cyan,
                                        topLeft = Offset(left, top),
                                        size = Size(tableW, tableH),
                                        style = Stroke(width = 4f)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 18.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    state.players.filter { !it.isEliminated }.take(5).forEachIndexed { idx, slot ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Cartoon3DRiggedCharacter(
                                                avatar = slot.avatar,
                                                size = 96.dp,
                                                pose = if (idx % 2 == 0) CharacterPose.TALKING else CharacterPose.SITTING,
                                                showPedestal = false,
                                                seedOffset = idx
                                            )
                                            Text(
                                                text = slot.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = palette.ink,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.primaryTop.copy(alpha = 0.18f))
                                    .border(1.dp, palette.primaryTop.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = strings.discussPrompt,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = palette.primaryTop,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Active Challenge Banner if active
                    state.activeChallenge?.let { ch ->
                        Neon3DPanel(borderColor = palette.tertiary, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = palette.primaryTop)
                                Text(
                                    text = if (language == AppLanguage.HINDI) "${ch.titleHi}: ${ch.descHi}" else "${ch.titleEn}: ${ch.descEn}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.ink
                                )
                            }
                        }
                    }

                    // Limited Hint Card
                    if (state.config.hintsEnabled && state.activeChallenge?.extraHintBlocked != true) {
                        val word = state.currentWord
                        if (state.hintRevealedForCrew && word != null) {
                            Neon3DPanel(borderColor = palette.primaryTop, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "${strings.hintPrefix}: ${if (language == AppLanguage.HINDI) word.hintHi else word.hintEn}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primaryTop
                                )
                            }
                        } else if (state.hintsRemaining > 0) {
                            Neon3DButton(
                                onClick = onUseHint,
                                style = NeonButtonStyle.PURPLE_MYSTERY,
                                vibrationEnabled = vibrationEnabled,
                                pulseGlow = false,
                                height = 46.dp,
                                testTag = "discussion_hint_btn",
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = palette.primaryTop)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${strings.useHintBtn} (${state.hintsRemaining})", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    // Smart Bot Clues Feed (in Practice / mixed play)
                    if (state.botClues.isNotEmpty()) {
                        Neon3DPanel(borderColor = palette.cyan, modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(strings.botCluesTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = palette.cyan)
                                    Neon3DButton(
                                        onClick = onMoreBotClues,
                                        style = NeonButtonStyle.BLUE_GLOSSY,
                                        vibrationEnabled = vibrationEnabled,
                                        pulseGlow = false,
                                        height = 36.dp
                                    ) {
                                        Text(strings.moreCluesBtn, style = MaterialTheme.typography.labelMedium, color = Color.White)
                                    }
                                }
                                state.botClues.takeLast(4).forEach { clue ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(palette.bgMid)
                                            .padding(8.dp)
                                    ) {
                                        Cartoon3DRiggedCharacter(
                                            avatar = clue.avatar,
                                            size = 38.dp,
                                            isPortraitOnly = true,
                                            pose = CharacterPose.TALKING
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(clue.playerName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = palette.primaryTop)
                                            Text(
                                                text = "\"${if (language == AppLanguage.HINDI) clue.clueTextHi else clue.clueTextEn}\"",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = palette.ink
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Neon3DButton(
                        onClick = onStartVoting,
                        style = NeonButtonStyle.GOLD_PRIMARY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 60.dp,
                        testTag = "discussion_vote_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.HowToVote, contentDescription = null, tint = Color(0xFF1E1300))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.startVotingBtn, fontSize = 19.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E1300))
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 13: VOTING SCREEN (No Self-Vote, No Duplicate Vote)
// ============================================================
@Composable
fun VotingScreen(
    state: ActiveMatchState,
    isSuddenDeath: Boolean,
    tiedCandidateIds: List<String>,
    currentVoterIndex: Int,
    votesCast: Map<String, String>,
    isPrivacyShieldUp: Boolean,
    secondsRemaining: Int,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onUnlockBallot: () -> Unit,
    onCastVote: (String) -> Unit
) {
    val palette = LocalStudioPalette.current
    val activeHumans = remember(state.players) { state.players.filter { !it.isBot && !it.isEliminated } }
    val currentVoter = activeHumans.getOrNull(currentVoterIndex)
    val allCandidates = remember(state.players, isSuddenDeath, tiedCandidateIds, currentVoter?.slotId) {
        val pool = if (isSuddenDeath && tiedCandidateIds.isNotEmpty()) {
            state.players.filter { it.slotId in tiedCandidateIds && !it.isEliminated }
        } else {
            state.players.filter { !it.isEliminated }
        }
        // Strictly block self-voting!
        pool.filter { it.slotId != currentVoter?.slotId }
            .ifEmpty { pool }
    }
    var selectedSuspectId by remember(currentVoterIndex, isSuddenDeath) { mutableStateOf<String?>(null) }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = if (isSuddenDeath) strings.modeSuddenDeathTitle else strings.votingTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled,
                animateButtons = false,
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(palette.danger)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("${secondsRemaining}s", fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isPrivacyShieldUp && currentVoter != null) {
                        Neon3DPanel(
                            borderColor = palette.primaryTop,
                            pulseGlow = false,
                            animateGlow = false,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.Lock, contentDescription = null, tint = palette.primaryTop, modifier = Modifier.size(44.dp))
                                Text(
                                    text = "${strings.passBallotTo}: ${currentVoter.name}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = palette.ink,
                                    textAlign = TextAlign.Center
                                )
                                Cartoon3DRiggedCharacter(
                                    avatar = currentVoter.avatar,
                                    size = 128.dp,
                                    pose = CharacterPose.THINKING,
                                    showPedestal = true
                                )
                                Neon3DButton(
                                    onClick = onUnlockBallot,
                                    style = NeonButtonStyle.GOLD_PRIMARY,
                                    vibrationEnabled = vibrationEnabled,
                                    pulseGlow = false,
                                    animateButton = false,
                                    testTag = "vote_unlock_btn",
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(strings.unlockBallotBtn, fontWeight = FontWeight.Black, color = Color(0xFF1E1300))
                                }
                            }
                        }
                    } else if (currentVoter != null) {
                        Text(
                            text = "${strings.selectSuspectLabel} (${currentVoter.name})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = palette.cyan,
                            textAlign = TextAlign.Center
                        )

                        // 2-column grid of Suspect 3D Cards (static buttons during voting)
                        val rows = allCandidates.chunked(2)
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            rows.forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEachIndexed { idx, candidate ->
                                        val isPicked = selectedSuspectId == candidate.slotId
                                        Neon3DPanel(
                                            borderColor = if (isPicked) palette.danger else palette.cardBorder,
                                            fillColor = if (isPicked) palette.danger.copy(alpha = 0.25f) else palette.surface,
                                            pulseGlow = false,
                                            animateGlow = false,
                                            contentPadding = PaddingValues(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("vote_candidate_${candidate.slotId}")
                                                .clickable(
                                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                                    indication = null
                                                ) { selectedSuspectId = candidate.slotId }
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Cartoon3DRiggedCharacter(
                                                    avatar = candidate.avatar,
                                                    size = 90.dp,
                                                    isPortraitOnly = true,
                                                    pose = if (isPicked) CharacterPose.SURPRISE else CharacterPose.IDLE,
                                                    seedOffset = idx
                                                )
                                                Text(
                                                    text = candidate.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = palette.ink,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        val alreadyVoted = votesCast.containsKey(currentVoter.slotId)
                        Neon3DButton(
                            onClick = { selectedSuspectId?.let(onCastVote) },
                            enabled = selectedSuspectId != null && !alreadyVoted,
                            style = NeonButtonStyle.RED_DANGER,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = false,
                            animateButton = false,
                            height = 60.dp,
                            testTag = "vote_confirm_btn",
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(strings.voteBtn, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        Text(
                            text = strings.onlyOnceNote,
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.mutedInk
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 14: VOTING RESULTS (Animated 3D Vote Bars)
// ============================================================
@Composable
fun VotingResultsScreen(
    state: ActiveMatchState,
    results: GamePhase.VotingResults,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onContinue: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val maxVotes = (results.voteCounts.values.maxOrNull() ?: 1).coerceAtLeast(1)
    val sortedSlots = remember(state.players, results.voteCounts) {
        state.players.sortedByDescending { results.voteCounts[it.slotId] ?: 0 }
    }
    val eliminated = state.players.firstOrNull { it.slotId == results.eliminatedPlayerId }

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.votingResultsTitle,
                onBack = onBack,
                onHome = onHome,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Neon3DPanel(borderColor = palette.cyan, pulseGlow = true, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            sortedSlots.forEachIndexed { idx, slot ->
                                val votes = results.voteCounts[slot.slotId] ?: 0
                                val isTop = votes == maxVotes && votes > 0
                                val barColor = PedestalColors[idx % PedestalColors.size]
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "$votes",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isTop) palette.primaryTop else palette.ink
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    AnimatedVoteBar3D(
                                        voteCount = votes,
                                        maxVotes = maxVotes,
                                        accentColor = barColor,
                                        isHighest = isTop
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Cartoon3DRiggedCharacter(
                                        avatar = slot.avatar,
                                        size = 56.dp,
                                        isPortraitOnly = true,
                                        pose = if (slot.slotId == results.eliminatedPlayerId) CharacterPose.DEFEAT else CharacterPose.IDLE,
                                        seedOffset = idx
                                    )
                                    Text(
                                        text = slot.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.ink,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    if (results.isTie) {
                        Neon3DPanel(borderColor = palette.danger, pulseGlow = true, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Rounded.Warning, contentDescription = null, tint = palette.danger, modifier = Modifier.size(30.dp))
                                Text(
                                    text = strings.tieBreakerAlert,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = palette.primaryTop
                                )
                            }
                        }
                    } else if (eliminated != null) {
                        Neon3DPanel(borderColor = palette.danger, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${strings.eliminatedPrefix}: ${eliminated.name}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = palette.danger,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Neon3DButton(
                        onClick = onContinue,
                        style = NeonButtonStyle.PURPLE_MYSTERY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 58.dp,
                        testTag = "results_continue_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.continueBtn, fontSize = 19.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 15: IMPOSTER REVEAL
// ============================================================
@Composable
fun ImposterRevealScreen(
    state: ActiveMatchState,
    reveal: GamePhase.ImposterReveal,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onContinue: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val imposters = state.players.filter { it.isImposter }
    val leadImposter = imposters.firstOrNull()
    val word = state.currentWord
    val wordDisplay = if (word != null) {
        if (language == AppLanguage.HINDI) word.wordHi else word.wordEn
    } else ""

    NeonCyberBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.imposterRevealTitle,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Neon3DPanel(
                        borderColor = palette.danger,
                        pulseGlow = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            leadImposter?.let { imp ->
                                Cartoon3DRiggedCharacter(
                                    avatar = imp.avatar,
                                    size = 148.dp,
                                    pose = if (reveal.winnerSide == WinnerSide.IMPOSTER) CharacterPose.VICTORY else CharacterPose.SURPRISE,
                                    showPedestal = true,
                                    isImposterAura = true
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = imposters.joinToString(", ") { it.name },
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = palette.danger
                                )
                                Text(
                                    text = strings.wasTheImposter,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = palette.ink
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(palette.primaryTop.copy(alpha = 0.2f))
                                        .border(1.dp, palette.primaryTop, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${strings.secretWordPrefix}: $wordDisplay",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.primaryTop
                                    )
                                }
                            }
                        }
                    }

                    Neon3DButton(
                        onClick = onContinue,
                        style = NeonButtonStyle.PURPLE_MYSTERY,
                        vibrationEnabled = vibrationEnabled,
                        pulseGlow = true,
                        height = 58.dp,
                        testTag = "reveal_continue_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.continueBtn, fontSize = 19.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}

// ============================================================
// SCREEN 16: WINNER SCREEN (Confetti + Animated XP + Cheering Crew)
// ============================================================
@Composable
fun WinnerScreen(
    state: ActiveMatchState,
    winner: GamePhase.WinnerCelebration,
    language: AppLanguage,
    strings: AppStrings,
    vibrationEnabled: Boolean,
    onToggleMissionCompleted: (String) -> Unit,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val palette = LocalStudioPalette.current
    val animatedXp by animateIntAsState(
        targetValue = winner.xpEarnedTotal,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "xp_counter"
    )
    val winningTeam = remember(state.players, winner.winnerSide) {
        state.players.filter {
            (winner.winnerSide == WinnerSide.CREW && !it.isImposter) ||
                (winner.winnerSide == WinnerSide.IMPOSTER && it.isImposter)
        }.ifEmpty { state.players }
    }

    NeonCyberBackground {
        ConfettiBurstOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NeonTopHeaderBar(
                title = strings.winnerScreenTitle,
                vibrationEnabled = vibrationEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 580.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Neon3DPanel(
                        borderColor = palette.primaryTop,
                        pulseGlow = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = palette.primaryTop,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = if (winner.winnerSide == WinnerSide.CREW) strings.crewWinsHeadline else strings.imposterWinsHeadline,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = palette.primaryTop,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = strings.wellPlayedSub,
                                style = MaterialTheme.typography.titleMedium,
                                color = palette.cyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "+$animatedXp XP",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = palette.success
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                winningTeam.take(4).forEachIndexed { idx, slot ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Cartoon3DRiggedCharacter(
                                            avatar = slot.avatar,
                                            size = 104.dp,
                                            pose = CharacterPose.VICTORY,
                                            showPedestal = true,
                                            seedOffset = idx
                                        )
                                        Text(
                                            text = slot.name,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.ink
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Secret Missions Bonus Claim
                    val missionHumans = state.players.filter { !it.isBot && it.secretMission != null }
                    if (missionHumans.isNotEmpty()) {
                        Neon3DPanel(borderColor = palette.tertiary, modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = strings.secretMissionsLabel,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primaryTop
                                )
                                missionHumans.forEach { slot ->
                                    val m = slot.secretMission ?: return@forEach
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onToggleMissionCompleted(slot.slotId) }
                                    ) {
                                        Checkbox(
                                            checked = slot.missionCompleted,
                                            onCheckedChange = { onToggleMissionCompleted(slot.slotId) },
                                            colors = CheckboxDefaults.colors(checkedColor = palette.primaryTop)
                                        )
                                        Text(
                                            text = "${slot.name}: ${if (language == AppLanguage.HINDI) m.titleHi else m.titleEn} (+${m.bonusXp} XP)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = palette.ink
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (winner.newlyUnlockedAchievementIds.isNotEmpty() || winner.leveledUpProfileNames.isNotEmpty()) {
                        Neon3DPanel(borderColor = palette.success, modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                winner.leveledUpProfileNames.forEach { name ->
                                    Text("★ $name LEVEL UP!", color = palette.primaryTop, fontWeight = FontWeight.Black)
                                }
                                winner.newlyUnlockedAchievementIds.forEach { ach ->
                                    Text("🏆 ${strings.unlockedBadge}: $ach", color = palette.success, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Neon3DButton(
                            onClick = onPlayAgain,
                            style = NeonButtonStyle.GOLD_PRIMARY,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = true,
                            testTag = "winner_play_again_btn",
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color(0xFF1E1300))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.playAgainBtn, fontWeight = FontWeight.Black, color = Color(0xFF1E1300))
                        }

                        Neon3DButton(
                            onClick = onMainMenu,
                            style = NeonButtonStyle.BLUE_GLOSSY,
                            vibrationEnabled = vibrationEnabled,
                            pulseGlow = false,
                            testTag = "winner_main_menu_btn",
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(strings.mainMenuBtn, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
