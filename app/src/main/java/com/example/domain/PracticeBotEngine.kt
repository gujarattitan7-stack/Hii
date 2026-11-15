package com.example.domain

import kotlin.random.Random

object PracticeBotEngine {

    private val botRoster = listOf(
        Triple("Rohan", AvatarPreset.ROHAN, BotPersonality.ANALYTICAL),
        Triple("Ananya", AvatarPreset.ANANYA, BotPersonality.CAUTIOUS),
        Triple("Kabir", AvatarPreset.KABIR, BotPersonality.BOLD),
        Triple("Zara", AvatarPreset.ZARA, BotPersonality.BLUFFER),
        Triple("Vikram", AvatarPreset.VIKRAM, BotPersonality.BOLD),
        Triple("Tara", AvatarPreset.TARA, BotPersonality.ANALYTICAL),
        Triple("Dev", AvatarPreset.DEV, BotPersonality.CAUTIOUS),
        Triple("Meera", AvatarPreset.MEERA, BotPersonality.BLUFFER),
        Triple("Ishaan", AvatarPreset.ISHAAN, BotPersonality.ANALYTICAL)
    )

    private val genericSlipsEn = listOf(
        "It's definitely something everyone recognizes",
        "Seen a version of this pretty recently",
        "Very iconic, hard to pin down in one clue",
        "Feels super familiar in everyday life"
    )

    private val genericSlipsHi = listOf(
        "यह ऐसी चीज़ है जिसे लगभग सब पहचानते हैं",
        "हाल ही में इसके बारे में काफी सुना है",
        "बहुत मशहूर है, एक शब्द में बताना मुश्किल है",
        "रोज़मर्रा की ज़िंदगी में इसका ज़िक्र होता है"
    )

    fun createBotSlot(index: Int): PlayerSlot {
        val (name, preset, personality) = botRoster[index % botRoster.size]
        return PlayerSlot(
            slotId = "bot_${index}_${System.nanoTime()}",
            name = name,
            isBot = true,
            isHost = false,
            profileId = null,
            avatar = AvatarConfig.fromPreset(preset),
            personality = personality
        )
    }

    fun generateClueRound(
        players: List<PlayerSlot>,
        word: WordEntry,
        difficulty: DifficultyLevel,
        roundIndex: Int
    ): List<BotClue> {
        val activeBots = players.filter { it.isBot && !it.isEliminated }
        return activeBots.mapIndexed { idx, bot ->
            val slipChance = when (difficulty) {
                DifficultyLevel.EASY -> 0.48f
                DifficultyLevel.MEDIUM -> 0.28f
                DifficultyLevel.HARD -> 0.14f
                DifficultyLevel.MIXED -> 0.28f
            }

            if (bot.isImposter) {
                val makesSlip = Random.nextFloat() < slipChance
                val enPool = if (makesSlip) genericSlipsEn else word.bluffCluesEn
                val hiPool = if (makesSlip) genericSlipsHi else word.bluffCluesHi
                val pick = (roundIndex + idx) % enPool.size
                BotClue(
                    slotId = bot.slotId,
                    playerName = bot.name,
                    avatar = bot.avatar,
                    clueTextEn = enPool[pick],
                    clueTextHi = hiPool[pick % hiPool.size],
                    isSuspicious = makesSlip
                )
            } else {
                val pick = (roundIndex + idx) % word.crewCluesEn.size
                BotClue(
                    slotId = bot.slotId,
                    playerName = bot.name,
                    avatar = bot.avatar,
                    clueTextEn = word.crewCluesEn[pick],
                    clueTextHi = word.crewCluesHi[pick % word.crewCluesHi.size],
                    isSuspicious = false
                )
            }
        }
    }

    fun castSmartBotVotes(
        voters: List<PlayerSlot>,
        candidates: List<PlayerSlot>,
        clues: List<BotClue>,
        difficulty: DifficultyLevel
    ): Map<String, String> {
        if (candidates.isEmpty()) return emptyMap()
        val suspiciousIds = clues.filter { it.isSuspicious }.map { it.slotId }.toSet()
        val imposterIds = candidates.filter { it.isImposter }.map { it.slotId }.toSet()

        val accuracy = when (difficulty) {
            DifficultyLevel.EASY -> 0.42f
            DifficultyLevel.MEDIUM -> 0.64f
            DifficultyLevel.HARD -> 0.80f
            DifficultyLevel.MIXED -> 0.64f
        }

        val votes = mutableMapOf<String, String>()
        for (bot in voters) {
            if (!bot.isBot || bot.isEliminated) continue
            val validTargets = candidates.filter { it.slotId != bot.slotId }
            if (validTargets.isEmpty()) continue

            val chosenTarget = if (bot.isImposter) {
                val nonImposterTargets = validTargets.filter { it.slotId !in imposterIds }
                (nonImposterTargets.ifEmpty { validTargets }).random()
            } else {
                val suspiciousValid = validTargets.filter { it.slotId in suspiciousIds }
                val imposterValid = validTargets.filter { it.slotId in imposterIds }
                when {
                    suspiciousValid.isNotEmpty() && Random.nextFloat() < (accuracy + 0.15f) ->
                        suspiciousValid.random()
                    imposterValid.isNotEmpty() && Random.nextFloat() < accuracy ->
                        imposterValid.random()
                    else -> validTargets.random()
                }
            }
            votes[bot.slotId] = chosenTarget.slotId
        }
        return votes
    }
}
