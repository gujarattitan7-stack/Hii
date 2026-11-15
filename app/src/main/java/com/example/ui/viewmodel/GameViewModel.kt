package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SynthAudioEngine
import com.example.data.local.UserSettings
import com.example.data.repository.GameRepository
import com.example.data.words.WordBank
import com.example.domain.ActiveMatchState
import com.example.domain.AvatarConfig
import com.example.domain.AvatarPreset
import com.example.domain.CategoryId
import com.example.domain.ChallengeModifier
import com.example.domain.GameModeType
import com.example.domain.GamePhase
import com.example.domain.MatchConfig
import com.example.domain.PlayerProfile
import com.example.domain.PlayerSlot
import com.example.domain.PracticeBotEngine
import com.example.domain.WinnerSide
import com.example.domain.WordEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _matchState = MutableStateFlow(ActiveMatchState())
    val matchState: StateFlow<ActiveMatchState> = _matchState.asStateFlow()

    val savedProfiles: StateFlow<List<PlayerProfile>> = repository.profiles
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val userSettings: StateFlow<UserSettings> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    private var timerJob: Job? = null
    private var botClueRoundCounter = 0

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            val savedConfig = repository.savedMatchConfig.first()
            val profiles = repository.profiles.first()
            val activeId = repository.userSettings.first().activeProfileId
            val active = profiles.firstOrNull { it.id == activeId } ?: profiles.firstOrNull()
            initializeRosterForConfig(savedConfig, active, profiles)
        }
    }

    fun prepareSetupWizard() {
        val profiles = savedProfiles.value
        val activeId = userSettings.value.activeProfileId
        val active = profiles.firstOrNull { it.id == activeId } ?: profiles.firstOrNull()
        val currentCfg = _matchState.value.config
        if (_matchState.value.players.isEmpty()) {
            initializeRosterForConfig(currentCfg, active, profiles)
        }
    }

    fun selectGameMode(mode: GameModeType) {
        val currentCfg = _matchState.value.config
        val profiles = savedProfiles.value
        val activeId = userSettings.value.activeProfileId
        val active = profiles.firstOrNull { it.id == activeId } ?: profiles.firstOrNull()

        val targetPlayerCount = when (mode) {
            GameModeType.PRACTICE -> currentCfg.playerCount.coerceIn(1, 2)
            else -> currentCfg.playerCount.coerceIn(1, 10)
        }
        val nextConfig = when (mode) {
            GameModeType.CLASSIC -> currentCfg.copy(
                modeType = mode,
                playerCount = targetPlayerCount,
                discussionSeconds = 45,
                votingSeconds = 30,
                suddenDeathEnabled = true,
                challengeWheelEnabled = false
            )
            GameModeType.PRACTICE -> currentCfg.copy(
                modeType = mode,
                playerCount = targetPlayerCount,
                hintsEnabled = true,
                maxHintsPerRound = 1,
                suddenDeathEnabled = true,
                challengeWheelEnabled = false
            )
            GameModeType.SUDDEN_DEATH -> currentCfg.copy(
                modeType = mode,
                playerCount = targetPlayerCount,
                discussionSeconds = 30,
                votingSeconds = 20,
                suddenDeathEnabled = true,
                challengeWheelEnabled = false
            )
            GameModeType.CHALLENGE -> currentCfg.copy(
                modeType = mode,
                playerCount = targetPlayerCount,
                challengeWheelEnabled = true,
                secretMissionsEnabled = true
            )
            GameModeType.CUSTOM -> currentCfg.copy(
                modeType = mode,
                playerCount = targetPlayerCount
            )
        }.let { cfg ->
            cfg.copy(imposterCount = cfg.imposterCount.coerceIn(1, maxImpostersFor(cfg.playerCount)))
        }

        if (targetPlayerCount != _matchState.value.players.size) {
            initializeRosterForConfig(nextConfig, active, profiles)
        } else {
            _matchState.update { it.copy(config = nextConfig) }
        }
        persistConfig(nextConfig)
    }

    private fun initializeRosterForConfig(
        config: MatchConfig,
        activeProfile: PlayerProfile?,
        allProfiles: List<PlayerProfile>
    ) {
        val count = config.playerCount.coerceIn(1, 10)
        val slots = mutableListOf<PlayerSlot>()
        for (i in 0 until count) {
            if (i == 0) {
                val leadName = activeProfile?.name ?: "You"
                val leadAvatar = activeProfile?.avatar ?: AvatarConfig.fromPreset(AvatarPreset.LEO)
                slots.add(
                    PlayerSlot(
                        slotId = "human_0",
                        name = leadName,
                        isBot = false,
                        isHost = true,
                        profileId = activeProfile?.id,
                        avatar = leadAvatar
                    )
                )
            } else {
                val linkedProfile = allProfiles.firstOrNull { it.id != activeProfile?.id && slots.none { s -> s.profileId == it.id } }
                val preset = AvatarPreset.entries[i % AvatarPreset.entries.size]
                slots.add(
                    PlayerSlot(
                        slotId = "human_$i",
                        name = linkedProfile?.name ?: preset.displayName,
                        isBot = false,
                        isHost = false,
                        profileId = linkedProfile?.id,
                        avatar = linkedProfile?.avatar ?: AvatarConfig.fromPreset(preset)
                    )
                )
            }
        }
        _matchState.update {
            it.copy(
                config = config,
                players = slots,
                phase = GamePhase.Idle
            )
        }
    }

    fun updatePlayerCount(newCount: Int) {
        val clamped = newCount.coerceIn(1, 10)
        val state = _matchState.value
        val currentPlayers = state.players.toMutableList()
        val profiles = savedProfiles.value

        while (currentPlayers.size < clamped) {
            val idx = currentPlayers.size
            val unusedProfile = profiles.firstOrNull { p -> currentPlayers.none { it.profileId == p.id } }
            val preset = AvatarPreset.entries[idx % AvatarPreset.entries.size]
            currentPlayers.add(
                PlayerSlot(
                    slotId = "human_${idx}_${System.nanoTime()}",
                    name = unusedProfile?.name ?: preset.displayName,
                    isBot = false,
                    isHost = idx == 0,
                    profileId = unusedProfile?.id,
                    avatar = unusedProfile?.avatar ?: AvatarConfig.fromPreset(preset)
                )
            )
        }
        while (currentPlayers.size > clamped) {
            currentPlayers.removeAt(currentPlayers.lastIndex)
        }

        val updatedConfig = state.config.copy(
            playerCount = clamped,
            imposterCount = state.config.imposterCount.coerceIn(1, maxImpostersFor(clamped)),
            modeType = if (clamped <= 2 && state.config.modeType == GameModeType.CLASSIC) {
                GameModeType.PRACTICE
            } else state.config.modeType
        )
        _matchState.update { it.copy(config = updatedConfig, players = currentPlayers) }
        persistConfig(updatedConfig)
    }

    fun toggleSlotBot(slotId: String) {
        val state = _matchState.value
        val updated = state.players.mapIndexed { idx, slot ->
            if (slot.slotId != slotId) slot
            else if (slot.isBot) {
                val preset = AvatarPreset.entries[idx % AvatarPreset.entries.size]
                slot.copy(
                    isBot = false,
                    name = preset.displayName,
                    profileId = null
                )
            } else {
                val humanCount = state.players.count { !it.isBot }
                if (humanCount <= 1) slot
                else PracticeBotEngine.createBotSlot(idx).copy(slotId = slot.slotId)
            }
        }
        _matchState.update { it.copy(players = updated) }
    }

    fun updateSlotName(slotId: String, name: String) {
        _matchState.update { state ->
            state.copy(
                players = state.players.map {
                    if (it.slotId == slotId) it.copy(name = name.take(18)) else it
                }
            )
        }
    }

    fun assignProfileToSlot(slotId: String, profile: PlayerProfile) {
        _matchState.update { state ->
            state.copy(
                players = state.players.map {
                    if (it.slotId == slotId) {
                        it.copy(
                            name = profile.name,
                            isBot = false,
                            profileId = profile.id,
                            avatar = profile.avatar
                        )
                    } else it
                }
            )
        }
    }

    fun updateConfig(transform: (MatchConfig) -> MatchConfig) {
        val current = _matchState.value.config
        var next = transform(current)
        next = next.copy(
            imposterCount = next.imposterCount.coerceIn(1, maxImpostersFor(next.playerCount)),
            selectedCategories = next.selectedCategories.ifEmpty { setOf(CategoryId.CINEMA_ACTORS) },
            discussionSeconds = next.discussionSeconds.coerceIn(20, 300),
            votingSeconds = next.votingSeconds.coerceIn(15, 120),
            maxHintsPerRound = next.maxHintsPerRound.coerceIn(0, 3)
        )
        _matchState.update { it.copy(config = next) }
        persistConfig(next)
    }

    fun toggleCategory(cat: CategoryId) {
        updateConfig { cfg ->
            val nextSet = cfg.selectedCategories.toMutableSet()
            if (cat in nextSet) {
                if (nextSet.size > 1) nextSet.remove(cat)
            } else {
                nextSet.add(cat)
            }
            cfg.copy(selectedCategories = nextSet)
        }
    }

    fun selectAllCategories() {
        updateConfig { it.copy(selectedCategories = CategoryId.entries.toSet()) }
    }

    private fun persistConfig(config: MatchConfig) {
        viewModelScope.launch { repository.saveMatchConfig(config) }
    }

    fun maxImpostersFor(playerCount: Int): Int {
        val effectiveCount = if (playerCount < 3) 4 else playerCount
        return when {
            effectiveCount <= 4 -> 1
            effectiveCount <= 7 -> 2
            else -> 3
        }
    }

    fun buildLobbyPreviewPlayers(): List<PlayerSlot> {
        val roster = _matchState.value.players
        return if (roster.size < 3) {
            val botsNeeded = 4 - roster.size
            roster + (0 until botsNeeded).map { PracticeBotEngine.createBotSlot(it) }
        } else {
            roster
        }
    }

    fun startRound(preLockedChallenge: ChallengeModifier? = null) {
        timerJob?.cancel()
        botClueRoundCounter = 0
        val state = _matchState.value
        val cfg = state.config

        // Practice / small party safeguard: actual Imposter game requires >= 3 active players
        val effectiveRoster = buildLobbyPreviewPlayers()
        val maxImp = maxImpostersFor(effectiveRoster.size)
        val actualImpCount = if (cfg.randomImposterCount) {
            Random.nextInt(1, maxImp + 1)
        } else {
            cfg.imposterCount.coerceIn(1, maxImp)
        }

        val imposterIndices = effectiveRoster.indices.shuffled().take(actualImpCount).toSet()
        val shuffledMissions = WordBank.secretMissions.shuffled()

        val initializedPlayers = effectiveRoster.mapIndexed { idx, slot ->
            slot.copy(
                name = slot.name.trim().ifEmpty { "Player ${idx + 1}" },
                isImposter = idx in imposterIndices,
                hasViewedCard = false,
                isEliminated = false,
                missionCompleted = false,
                secretMission = if (cfg.secretMissionsEnabled) shuffledMissions[idx % shuffledMissions.size] else null
            )
        }

        val chosenWord = pickWord(cfg)
        val useChallenge = preLockedChallenge ?: if (cfg.challengeWheelEnabled) null else null
        val initialPhase = if (cfg.challengeWheelEnabled && preLockedChallenge == null) {
            GamePhase.ChallengeWheelSpin
        } else {
            GamePhase.SecretCardPass(currentHumanIndex = 0)
        }

        val roundDuration = useChallenge?.timeOverrideSeconds ?: cfg.discussionSeconds
        val hintsAllowed = if (cfg.hintsEnabled && useChallenge?.extraHintBlocked != true) {
            cfg.maxHintsPerRound
        } else 0

        _matchState.value = ActiveMatchState(
            config = cfg,
            players = initializedPlayers,
            currentWord = chosenWord,
            activeChallenge = useChallenge,
            phase = initialPhase,
            secondsRemaining = roundDuration,
            isTimerRunning = false,
            skipsRemaining = if (cfg.skipWordEnabled) 2 else 0,
            hintsRemaining = hintsAllowed,
            hintRevealedForCrew = false,
            botClues = emptyList(),
            suddenDeathOccurred = false,
            roundSaved = false
        )
    }

    fun lockChallengeAndProceed(modifier: ChallengeModifier) {
        val state = _matchState.value
        val adjustedSeconds = modifier.timeOverrideSeconds ?: state.config.discussionSeconds
        val hintsAllowed = if (state.config.hintsEnabled && !modifier.extraHintBlocked) {
            state.config.maxHintsPerRound
        } else 0

        _matchState.update {
            it.copy(
                activeChallenge = modifier,
                secondsRemaining = adjustedSeconds,
                hintsRemaining = hintsAllowed,
                phase = GamePhase.SecretCardPass(currentHumanIndex = 0)
            )
        }
    }

    fun flipSecretCardOpen() {
        val phase = _matchState.value.phase as? GamePhase.SecretCardPass ?: return
        val humanPlayers = _matchState.value.players.filter { !it.isBot }
        val current = humanPlayers.getOrNull(phase.currentHumanIndex)
        if (current?.isImposter == true) {
            SynthAudioEngine.playImposterSting()
        } else {
            SynthAudioEngine.playWordReveal()
        }
        _matchState.update {
            it.copy(
                phase = phase.copy(
                    isCardFlipped = true,
                    cardRevealedOnce = true,
                    isPassPhoneCurtain = false
                )
            )
        }
    }

    fun hideSecretCard() {
        val phase = _matchState.value.phase as? GamePhase.SecretCardPass ?: return
        SynthAudioEngine.playCardFlip()
        _matchState.update {
            it.copy(phase = phase.copy(isCardFlipped = false))
        }
    }

    fun dismissPassPhoneCurtain() {
        val phase = _matchState.value.phase as? GamePhase.SecretCardPass ?: return
        _matchState.update {
            it.copy(phase = phase.copy(isPassPhoneCurtain = false, isCardFlipped = false))
        }
    }

    fun advanceSecretCardPass() {
        val state = _matchState.value
        val phase = state.phase as? GamePhase.SecretCardPass ?: return
        val humanPlayers = state.players.filter { !it.isBot }
        val currentHuman = humanPlayers.getOrNull(phase.currentHumanIndex)

        val updatedPlayers = state.players.map {
            if (currentHuman != null && it.slotId == currentHuman.slotId) {
                it.copy(hasViewedCard = true)
            } else it
        }

        val nextIdx = phase.currentHumanIndex + 1
        if (nextIdx < humanPlayers.size) {
            SynthAudioEngine.playCardFlip()
            _matchState.update {
                it.copy(
                    players = updatedPlayers,
                    phase = GamePhase.SecretCardPass(
                        currentHumanIndex = nextIdx,
                        isCardFlipped = false,
                        cardRevealedOnce = false,
                        isPassPhoneCurtain = true
                    )
                )
            }
        } else {
            val word = state.currentWord
            val initialBotClues = if (word != null) {
                PracticeBotEngine.generateClueRound(
                    players = updatedPlayers,
                    word = word,
                    difficulty = state.config.difficulty,
                    roundIndex = botClueRoundCounter++
                )
            } else emptyList()

            _matchState.update {
                it.copy(
                    players = updatedPlayers,
                    botClues = initialBotClues,
                    phase = GamePhase.Discussion,
                    isTimerRunning = true
                )
            }
            startActivePhaseTimer()
        }
    }

    fun skipCurrentWord() {
        val state = _matchState.value
        if (!state.config.skipWordEnabled || state.skipsRemaining <= 0) return
        timerJob?.cancel()
        val newWord = pickWord(state.config, excludeId = state.currentWord?.id)
        botClueRoundCounter = 0

        val resetPlayers = state.players.map { it.copy(hasViewedCard = false) }
        val newSeconds = state.activeChallenge?.timeOverrideSeconds ?: state.config.discussionSeconds

        _matchState.update {
            it.copy(
                players = resetPlayers,
                currentWord = newWord,
                skipsRemaining = it.skipsRemaining - 1,
                secondsRemaining = newSeconds,
                isTimerRunning = false,
                hintRevealedForCrew = false,
                botClues = emptyList(),
                phase = GamePhase.SecretCardPass(currentHumanIndex = 0)
            )
        }
    }

    fun revealLimitedHint() {
        val state = _matchState.value
        if (!state.config.hintsEnabled || state.activeChallenge?.extraHintBlocked == true) return
        if (state.hintsRemaining <= 0) return
        _matchState.update {
            it.copy(
                hintsRemaining = (it.hintsRemaining - 1).coerceAtLeast(0),
                hintRevealedForCrew = true
            )
        }
    }

    fun generateNextBotClueRound() {
        val state = _matchState.value
        val word = state.currentWord ?: return
        val more = PracticeBotEngine.generateClueRound(
            players = state.players,
            word = word,
            difficulty = state.config.difficulty,
            roundIndex = botClueRoundCounter++
        )
        _matchState.update { it.copy(botClues = it.botClues + more) }
    }

    fun toggleTimerPause() {
        val running = !_matchState.value.isTimerRunning
        _matchState.update { it.copy(isTimerRunning = running) }
        if (running) startActivePhaseTimer() else timerJob?.cancel()
    }

    private fun startActivePhaseTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = _matchState.value
                if (!state.isTimerRunning) break
                when (val phase = state.phase) {
                    is GamePhase.Discussion -> {
                        if (state.secondsRemaining > 0) {
                            _matchState.update { it.copy(secondsRemaining = it.secondsRemaining - 1) }
                        } else {
                            _matchState.update { it.copy(isTimerRunning = false) }
                            beginVotingPhase()
                            break
                        }
                    }
                    is GamePhase.Voting -> {
                        if (phase.secondsRemaining > 0) {
                            _matchState.update { it.copy(phase = phase.copy(secondsRemaining = phase.secondsRemaining - 1)) }
                        } else {
                            autoCompleteRemainingHumanVotes(phase.votesCast, isSuddenDeath = false)
                            break
                        }
                    }
                    is GamePhase.SuddenDeath -> {
                        if (phase.secondsRemaining > 0) {
                            _matchState.update { it.copy(phase = phase.copy(secondsRemaining = phase.secondsRemaining - 1)) }
                        } else {
                            autoCompleteRemainingHumanVotes(phase.votesCast, isSuddenDeath = true)
                            break
                        }
                    }
                    else -> break
                }
            }
        }
    }

    fun beginVotingPhase() {
        timerJob?.cancel()
        val state = _matchState.value
        val activeHumans = state.players.filter { !it.isBot && !it.isEliminated }
        val activeBots = state.players.filter { it.isBot && !it.isEliminated }
        val activeAll = state.players.filter { !it.isEliminated }

        val botVotes = PracticeBotEngine.castSmartBotVotes(
            voters = activeBots,
            candidates = activeAll,
            clues = state.botClues,
            difficulty = state.config.difficulty
        )

        if (activeHumans.isEmpty()) {
            resolveVotes(botVotes, isSuddenDeath = false)
            return
        }

        _matchState.update {
            it.copy(
                isTimerRunning = true,
                phase = GamePhase.Voting(
                    currentVoterIndex = 0,
                    votesCast = botVotes,
                    isPrivacyShieldUp = state.config.secretVotingEnabled && activeHumans.size > 1,
                    secondsRemaining = state.config.votingSeconds
                )
            )
        }
        startActivePhaseTimer()
    }

    fun unlockCurrentBallot() {
        val state = _matchState.value
        when (val p = state.phase) {
            is GamePhase.Voting -> _matchState.update { it.copy(phase = p.copy(isPrivacyShieldUp = false)) }
            is GamePhase.SuddenDeath -> _matchState.update { it.copy(phase = p.copy(isPrivacyShieldUp = false)) }
            else -> Unit
        }
    }

    fun castHumanVote(targetSlotId: String) {
        val state = _matchState.value
        val activeHumans = state.players.filter { !it.isBot && !it.isEliminated }

        when (val p = state.phase) {
            is GamePhase.Voting -> {
                val voter = activeHumans.getOrNull(p.currentVoterIndex) ?: return
                // Strict rules: No self-voting & No duplicate votes
                if (targetSlotId == voter.slotId) return
                if (p.votesCast.containsKey(voter.slotId)) return

                SynthAudioEngine.playVoteLock()
                val updatedVotes = p.votesCast + (voter.slotId to targetSlotId)
                val nextIdx = p.currentVoterIndex + 1

                if (!state.config.secretVotingEnabled && activeHumans.size > 1) {
                    val consensusVotes = updatedVotes.toMutableMap()
                    activeHumans.forEach { human ->
                        if (human.slotId != targetSlotId) {
                            consensusVotes[human.slotId] = targetSlotId
                        } else {
                            val fallback = state.players.firstOrNull { !it.isEliminated && it.slotId != human.slotId }?.slotId
                            if (fallback != null) consensusVotes[human.slotId] = fallback
                        }
                    }
                    resolveVotes(consensusVotes, isSuddenDeath = false)
                } else if (nextIdx < activeHumans.size) {
                    _matchState.update {
                        it.copy(
                            phase = p.copy(
                                currentVoterIndex = nextIdx,
                                votesCast = updatedVotes,
                                isPrivacyShieldUp = true,
                                secondsRemaining = state.config.votingSeconds
                            )
                        )
                    }
                } else {
                    resolveVotes(updatedVotes, isSuddenDeath = false)
                }
            }

            is GamePhase.SuddenDeath -> {
                val voter = activeHumans.getOrNull(p.currentVoterIndex) ?: return
                if (targetSlotId == voter.slotId) return
                if (p.votesCast.containsKey(voter.slotId)) return

                SynthAudioEngine.playVoteLock()
                val updatedVotes = p.votesCast + (voter.slotId to targetSlotId)
                val nextIdx = p.currentVoterIndex + 1

                if (!state.config.secretVotingEnabled && activeHumans.size > 1) {
                    val consensusVotes = updatedVotes.toMutableMap()
                    activeHumans.forEach { human ->
                        if (human.slotId != targetSlotId) consensusVotes[human.slotId] = targetSlotId
                    }
                    resolveVotes(consensusVotes, isSuddenDeath = true)
                } else if (nextIdx < activeHumans.size) {
                    _matchState.update {
                        it.copy(
                            phase = p.copy(
                                currentVoterIndex = nextIdx,
                                votesCast = updatedVotes,
                                isPrivacyShieldUp = true,
                                secondsRemaining = 20
                            )
                        )
                    }
                } else {
                    resolveVotes(updatedVotes, isSuddenDeath = true)
                }
            }

            else -> Unit
        }
    }

    private fun autoCompleteRemainingHumanVotes(existingVotes: Map<String, String>, isSuddenDeath: Boolean) {
        val state = _matchState.value
        val activeAll = state.players.filter { !it.isEliminated }
        val updated = existingVotes.toMutableMap()
        val activeHumans = activeAll.filter { !it.isBot }
        for (human in activeHumans) {
            if (!updated.containsKey(human.slotId)) {
                val candidates = activeAll.filter { it.slotId != human.slotId }
                val pick = candidates.randomOrNull()
                if (pick != null) {
                    updated[human.slotId] = pick.slotId
                }
            }
        }
        resolveVotes(updated, isSuddenDeath)
    }

    private fun resolveVotes(votesCast: Map<String, String>, isSuddenDeath: Boolean) {
        timerJob?.cancel()
        val state = _matchState.value
        val activeAll = state.players.filter { !it.isEliminated }
        val tally = mutableMapOf<String, Int>()
        activeAll.forEach { tally[it.slotId] = 0 }
        votesCast.values.forEach { targetId ->
            tally[targetId] = (tally[targetId] ?: 0) + 1
        }
        val maxVotes = tally.values.maxOrNull() ?: 0
        val topCandidates = tally.filterValues { it == maxVotes }.keys.toList()
        val isTie = topCandidates.size > 1

        val eliminatedId = if (isTie && state.config.suddenDeathEnabled && !isSuddenDeath) {
            null
        } else {
            topCandidates.randomOrNull()
        }

        val updatedPlayers = if (eliminatedId != null) {
            state.players.map { if (it.slotId == eliminatedId) it.copy(isEliminated = true) else it }
        } else {
            state.players
        }

        val eliminatedSlot = updatedPlayers.firstOrNull { it.slotId == eliminatedId }
        val remainingImposters = updatedPlayers.count { it.isImposter && !it.isEliminated }
        val winnerSide = if (eliminatedSlot?.isImposter == true || remainingImposters == 0) {
            WinnerSide.CREW
        } else {
            WinnerSide.IMPOSTER
        }

        SynthAudioEngine.playEliminate()
        _matchState.update {
            it.copy(
                players = updatedPlayers,
                isTimerRunning = false,
                phase = GamePhase.VotingResults(
                    voteCounts = tally,
                    eliminatedPlayerId = eliminatedId,
                    isTie = isTie && state.config.suddenDeathEnabled && !isSuddenDeath,
                    tiedCandidateIds = topCandidates,
                    winnerSide = winnerSide,
                    votesCast = votesCast
                )
            )
        }
    }

    fun proceedFromVotingResults() {
        val state = _matchState.value
        val results = state.phase as? GamePhase.VotingResults ?: return

        if (results.isTie) {
            val activeBots = state.players.filter { it.isBot && !it.isEliminated }
            val tiedSlots = state.players.filter { it.slotId in results.tiedCandidateIds }
            val botSuddenVotes = PracticeBotEngine.castSmartBotVotes(
                voters = activeBots,
                candidates = tiedSlots,
                clues = state.botClues,
                difficulty = state.config.difficulty
            )
            val activeHumans = state.players.filter { !it.isBot && !it.isEliminated }
            _matchState.update {
                it.copy(
                    suddenDeathOccurred = true,
                    isTimerRunning = true,
                    phase = GamePhase.SuddenDeath(
                        tiedCandidateIds = results.tiedCandidateIds,
                        currentVoterIndex = 0,
                        votesCast = botSuddenVotes,
                        isPrivacyShieldUp = state.config.secretVotingEnabled && activeHumans.size > 1,
                        secondsRemaining = 25
                    )
                )
            }
            startActivePhaseTimer()
            return
        }

        _matchState.update {
            it.copy(
                phase = GamePhase.ImposterReveal(
                    winnerSide = results.winnerSide,
                    eliminatedPlayerId = results.eliminatedPlayerId,
                    voteCounts = results.voteCounts,
                    xpEarnedTotal = 0,
                    newlyUnlockedAchievementIds = emptyList(),
                    leveledUpProfileNames = emptyList()
                )
            )
        }
        saveResultsToDatabase(results.votesCast, results.winnerSide)
    }

    fun proceedToWinnerScreen() {
        val reveal = _matchState.value.phase as? GamePhase.ImposterReveal ?: return
        SynthAudioEngine.playVictory()
        _matchState.update {
            it.copy(
                phase = GamePhase.WinnerCelebration(
                    winnerSide = reveal.winnerSide,
                    eliminatedPlayerId = reveal.eliminatedPlayerId,
                    voteCounts = reveal.voteCounts,
                    xpEarnedTotal = reveal.xpEarnedTotal,
                    newlyUnlockedAchievementIds = reveal.newlyUnlockedAchievementIds,
                    leveledUpProfileNames = reveal.leveledUpProfileNames
                )
            )
        }
    }

    fun toggleSecretMissionCompleted(slotId: String) {
        val state = _matchState.value
        val target = state.players.firstOrNull { it.slotId == slotId } ?: return
        val newCompleted = !target.missionCompleted
        val mult = state.activeChallenge?.xpMultiplier ?: 1.0f
        val baseBonus = target.secretMission?.bonusXp ?: 50
        val deltaXp = ((baseBonus * mult).toInt()) * (if (newCompleted) 1 else -1)
        val deltaCount = if (newCompleted) 1 else -1

        _matchState.update { current ->
            val updatedPlayers = current.players.map {
                if (it.slotId == slotId) it.copy(missionCompleted = newCompleted) else it
            }
            val updatedPhase = when (val p = current.phase) {
                is GamePhase.ImposterReveal -> p.copy(xpEarnedTotal = (p.xpEarnedTotal + deltaXp).coerceAtLeast(0))
                is GamePhase.WinnerCelebration -> p.copy(xpEarnedTotal = (p.xpEarnedTotal + deltaXp).coerceAtLeast(0))
                else -> p
            }
            current.copy(players = updatedPlayers, phase = updatedPhase)
        }

        viewModelScope.launch {
            repository.awardSecretMissionBonus(
                profileId = target.profileId,
                bonusXp = deltaXp,
                deltaCount = deltaCount
            )
        }
    }

    private fun saveResultsToDatabase(votesCast: Map<String, String>, winnerSide: WinnerSide) {
        val state = _matchState.value
        if (state.roundSaved) return
        val word = state.currentWord ?: return
        val lang = userSettings.value.language
        viewModelScope.launch {
            val saved = repository.recordRoundOutcome(
                config = state.config,
                players = state.players,
                word = word,
                winnerSide = winnerSide,
                votesCast = votesCast,
                suddenDeathTriggered = state.suddenDeathOccurred,
                challengeTitleEn = state.activeChallenge?.titleEn,
                challengeTitleHi = state.activeChallenge?.titleHi,
                xpMultiplier = state.activeChallenge?.xpMultiplier ?: 1.0f,
                activeLanguage = lang
            )
            _matchState.update { current ->
                val updatedPhase = when (val p = current.phase) {
                    is GamePhase.ImposterReveal -> p.copy(
                        xpEarnedTotal = saved.xpAwardedTotal,
                        newlyUnlockedAchievementIds = saved.newlyUnlockedAchievementIds,
                        leveledUpProfileNames = saved.leveledUpNames
                    )
                    is GamePhase.WinnerCelebration -> p.copy(
                        xpEarnedTotal = saved.xpAwardedTotal,
                        newlyUnlockedAchievementIds = saved.newlyUnlockedAchievementIds,
                        leveledUpProfileNames = saved.leveledUpNames
                    )
                    else -> p
                }
                current.copy(roundSaved = true, phase = updatedPhase)
            }
        }
    }

    fun returnToSetup() {
        timerJob?.cancel()
        _matchState.update { it.copy(phase = GamePhase.Idle, isTimerRunning = false) }
    }

    private fun pickWord(config: MatchConfig, excludeId: String? = null): WordEntry {
        val pool = WordBank.wordsFor(config.selectedCategories, config.difficulty)
        val filtered = pool.filter { it.id != excludeId }.ifEmpty { pool }
        return filtered.random()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
