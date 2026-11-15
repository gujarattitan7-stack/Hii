package com.example.domain

enum class AppLanguage(val code: String, val displayLabel: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

enum class GraphicsQuality(val id: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    companion object {
        fun fromId(id: String): GraphicsQuality =
            entries.firstOrNull { it.id == id } ?: HIGH
    }
}

enum class GameModeType(val id: String) {
    CLASSIC("classic"),
    PRACTICE("practice"),
    SUDDEN_DEATH("sudden_death"),
    CHALLENGE("challenge"),
    CUSTOM("custom");

    companion object {
        fun fromId(id: String): GameModeType =
            entries.firstOrNull { it.id == id } ?: CLASSIC
    }
}

enum class DifficultyLevel {
    EASY,
    MEDIUM,
    HARD,
    MIXED
}

enum class CategoryId(val id: Int) {
    CINEMA_ACTORS(1),
    FOOD(2),
    SWEET_DISHES(3),
    CRICKET_PLAYERS(4),
    ANIMALS(5),
    COUNTRIES(6),
    VEHICLES(7),
    GAMES(8),
    TECHNOLOGY(9),
    SUPERHEROES(10);

    companion object {
        fun fromId(id: Int): CategoryId =
            entries.firstOrNull { it.id == id } ?: CINEMA_ACTORS
    }
}

// 20 Original 3D Cartoon Avatar Presets
enum class AvatarPreset(
    val id: String,
    val displayName: String,
    val isFeminine: Boolean,
    val defaultHair: HairStyle,
    val defaultGlasses: GlassesStyle,
    val defaultHat: HatStyle,
    val defaultOutfit: OutfitStyle,
    val defaultAccessory: AccessoryStyle,
    val defaultColorIndex: Int,
    val skinHex: Long,
    val hairHex: Long
) {
    LEO("leo", "Leo", false, HairStyle.SPIKY, GlassesStyle.NONE, HatStyle.NONE, OutfitStyle.RED_HOODIE, AccessoryStyle.NONE, 0, 0xFFF4C29F, 0xFF5A331E),
    RIYA("riya", "Riya", true, HairStyle.PONYTAIL, GlassesStyle.NONE, HatStyle.NONE, OutfitStyle.YELLOW_TEE, AccessoryStyle.NONE, 3, 0xFFF2BC97, 0xFF6B3E26),
    ARJUN("arjun", "Arjun", false, HairStyle.SIDE_PART, GlassesStyle.DARK_SHADES, HatStyle.NONE, OutfitStyle.DETECTIVE_COAT, AccessoryStyle.MAGNIFIER, 1, 0xFFE5A982, 0xFF231B18),
    NEHA("neha", "Neha", true, HairStyle.SLEEK_LONG, GlassesStyle.ROUND_SPECS, HatStyle.NONE, OutfitStyle.PURPLE_JACKET, AccessoryStyle.NONE, 2, 0xFFF5C6A5, 0xFF3A2319),
    GAMER_PRO("gamer_pro", "GamerPro", false, HairStyle.CURLY, GlassesStyle.ROUND_SPECS, HatStyle.HEADPHONES, OutfitStyle.NEON_TRACK, AccessoryStyle.MIC, 5, 0xFFF0B892, 0xFF2C1D14),
    ROHAN("rohan", "Rohan", false, HairStyle.SLEEK_SHORT, GlassesStyle.CYBER_VISOR, HatStyle.NONE, OutfitStyle.CYBER_ARMOR, AccessoryStyle.WATCH, 6, 0xFFD99A73, 0xFF1E1B18),
    ANANYA("ananya", "Ananya", true, HairStyle.SLEEK_LONG, GlassesStyle.NONE, HatStyle.CROWN, OutfitStyle.PURPLE_JACKET, AccessoryStyle.STAR_BADGE, 2, 0xFFF3C09A, 0xFF4A2B1D),
    KABIR("kabir", "Kabir", false, HairStyle.SPIKY, GlassesStyle.NONE, HatStyle.CAP, OutfitStyle.JERSEY, AccessoryStyle.WATCH, 4, 0xFFE3A47B, 0xFF211715),
    ZARA("zara", "Zara", true, HairStyle.BOB, GlassesStyle.NONE, HatStyle.HEADPHONES, OutfitStyle.RED_HOODIE, AccessoryStyle.MIC, 0, 0xFFF6CCB0, 0xFFC8D3E6),
    VIKRAM("vikram", "Vikram", false, HairStyle.MOHAWK, GlassesStyle.DARK_SHADES, HatStyle.NONE, OutfitStyle.CYBER_ARMOR, AccessoryStyle.CAPE, 6, 0xFFD7946B, 0xFF1F2937),
    TARA("tara", "Tara", true, HairStyle.TWIN_BUNS, GlassesStyle.STAR_FRAMES, HatStyle.NONE, OutfitStyle.NEON_TRACK, AccessoryStyle.STAR_BADGE, 7, 0xFFF1BD98, 0xFF7C3AED),
    DEV("dev", "Dev", false, HairStyle.SIDE_PART, GlassesStyle.MONOCLE, HatStyle.FEDORA, OutfitStyle.TUXEDO, AccessoryStyle.MAGNIFIER, 7, 0xFFE6AC87, 0xFF332018),
    MEERA("meera", "Meera", true, HairStyle.CURLY, GlassesStyle.ROUND_SPECS, HatStyle.BEANIE, OutfitStyle.YELLOW_TEE, AccessoryStyle.WATCH, 3, 0xFFEAB28C, 0xFF2F1B12),
    ISHAAN("ishaan", "Ishaan", false, HairStyle.CURLY, GlassesStyle.NONE, HatStyle.BEANIE, OutfitStyle.RED_HOODIE, AccessoryStyle.NONE, 0, 0xFFF2BE9B, 0xFF4B2D1F),
    KAVYA("kavya", "Kavya", true, HairStyle.PONYTAIL, GlassesStyle.CYBER_VISOR, HatStyle.NONE, OutfitStyle.CYBER_ARMOR, AccessoryStyle.CAPE, 1, 0xFFF5C6A5, 0xFF1E293B),
    SIDDHARTH("siddharth", "Sid", false, HairStyle.SLEEK_SHORT, GlassesStyle.NONE, HatStyle.HELMET, OutfitStyle.CYBER_ARMOR, AccessoryStyle.CAPE, 5, 0xFFE4A780, 0xFF281C16),
    NINA("nina", "Nina", true, HairStyle.BOB, GlassesStyle.DARK_SHADES, HatStyle.FEDORA, OutfitStyle.DETECTIVE_COAT, AccessoryStyle.MAGNIFIER, 2, 0xFFF4C4A0, 0xFF5B2C1B),
    AARAV("aarav", "Aarav", false, HairStyle.SPIKY, GlassesStyle.STAR_FRAMES, HatStyle.CROWN, OutfitStyle.TUXEDO, AccessoryStyle.STAR_BADGE, 3, 0xFFE8AD86, 0xFF241A15),
    MAYA("maya", "Maya", true, HairStyle.TWIN_BUNS, GlassesStyle.NONE, HatStyle.HALO, OutfitStyle.PURPLE_JACKET, AccessoryStyle.CAPE, 4, 0xFFF3C3A1, 0xFF9333EA),
    SAM("sam", "Sam", false, HairStyle.MOHAWK, GlassesStyle.CYBER_VISOR, HatStyle.CAP, OutfitStyle.JERSEY, AccessoryStyle.MIC, 6, 0xFFD99872, 0xFF0EA5E9);

    companion object {
        fun fromId(id: String): AvatarPreset =
            entries.firstOrNull { it.id == id } ?: LEO
    }
}

enum class HairStyle(val id: String) {
    SPIKY("spiky"),
    PONYTAIL("ponytail"),
    SLEEK_LONG("sleek_long"),
    SLEEK_SHORT("sleek_short"),
    CURLY("curly"),
    BOB("bob"),
    SIDE_PART("side_part"),
    MOHAWK("mohawk"),
    TWIN_BUNS("twin_buns");

    companion object {
        fun fromId(id: String): HairStyle = entries.firstOrNull { it.id == id } ?: SPIKY
    }
}

enum class GlassesStyle(val id: String) {
    NONE("none"),
    DARK_SHADES("dark_shades"),
    ROUND_SPECS("round_specs"),
    CYBER_VISOR("cyber_visor"),
    MONOCLE("monocle"),
    STAR_FRAMES("star_frames");

    companion object {
        fun fromId(id: String): GlassesStyle = entries.firstOrNull { it.id == id } ?: NONE
    }
}

enum class HatStyle(val id: String) {
    NONE("none"),
    CROWN("crown"),
    FEDORA("fedora"),
    CAP("cap"),
    BEANIE("beanie"),
    HEADPHONES("headphones"),
    HELMET("helmet"),
    HALO("halo");

    companion object {
        fun fromId(id: String): HatStyle = entries.firstOrNull { it.id == id } ?: NONE
    }
}

enum class OutfitStyle(val id: String) {
    RED_HOODIE("red_hoodie"),
    PURPLE_JACKET("purple_jacket"),
    DETECTIVE_COAT("detective_coat"),
    YELLOW_TEE("yellow_tee"),
    CYBER_ARMOR("cyber_armor"),
    JERSEY("jersey"),
    TUXEDO("tuxedo"),
    NEON_TRACK("neon_track");

    companion object {
        fun fromId(id: String): OutfitStyle = entries.firstOrNull { it.id == id } ?: RED_HOODIE
    }
}

enum class AccessoryStyle(val id: String) {
    NONE("none"),
    MAGNIFIER("magnifier"),
    MIC("mic"),
    WATCH("watch"),
    CAPE("cape"),
    STAR_BADGE("star_badge");

    companion object {
        fun fromId(id: String): AccessoryStyle = entries.firstOrNull { it.id == id } ?: NONE
    }
}

enum class CharacterPose {
    IDLE,
    POINTING,
    THUMBS_UP,
    WALKING,
    RUNNING,
    SITTING,
    VICTORY,
    DEFEAT,
    SURPRISE,
    THINKING,
    TALKING
}

data class AvatarConfig(
    val preset: AvatarPreset = AvatarPreset.LEO,
    val hairStyle: HairStyle = HairStyle.SPIKY,
    val glasses: GlassesStyle = GlassesStyle.NONE,
    val hat: HatStyle = HatStyle.NONE,
    val outfit: OutfitStyle = OutfitStyle.RED_HOODIE,
    val accessory: AccessoryStyle = AccessoryStyle.NONE,
    val colorIndex: Int = 0,
    val pedestalColorIndex: Int = 0
) {
    companion object {
        fun fromPreset(preset: AvatarPreset): AvatarConfig = AvatarConfig(
            preset = preset,
            hairStyle = preset.defaultHair,
            glasses = preset.defaultGlasses,
            hat = preset.defaultHat,
            outfit = preset.defaultOutfit,
            accessory = preset.defaultAccessory,
            colorIndex = preset.defaultColorIndex,
            pedestalColorIndex = preset.defaultColorIndex
        )
    }
}

data class PlayerProfile(
    val id: Long = 0,
    val name: String,
    val avatar: AvatarConfig = AvatarConfig(),
    val xp: Int = 0,
    val level: Int = 1,
    val gamesPlayed: Int = 0,
    val crewWins: Int = 0,
    val imposterWins: Int = 0,
    val losses: Int = 0,
    val correctVotes: Int = 0,
    val missionsCompleted: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalWins: Int get() = crewWins + imposterWins
    val winRatePercent: Int
        get() = if (gamesPlayed == 0) 0 else ((totalWins.toFloat() / gamesPlayed) * 100f).toInt()
    val xpPerLevel: Int get() = 1200
    val xpIntoCurrentLevel: Int get() = xp % xpPerLevel
}

data class WordEntry(
    val id: String,
    val category: CategoryId,
    val difficulty: DifficultyLevel,
    val wordEn: String,
    val wordHi: String,
    val hintEn: String,
    val hintHi: String,
    val crewCluesEn: List<String>,
    val crewCluesHi: List<String>,
    val bluffCluesEn: List<String>,
    val bluffCluesHi: List<String>
)

data class ChallengeModifier(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descEn: String,
    val descHi: String,
    val timeOverrideSeconds: Int? = null,
    val xpMultiplier: Float = 1.0f,
    val extraHintBlocked: Boolean = false
)

data class SecretMission(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val bonusXp: Int = 50
)

enum class BotPersonality {
    ANALYTICAL,
    BLUFFER,
    CAUTIOUS,
    BOLD
}

data class PlayerSlot(
    val slotId: String,
    val name: String,
    val isBot: Boolean,
    val isHost: Boolean = false,
    val profileId: Long? = null,
    val avatar: AvatarConfig = AvatarConfig(),
    val personality: BotPersonality = BotPersonality.ANALYTICAL,
    val isImposter: Boolean = false,
    val hasViewedCard: Boolean = false,
    val secretMission: SecretMission? = null,
    val missionCompleted: Boolean = false,
    val isEliminated: Boolean = false
)

data class BotClue(
    val slotId: String,
    val playerName: String,
    val avatar: AvatarConfig,
    val clueTextEn: String,
    val clueTextHi: String,
    val isSuspicious: Boolean = false
)

data class MatchConfig(
    val modeType: GameModeType = GameModeType.CLASSIC,
    val playerCount: Int = 4,
    val imposterCount: Int = 1,
    val randomImposterCount: Boolean = false,
    val difficulty: DifficultyLevel = DifficultyLevel.MIXED,
    val selectedCategories: Set<CategoryId> = CategoryId.entries.toSet(),
    val discussionSeconds: Int = 45,
    val votingSeconds: Int = 30,
    val hintsEnabled: Boolean = true,
    val maxHintsPerRound: Int = 1,
    val secretMissionsEnabled: Boolean = true,
    val challengeWheelEnabled: Boolean = false,
    val suddenDeathEnabled: Boolean = true,
    val skipWordEnabled: Boolean = true,
    val secretVotingEnabled: Boolean = true,
    val roomCode: String = "7F3KQ"
)

enum class WinnerSide {
    CREW,
    IMPOSTER
}

sealed interface GamePhase {
    data object Idle : GamePhase
    data object ChallengeWheelSpin : GamePhase
    data class SecretCardPass(
        val currentHumanIndex: Int,
        val isCardFlipped: Boolean = false,
        val cardRevealedOnce: Boolean = false,
        val isPassPhoneCurtain: Boolean = false
    ) : GamePhase
    data object Discussion : GamePhase
    data class Voting(
        val currentVoterIndex: Int = 0,
        val votesCast: Map<String, String> = emptyMap(),
        val isPrivacyShieldUp: Boolean = true,
        val secondsRemaining: Int = 30
    ) : GamePhase
    data class SuddenDeath(
        val tiedCandidateIds: List<String>,
        val currentVoterIndex: Int = 0,
        val votesCast: Map<String, String> = emptyMap(),
        val isPrivacyShieldUp: Boolean = true,
        val secondsRemaining: Int = 30
    ) : GamePhase
    data class VotingResults(
        val voteCounts: Map<String, Int>,
        val eliminatedPlayerId: String?,
        val isTie: Boolean,
        val tiedCandidateIds: List<String>,
        val winnerSide: WinnerSide,
        val votesCast: Map<String, String>
    ) : GamePhase
    data class ImposterReveal(
        val winnerSide: WinnerSide,
        val eliminatedPlayerId: String?,
        val voteCounts: Map<String, Int>,
        val xpEarnedTotal: Int,
        val newlyUnlockedAchievementIds: List<String>,
        val leveledUpProfileNames: List<String>
    ) : GamePhase
    data class WinnerCelebration(
        val winnerSide: WinnerSide,
        val eliminatedPlayerId: String?,
        val voteCounts: Map<String, Int>,
        val xpEarnedTotal: Int,
        val newlyUnlockedAchievementIds: List<String>,
        val leveledUpProfileNames: List<String>
    ) : GamePhase
}

data class ActiveMatchState(
    val config: MatchConfig = MatchConfig(),
    val players: List<PlayerSlot> = emptyList(),
    val currentWord: WordEntry? = null,
    val activeChallenge: ChallengeModifier? = null,
    val phase: GamePhase = GamePhase.Idle,
    val secondsRemaining: Int = 45,
    val isTimerRunning: Boolean = false,
    val skipsRemaining: Int = 2,
    val hintsRemaining: Int = 1,
    val hintRevealedForCrew: Boolean = false,
    val botClues: List<BotClue> = emptyList(),
    val suddenDeathOccurred: Boolean = false,
    val roundSaved: Boolean = false
)

data class AchievementDef(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descEn: String,
    val descHi: String,
    val target: Int,
    val iconKey: String
)

data class AchievementProgress(
    val def: AchievementDef,
    val progress: Int,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)

data class RoundHistoryItem(
    val id: Long,
    val timestamp: Long,
    val modeType: GameModeType,
    val categorySummary: String,
    val difficulty: DifficultyLevel,
    val secretWordEn: String,
    val secretWordHi: String,
    val imposterNames: String,
    val winnerSide: WinnerSide,
    val suddenDeathTriggered: Boolean,
    val challengeTitleEn: String?,
    val challengeTitleHi: String?,
    val xpAwarded: Int,
    val playerCount: Int
)
