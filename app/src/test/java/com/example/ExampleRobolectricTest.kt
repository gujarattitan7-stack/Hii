package com.example

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.local.UserSettings
import com.example.data.repository.GameRepository
import com.example.data.words.WordBank
import com.example.domain.ActiveMatchState
import com.example.domain.AppLanguage
import com.example.domain.AvatarConfig
import com.example.domain.AvatarPreset
import com.example.domain.CategoryId
import com.example.domain.DifficultyLevel
import com.example.domain.GameModeType
import com.example.domain.GamePhase
import com.example.domain.GlassesStyle
import com.example.domain.GraphicsQuality
import com.example.domain.HatStyle
import com.example.domain.MatchConfig
import com.example.domain.PlayerSlot
import com.example.domain.PracticeBotEngine
import com.example.domain.WinnerSide
import com.example.localization.stringsFor
import com.example.ui.screens.CategorySelectionScreen
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VotingScreen
import com.example.ui.theme.ImposterGTheme
import com.example.ui.viewmodel.GameViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `app name matches Imposter G`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Imposter G", appName)
    }

    @Test
    fun `word bank has 10 categories with 100 unique EN and 100 unique HI words each`() {
        assertEquals(10, CategoryId.entries.size)
        assertEquals(1000, WordBank.allWords.size)
        CategoryId.entries.forEach { cat ->
            val catWords = WordBank.allWords.filter { it.category == cat }
            assertEquals("Category $cat must have 100 words", 100, catWords.size)
            assertEquals("Category $cat EN words must be unique", 100, catWords.map { it.wordEn }.distinct().size)
            assertEquals("Category $cat HI words must be unique", 100, catWords.map { it.wordHi }.distinct().size)
            assertTrue(catWords.any { it.difficulty == DifficultyLevel.EASY })
            assertTrue(catWords.any { it.difficulty == DifficultyLevel.MEDIUM })
            assertTrue(catWords.any { it.difficulty == DifficultyLevel.HARD })
        }
        assertEquals(20, AvatarPreset.entries.size)
        assertEquals(10, WordBank.achievements.size)
    }

    @Test
    fun `practice bot engine generates bilingual clues and never self-votes`() {
        val slots = listOf(
            PracticeBotEngine.createBotSlot(0).copy(isImposter = false),
            PracticeBotEngine.createBotSlot(1).copy(isImposter = true),
            PracticeBotEngine.createBotSlot(2).copy(isImposter = false),
            PracticeBotEngine.createBotSlot(3).copy(isImposter = false)
        )
        val word = WordBank.allWords.first()
        val clues = PracticeBotEngine.generateClueRound(slots, word, DifficultyLevel.MEDIUM, 0)
        assertEquals(4, clues.size)
        clues.forEach { clue ->
            assertTrue(clue.clueTextEn.isNotBlank())
            assertTrue(clue.clueTextHi.isNotBlank())
        }

        val votes = PracticeBotEngine.castSmartBotVotes(slots, slots, clues, DifficultyLevel.HARD)
        assertEquals(4, votes.size)
        votes.forEach { (voterId, targetId) ->
            assertTrue("Bot must not vote for itself", voterId != targetId)
            assertTrue(slots.any { it.slotId == targetId })
        }
    }

    @Test
    fun `room v2 repository persists profiles, 3D avatars, round history, XP, losses, and settings`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = AppPreferences(context)
        val repo = GameRepository(db, prefs)

        repo.ensureSeeded()
        val initialProfiles = repo.profiles.first()
        assertEquals(4, initialProfiles.size)
        assertTrue(initialProfiles.any { it.name == "GamerPro" })

        val customAvatar = AvatarConfig.fromPreset(AvatarPreset.GAMER_PRO).copy(
            hat = HatStyle.CROWN,
            glasses = GlassesStyle.CYBER_VISOR,
            pedestalColorIndex = 2
        )
        val newId = repo.createProfile("Detective Test", customAvatar)
        val created = repo.profiles.first().first { it.id == newId }
        assertEquals(AvatarPreset.GAMER_PRO, created.avatar.preset)
        assertEquals(HatStyle.CROWN, created.avatar.hat)
        assertEquals(GlassesStyle.CYBER_VISOR, created.avatar.glasses)

        repo.setMusicEnabled(false)
        repo.setSfxEnabled(false)
        repo.setVibrationEnabled(false)
        repo.setGraphicsQuality(GraphicsQuality.MEDIUM)
        repo.setReduceMotion(true)
        val customSettings = repo.userSettings.first()
        assertFalse(customSettings.musicEnabled)
        assertFalse(customSettings.sfxEnabled)
        assertFalse(customSettings.vibrationEnabled)
        assertEquals(GraphicsQuality.MEDIUM, customSettings.graphicsQuality)
        assertTrue(customSettings.reduceMotion)

        repo.resetSettings()
        val resetSettings = repo.userSettings.first()
        assertTrue(resetSettings.musicEnabled)
        assertEquals(GraphicsQuality.HIGH, resetSettings.graphicsQuality)
        assertFalse(resetSettings.reduceMotion)

        val playerSlots = listOf(
            PlayerSlot("h0", created.name, isBot = false, profileId = newId, isImposter = false),
            PracticeBotEngine.createBotSlot(0).copy(isImposter = true),
            PracticeBotEngine.createBotSlot(1).copy(isImposter = false),
            PracticeBotEngine.createBotSlot(2).copy(isImposter = false)
        )
        val word = WordBank.allWords.first()
        val saveResult = repo.recordRoundOutcome(
            config = MatchConfig(modeType = GameModeType.PRACTICE, playerCount = 1),
            players = playerSlots,
            word = word,
            winnerSide = WinnerSide.CREW,
            votesCast = mapOf("h0" to playerSlots[1].slotId),
            suddenDeathTriggered = true,
            challengeTitleEn = "Turbo 30s Clock",
            challengeTitleHi = "टर्बो 30 सेकंड",
            xpMultiplier = 1.3f,
            activeLanguage = AppLanguage.ENGLISH
        )
        assertTrue(saveResult.xpAwardedTotal > 0)
        assertTrue("ach_first_win" in saveResult.newlyUnlockedAchievementIds)
        assertTrue("ach_sudden_death" in saveResult.newlyUnlockedAchievementIds)

        val updatedProfile = repo.profiles.first().first { it.id == newId }
        assertTrue(updatedProfile.xp > 0)
        assertEquals(1, updatedProfile.gamesPlayed)
        assertEquals(1, updatedProfile.crewWins)
        assertEquals(0, updatedProfile.losses)

        val history = repo.roundHistory.first()
        assertEquals(1, history.size)
        assertEquals(GameModeType.PRACTICE, history.first().modeType)
        db.close()
    }

    @Test
    fun `game state machine enforces practice min 3 players, card flip, skip word, and strict voting`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = GameRepository(db, AppPreferences(context))
        repo.ensureSeeded()
        val vm = GameViewModel(repo)

        // Practice Mode with 1 player must auto-expand to >= 3 active players
        vm.selectGameMode(GameModeType.PRACTICE)
        vm.updatePlayerCount(1)
        vm.startRound()
        assertTrue("1-player practice must run with >=3 active players", vm.matchState.value.players.size >= 3)

        // Full 10-player Challenge Mode test
        vm.selectGameMode(GameModeType.CHALLENGE)
        vm.updatePlayerCount(10)
        assertEquals(10, vm.matchState.value.players.size)
        vm.updateConfig { it.copy(imposterCount = 3, challengeWheelEnabled = true, skipWordEnabled = true, hintsEnabled = true, maxHintsPerRound = 1) }

        vm.startRound()
        assertTrue(vm.matchState.value.phase is GamePhase.ChallengeWheelSpin)
        assertEquals(3, vm.matchState.value.players.count { it.isImposter })
        assertNotNull(vm.matchState.value.currentWord)

        val firstWordId = vm.matchState.value.currentWord!!.id
        vm.lockChallengeAndProceed(WordBank.challengeModifiers.first())
        assertTrue(vm.matchState.value.phase is GamePhase.SecretCardPass)

        vm.flipSecretCardOpen()
        val flipped = vm.matchState.value.phase as GamePhase.SecretCardPass
        assertTrue(flipped.isCardFlipped)
        assertTrue(flipped.cardRevealedOnce)

        vm.hideSecretCard()
        assertFalse((vm.matchState.value.phase as GamePhase.SecretCardPass).isCardFlipped)

        vm.skipCurrentWord()
        assertEquals(1, vm.matchState.value.skipsRemaining)
        assertFalse(vm.matchState.value.currentWord!!.id == firstWordId)

        // Test Voting constraints (no self-vote)
        vm.beginVotingPhase()
        val voting = vm.matchState.value.phase as GamePhase.Voting
        val activeHuman = vm.matchState.value.players.first { !it.isBot }
        vm.unlockCurrentBallot()
        vm.castHumanVote(activeHuman.slotId) // Attempt self-vote -> MUST BE IGNORED
        assertFalse(
            "Self-vote must be blocked",
            (vm.matchState.value.phase as? GamePhase.Voting)?.votesCast?.containsKey(activeHuman.slotId) == true
        )

        val validTarget = vm.matchState.value.players.first { it.slotId != activeHuman.slotId }
        vm.castHumanVote(validTarget.slotId)
        val afterValidVote = vm.matchState.value.phase
        assertTrue(
            afterValidVote is GamePhase.Voting || afterValidVote is GamePhase.VotingResults
        )
        db.close()
    }

    @Test
    fun `main menu and language screen support instant English and Hindi switching`() {
        composeRule.setContent {
            val langState = remember { mutableStateOf(AppLanguage.ENGLISH) }
            ImposterGTheme {
                val strings = stringsFor(langState.value)
                LanguageSelectionScreen(
                    selectedLanguage = langState.value,
                    strings = strings,
                    vibrationEnabled = false,
                    onSelectLanguage = { langState.value = it },
                    onBack = {},
                    onNext = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Language Selection").assertIsDisplayed()
        composeRule.onNodeWithTag("lang_card_hi").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("भाषा चुनें").assertIsDisplayed()
    }

    @Test
    fun `main menu displays Play Offline and all 6 primary tile actions`() {
        composeRule.setContent {
            ImposterGTheme {
                MainMenuScreen(
                    activeProfile = null,
                    strings = stringsFor(AppLanguage.ENGLISH),
                    vibrationEnabled = false,
                    onPlayOffline = {},
                    onOpenProfile = {},
                    onOpenAchievements = {},
                    onOpenAvatarCustom = {},
                    onOpenHowToPlay = {},
                    onOpenSettings = {},
                    onOpenLanguage = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("menu_play_offline").assertIsDisplayed()
        composeRule.onNodeWithTag("menu_profile").assertIsDisplayed()
        composeRule.onNodeWithTag("menu_achievements").assertIsDisplayed()
        composeRule.onNodeWithTag("menu_avatar").assertIsDisplayed()
        composeRule.onNodeWithTag("menu_how_to_play").assertIsDisplayed()
        composeRule.onNodeWithTag("menu_settings").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("menu_language").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `voting screen blocks self-vote and activates confirm on suspect tap`() {
        val players = listOf(
            PlayerSlot("h0", "You", isBot = false, isHost = true, avatar = AvatarConfig.fromPreset(AvatarPreset.LEO)),
            PlayerSlot("h1", "Riya", isBot = true, avatar = AvatarConfig.fromPreset(AvatarPreset.RIYA)),
            PlayerSlot("h2", "Arjun", isBot = true, avatar = AvatarConfig.fromPreset(AvatarPreset.ARJUN))
        )
        composeRule.setContent {
            ImposterGTheme {
                VotingScreen(
                    state = ActiveMatchState(players = players),
                    isSuddenDeath = false,
                    tiedCandidateIds = emptyList(),
                    currentVoterIndex = 0,
                    votesCast = emptyMap(),
                    isPrivacyShieldUp = false,
                    secondsRemaining = 30,
                    strings = stringsFor(AppLanguage.ENGLISH),
                    vibrationEnabled = false,
                    onBack = {},
                    onHome = {},
                    onUnlockBallot = {},
                    onCastVote = {}
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("vote_candidate_h0").assertDoesNotExist()
        composeRule.onNodeWithTag("vote_confirm_btn").assertIsNotEnabled()
        composeRule.onNodeWithTag("vote_candidate_h1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("vote_confirm_btn").assertIsEnabled()
    }

    @Test
    fun `settings and category screens render and toggle cleanly`() {
        composeRule.setContent {
            val settingsState = remember { mutableStateOf(UserSettings()) }
            ImposterGTheme(
                graphicsQuality = settingsState.value.graphicsQuality,
                reduceMotion = settingsState.value.reduceMotion
            ) {
                SettingsScreen(
                    settings = settingsState.value,
                    strings = stringsFor(settingsState.value.language),
                    onBack = {},
                    onHome = {},
                    onMusicChange = { settingsState.value = settingsState.value.copy(musicEnabled = it) },
                    onSfxChange = { settingsState.value = settingsState.value.copy(sfxEnabled = it) },
                    onVibrationChange = { settingsState.value = settingsState.value.copy(vibrationEnabled = it) },
                    onQualityChange = { settingsState.value = settingsState.value.copy(graphicsQuality = it) },
                    onReduceMotionChange = { settingsState.value = settingsState.value.copy(reduceMotion = it) },
                    onToggleLanguage = {},
                    onReset = { settingsState.value = UserSettings() }
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("setting_music").assertIsDisplayed()
        composeRule.onNodeWithTag("setting_quality_low").performClick()
        composeRule.onNodeWithTag("setting_reduce_motion").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("setting_reset_btn").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `category selection displays all 10 category cards`() {
        composeRule.setContent {
            ImposterGTheme {
                CategorySelectionScreen(
                    state = ActiveMatchState(),
                    language = AppLanguage.ENGLISH,
                    strings = stringsFor(AppLanguage.ENGLISH),
                    vibrationEnabled = false,
                    onBack = {},
                    onHome = {},
                    onToggleCategory = {},
                    onSelectAll = {},
                    onUpdateConfig = {},
                    onNext = {}
                )
            }
        }
        composeRule.waitForIdle()
        for (id in 1..10) {
            composeRule.onNodeWithTag("category_card_$id").assertExists()
        }
    }
}
