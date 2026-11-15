package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audio.SynthAudioEngine
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.repository.GameRepository
import com.example.domain.GamePhase
import com.example.localization.stringsFor
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AvatarCustomizationScreen
import com.example.ui.screens.CategorySelectionScreen
import com.example.ui.screens.ChallengeWheelOverlayScreen
import com.example.ui.screens.DiscussionPhaseScreen
import com.example.ui.screens.GameLobbyScreen
import com.example.ui.screens.GameModeSelectionScreen
import com.example.ui.screens.GameRulesScreen
import com.example.ui.screens.HowToPlayScreen
import com.example.ui.screens.ImposterRevealScreen
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.PlayerProfileScreen
import com.example.ui.screens.PlayerSelectionScreen
import com.example.ui.screens.SecretCardAndRevealScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VotingResultsScreen
import com.example.ui.screens.VotingScreen
import com.example.ui.screens.WinnerScreen
import com.example.ui.theme.ImposterGTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.ProfileStatsViewModel

const val ROUTE_SPLASH = "splash"
const val ROUTE_MAIN_MENU = "main_menu"
const val ROUTE_LANGUAGE = "language"
const val ROUTE_PLAYER_SELECTION = "player_selection"
const val ROUTE_AVATAR_CUSTOMIZATION = "avatar_customization"
const val ROUTE_CATEGORY_SELECTION = "category_selection"
const val ROUTE_GAME_MODE = "game_mode"
const val ROUTE_GAME_RULES = "game_rules"
const val ROUTE_GAME_LOBBY = "game_lobby"
const val ROUTE_GAME_STAGE = "game_stage"
const val ROUTE_PLAYER_PROFILE = "player_profile"
const val ROUTE_ACHIEVEMENTS = "achievements"
const val ROUTE_SETTINGS = "settings"
const val ROUTE_HOW_TO_PLAY = "how_to_play"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GameRepository(
            db = AppDatabase.getInstance(applicationContext),
            prefs = AppPreferences(applicationContext)
        )
        setContent {
            ImposterGApp(repository = repository)
        }
    }
}

@Composable
fun ImposterGApp(repository: GameRepository) {
    val factory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(repository) as T
                    modelClass.isAssignableFrom(ProfileStatsViewModel::class.java) -> ProfileStatsViewModel(repository) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
                }
            }
        }
    }

    val gameVm: GameViewModel = viewModel(factory = factory)
    val profileVm: ProfileStatsViewModel = viewModel(factory = factory)

    val settings by profileVm.userSettings.collectAsStateWithLifecycle()
    val activeProfile by profileVm.activeProfile.collectAsStateWithLifecycle()
    val profiles by profileVm.profiles.collectAsStateWithLifecycle()
    val history by profileVm.roundHistory.collectAsStateWithLifecycle()
    val achievements by profileVm.achievements.collectAsStateWithLifecycle()
    val matchState by gameVm.matchState.collectAsStateWithLifecycle()

    LaunchedEffect(settings.musicEnabled, settings.sfxEnabled) {
        SynthAudioEngine.configure(settings.musicEnabled, settings.sfxEnabled)
    }

    DisposableEffect(Unit) {
        onDispose {
            SynthAudioEngine.stopAmbientMusic()
        }
    }

    val isSuddenDeathActive = matchState.phase is GamePhase.SuddenDeath ||
        (matchState.phase is GamePhase.VotingResults && (matchState.phase as GamePhase.VotingResults).isTie)
    val strings = remember(settings.language) { stringsFor(settings.language) }
    val navController = rememberNavController()

    fun navigateHome() {
        gameVm.returnToSetup()
        navController.popBackStack(ROUTE_MAIN_MENU, inclusive = false)
    }

    ImposterGTheme(
        graphicsQuality = settings.graphicsQuality,
        reduceMotion = settings.reduceMotion,
        suddenDeath = isSuddenDeathActive
    ) {
        val animDuration = if (settings.reduceMotion) 0 else 300
        NavHost(
            navController = navController,
            startDestination = ROUTE_SPLASH,
            enterTransition = {
                fadeIn(tween(animDuration)) +
                    slideInHorizontally(tween(animDuration)) { it / 6 } +
                    scaleIn(initialScale = 0.94f, animationSpec = tween(animDuration))
            },
            exitTransition = {
                fadeOut(tween(animDuration)) +
                    slideOutHorizontally(tween(animDuration)) { -it / 6 } +
                    scaleOut(targetScale = 0.96f, animationSpec = tween(animDuration))
            },
            popEnterTransition = {
                fadeIn(tween(animDuration)) +
                    slideInHorizontally(tween(animDuration)) { -it / 6 } +
                    scaleIn(initialScale = 0.96f, animationSpec = tween(animDuration))
            },
            popExitTransition = {
                fadeOut(tween(animDuration)) +
                    slideOutHorizontally(tween(animDuration)) { it / 6 } +
                    scaleOut(targetScale = 0.94f, animationSpec = tween(animDuration))
            }
        ) {
            // 1. Splash Screen
            composable(ROUTE_SPLASH) {
                SplashScreen(
                    strings = strings,
                    onFinished = {
                        navController.navigate(ROUTE_MAIN_MENU) {
                            popUpTo(ROUTE_SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Main Menu
            composable(ROUTE_MAIN_MENU) {
                MainMenuScreen(
                    activeProfile = activeProfile,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onPlayOffline = {
                        gameVm.prepareSetupWizard()
                        navController.navigate(ROUTE_PLAYER_SELECTION)
                    },
                    onOpenProfile = { navController.navigate(ROUTE_PLAYER_PROFILE) },
                    onOpenAchievements = { navController.navigate(ROUTE_ACHIEVEMENTS) },
                    onOpenAvatarCustom = { navController.navigate(ROUTE_AVATAR_CUSTOMIZATION) },
                    onOpenHowToPlay = { navController.navigate(ROUTE_HOW_TO_PLAY) },
                    onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
                    onOpenLanguage = { navController.navigate(ROUTE_LANGUAGE) }
                )
            }

            // 3. Language Selection
            composable(ROUTE_LANGUAGE) {
                LanguageSelectionScreen(
                    selectedLanguage = settings.language,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onSelectLanguage = { lang -> profileVm.setLanguage(lang) },
                    onBack = { navController.popBackStack() },
                    onNext = { navController.popBackStack() }
                )
            }

            // 4. Player Selection
            composable(ROUTE_PLAYER_SELECTION) {
                PlayerSelectionScreen(
                    state = matchState,
                    profiles = profiles,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onPlayerCountChange = { count -> gameVm.updatePlayerCount(count) },
                    onUpdateSlotName = { id, name -> gameVm.updateSlotName(id, name) },
                    onToggleSlotBot = { id -> gameVm.toggleSlotBot(id) },
                    onAssignProfile = { id, prof -> gameVm.assignProfileToSlot(id, prof) },
                    onUpdateConfig = { transform -> gameVm.updateConfig(transform) },
                    maxImpostersForCount = { count -> gameVm.maxImpostersFor(count) },
                    onNext = { navController.navigate(ROUTE_CATEGORY_SELECTION) }
                )
            }

            // 5. Avatar Customization
            composable(ROUTE_AVATAR_CUSTOMIZATION) {
                AvatarCustomizationScreen(
                    activeProfile = activeProfile,
                    language = settings.language,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onSaveAvatar = { id, name, avatar ->
                        profileVm.updateProfileAvatar(id, name, avatar)
                    }
                )
            }

            // 6. Category Selection
            composable(ROUTE_CATEGORY_SELECTION) {
                CategorySelectionScreen(
                    state = matchState,
                    language = settings.language,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onToggleCategory = { cat -> gameVm.toggleCategory(cat) },
                    onSelectAll = { gameVm.selectAllCategories() },
                    onUpdateConfig = { transform -> gameVm.updateConfig(transform) },
                    onNext = { navController.navigate(ROUTE_GAME_MODE) }
                )
            }

            // 7. Game Mode Selection
            composable(ROUTE_GAME_MODE) {
                GameModeSelectionScreen(
                    state = matchState,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onSelectMode = { mode -> gameVm.selectGameMode(mode) },
                    onUpdateConfig = { transform -> gameVm.updateConfig(transform) },
                    onNext = { navController.navigate(ROUTE_GAME_RULES) }
                )
            }

            // 8. Game Rules
            composable(ROUTE_GAME_RULES) {
                GameRulesScreen(
                    state = matchState,
                    language = settings.language,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onStartLobby = { navController.navigate(ROUTE_GAME_LOBBY) }
                )
            }

            // 9. Game Lobby
            composable(ROUTE_GAME_LOBBY) {
                GameLobbyScreen(
                    state = matchState,
                    previewPlayers = gameVm.buildLobbyPreviewPlayers(),
                    language = settings.language,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onStartRound = {
                        gameVm.startRound()
                        navController.navigate(ROUTE_GAME_STAGE)
                    }
                )
            }

            // 10–16. Live Gameplay Stage (Wheel -> Secret Card -> Word Reveal -> Discussion -> Voting -> Results -> Imposter Reveal -> Winner Screen)
            composable(ROUTE_GAME_STAGE) {
                BackHandler {
                    gameVm.returnToSetup()
                    navController.popBackStack()
                }

                AnimatedContent(
                    targetState = matchState.phase,
                    contentKey = { phase -> phase::class },
                    transitionSpec = {
                        (fadeIn(tween(animDuration)) + scaleIn(initialScale = 0.95f, animationSpec = tween(animDuration)))
                            .togetherWith(fadeOut(tween(animDuration)) + scaleOut(targetScale = 0.97f, animationSpec = tween(animDuration)))
                    },
                    label = "game_stage_phase"
                ) { phase ->
                    when (phase) {
                        is GamePhase.ChallengeWheelSpin -> ChallengeWheelOverlayScreen(
                            language = settings.language,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onBack = {
                                gameVm.returnToSetup()
                                navController.popBackStack()
                            },
                            onHome = { navigateHome() },
                            onModifierLocked = { mod -> gameVm.lockChallengeAndProceed(mod) }
                        )

                        is GamePhase.SecretCardPass -> SecretCardAndRevealScreen(
                            state = matchState,
                            phase = phase,
                            language = settings.language,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onBack = {
                                gameVm.returnToSetup()
                                navController.popBackStack()
                            },
                            onHome = { navigateHome() },
                            onDismissPassCurtain = { gameVm.dismissPassPhoneCurtain() },
                            onFlipOpen = { gameVm.flipSecretCardOpen() },
                            onHideCard = { gameVm.hideSecretCard() },
                            onNextPlayer = { gameVm.advanceSecretCardPass() },
                            onSkipWord = { gameVm.skipCurrentWord() }
                        )

                        is GamePhase.Discussion -> DiscussionPhaseScreen(
                            state = matchState,
                            language = settings.language,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onBack = {
                                gameVm.returnToSetup()
                                navController.popBackStack()
                            },
                            onHome = { navigateHome() },
                            onTogglePause = { gameVm.toggleTimerPause() },
                            onUseHint = { gameVm.revealLimitedHint() },
                            onMoreBotClues = { gameVm.generateNextBotClueRound() },
                            onStartVoting = { gameVm.beginVotingPhase() }
                        )

                        is GamePhase.Voting -> VotingScreen(
                            state = matchState,
                            isSuddenDeath = false,
                            tiedCandidateIds = emptyList(),
                            currentVoterIndex = phase.currentVoterIndex,
                            votesCast = phase.votesCast,
                            isPrivacyShieldUp = phase.isPrivacyShieldUp,
                            secondsRemaining = phase.secondsRemaining,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onBack = {
                                gameVm.returnToSetup()
                                navController.popBackStack()
                            },
                            onHome = { navigateHome() },
                            onUnlockBallot = { gameVm.unlockCurrentBallot() },
                            onCastVote = { id -> gameVm.castHumanVote(id) }
                        )

                        is GamePhase.SuddenDeath -> VotingScreen(
                            state = matchState,
                            isSuddenDeath = true,
                            tiedCandidateIds = phase.tiedCandidateIds,
                            currentVoterIndex = phase.currentVoterIndex,
                            votesCast = phase.votesCast,
                            isPrivacyShieldUp = phase.isPrivacyShieldUp,
                            secondsRemaining = phase.secondsRemaining,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onBack = {
                                gameVm.returnToSetup()
                                navController.popBackStack()
                            },
                            onHome = { navigateHome() },
                            onUnlockBallot = { gameVm.unlockCurrentBallot() },
                            onCastVote = { id -> gameVm.castHumanVote(id) }
                        )

                        is GamePhase.VotingResults -> VotingResultsScreen(
                            state = matchState,
                            results = phase,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onBack = {
                                gameVm.returnToSetup()
                                navController.popBackStack()
                            },
                            onHome = { navigateHome() },
                            onContinue = { gameVm.proceedFromVotingResults() }
                        )

                        is GamePhase.ImposterReveal -> ImposterRevealScreen(
                            state = matchState,
                            reveal = phase,
                            language = settings.language,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onContinue = { gameVm.proceedToWinnerScreen() }
                        )

                        is GamePhase.WinnerCelebration -> WinnerScreen(
                            state = matchState,
                            winner = phase,
                            language = settings.language,
                            strings = strings,
                            vibrationEnabled = settings.vibrationEnabled,
                            onToggleMissionCompleted = { id -> gameVm.toggleSecretMissionCompleted(id) },
                            onPlayAgain = { gameVm.startRound() },
                            onMainMenu = { navigateHome() }
                        )

                        is GamePhase.Idle -> Unit
                    }
                }
            }

            // 17. Player Profile + History
            composable(ROUTE_PLAYER_PROFILE) {
                PlayerProfileScreen(
                    profiles = profiles,
                    activeProfile = activeProfile,
                    history = history,
                    language = settings.language,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onSetActiveProfile = { id -> profileVm.setActiveProfile(id) },
                    onCreateProfile = { name, av -> profileVm.createProfile(name, av) },
                    onDeleteProfile = { id -> profileVm.deleteProfile(id) },
                    onOpenAvatarCustomization = { navController.navigate(ROUTE_AVATAR_CUSTOMIZATION) },
                    onOpenAchievements = { navController.navigate(ROUTE_ACHIEVEMENTS) }
                )
            }

            // 18. Achievements
            composable(ROUTE_ACHIEVEMENTS) {
                AchievementsScreen(
                    achievements = achievements,
                    language = settings.language,
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() }
                )
            }

            // 19. Settings
            composable(ROUTE_SETTINGS) {
                SettingsScreen(
                    settings = settings,
                    strings = strings,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() },
                    onMusicChange = { on -> profileVm.setMusicEnabled(on) },
                    onSfxChange = { on -> profileVm.setSfxEnabled(on) },
                    onVibrationChange = { on -> profileVm.setVibrationEnabled(on) },
                    onQualityChange = { q -> profileVm.setGraphicsQuality(q) },
                    onReduceMotionChange = { on -> profileVm.setReduceMotion(on) },
                    onToggleLanguage = { profileVm.toggleLanguage() },
                    onReset = { profileVm.resetSettings() }
                )
            }

            // 20. How to Play
            composable(ROUTE_HOW_TO_PLAY) {
                HowToPlayScreen(
                    strings = strings,
                    vibrationEnabled = settings.vibrationEnabled,
                    onBack = { navController.popBackStack() },
                    onHome = { navigateHome() }
                )
            }
        }
    }
}
