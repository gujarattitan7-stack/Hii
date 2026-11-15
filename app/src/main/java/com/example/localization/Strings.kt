package com.example.localization

import com.example.domain.AccessoryStyle
import com.example.domain.AppLanguage
import com.example.domain.CategoryId
import com.example.domain.DifficultyLevel
import com.example.domain.GameModeType
import com.example.domain.GlassesStyle
import com.example.domain.GraphicsQuality
import com.example.domain.HairStyle
import com.example.domain.HatStyle
import com.example.domain.OutfitStyle

data class AppStrings(
    val appTitle: String,
    val tagline: String,
    val offlineBadge: String,
    val playOffline: String,
    val playerProfileBtn: String,
    val achievementsBtn: String,
    val avatarCustomBtn: String,
    val howToPlayBtn: String,
    val settingsBtn: String,
    val languageBtn: String,

    // Language Screen
    val languageScreenTitle: String,
    val nextBtn: String,
    val backBtn: String,

    // Player Selection
    val playerSelectionTitle: String,
    val selectPlayersLabel: String,
    val impostersLabel: String,
    val practiceHintBadge: String,
    val playerNameHint: String,
    val botTag: String,
    val humanTag: String,
    val hostTag: String,

    // Avatar Customization
    val avatarCustomTitle: String,
    val hairStyleLabel: String,
    val glassesLabel: String,
    val hatLabel: String,
    val outfitLabel: String,
    val accessoryLabel: String,
    val colorLabel: String,
    val presetAvatarLabel: String,
    val resetBtn: String,
    val saveBtn: String,

    // Category Selection
    val categorySelectionTitle: String,
    val categoryMixHint: String,
    val selectAllBtn: String,
    val difficultyLabel: String,
    val skipWordToggle: String,
    val browseWordsBtn: String,

    // Game Mode Selection
    val gameModeTitle: String,
    val modeClassicTitle: String,
    val modeClassicSub: String,
    val modePracticeTitle: String,
    val modePracticeSub: String,
    val modeSuddenDeathTitle: String,
    val modeSuddenDeathSub: String,
    val modeChallengeTitle: String,
    val modeChallengeSub: String,
    val modeCustomTitle: String,
    val modeCustomSub: String,
    val discussionTimerLabel: String,
    val votingTimerLabel: String,
    val imposterHintsLabel: String,
    val secretMissionsLabel: String,
    val challengeWheelLabel: String,

    // Game Rules & Lobby
    val gameRulesTitle: String,
    val rule1: String,
    val rule2: String,
    val rule3: String,
    val rule4: String,
    val startGameBtn: String,
    val gameLobbyTitle: String,
    val playersCountFormat: String,
    val roomBadgePrefix: String,
    val spinWheelBtn: String,
    val spinningLabel: String,
    val wheelTitle: String,
    val continueBtn: String,

    // Secret Card & Word Reveal
    val secretCardTitle: String,
    val secretWordRevealTitle: String,
    val playerPrefix: String,
    val revealCardBtn: String,
    val hideCardBtn: String,
    val yourSecretWordLabel: String,
    val youAreImposterTitle: String,
    val youAreImposterSub: String,
    val categoryPrefix: String,
    val hintPrefix: String,
    val secretMissionPrefix: String,
    val nextPlayerBtn: String,
    val startDiscussionBtn: String,
    val passPhoneTitle: String,
    val passPhoneSub: String,
    val skipWordBtn: String,

    // Discussion & Voting & Results
    val discussionTitle: String,
    val discussPrompt: String,
    val botCluesTitle: String,
    val moreCluesBtn: String,
    val useHintBtn: String,
    val startVotingBtn: String,
    val votingTitle: String,
    val selectSuspectLabel: String,
    val voteBtn: String,
    val onlyOnceNote: String,
    val unlockBallotBtn: String,
    val passBallotTo: String,
    val votingResultsTitle: String,
    val eliminatedPrefix: String,
    val tieBreakerAlert: String,
    val imposterRevealTitle: String,
    val wasTheImposter: String,
    val wasNotImposter: String,
    val secretWordPrefix: String,
    val winnerScreenTitle: String,
    val crewWinsHeadline: String,
    val imposterWinsHeadline: String,
    val wellPlayedSub: String,
    val playAgainBtn: String,
    val mainMenuBtn: String,

    // Profile, Achievements, Settings, How to Play
    val levelLabel: String,
    val winsLabel: String,
    val lossesLabel: String,
    val impWinsLabel: String,
    val crewWinsLabel: String,
    val historyBtn: String,
    val gameHistoryTitle: String,
    val createProfileBtn: String,
    val achievementsTitle: String,
    val unlockedBadge: String,
    val settingsTitle: String,
    val musicToggle: String,
    val sfxToggle: String,
    val vibrationToggle: String,
    val graphicsQualityLabel: String,
    val reduceMotionToggle: String,
    val howToPlayTitle: String,
    val step1Title: String,
    val step1Desc: String,
    val step2Title: String,
    val step2Desc: String,
    val step3Title: String,
    val step3Desc: String,
    val step4Title: String,
    val step4Desc: String,
    val localWordsTitle: String,
    val emptyHistoryMsg: String,
) {
    fun categoryName(cat: CategoryId, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> when (cat) {
            CategoryId.CINEMA_ACTORS -> "Cinema Actors"
            CategoryId.FOOD -> "Food"
            CategoryId.SWEET_DISHES -> "Sweet Dishes"
            CategoryId.CRICKET_PLAYERS -> "Cricket Players"
            CategoryId.ANIMALS -> "Animals"
            CategoryId.COUNTRIES -> "Countries"
            CategoryId.VEHICLES -> "Vehicles"
            CategoryId.GAMES -> "Games"
            CategoryId.TECHNOLOGY -> "Technology"
            CategoryId.SUPERHEROES -> "Superheroes"
        }
        AppLanguage.HINDI -> when (cat) {
            CategoryId.CINEMA_ACTORS -> "सिनेमा कलाकार"
            CategoryId.FOOD -> "खाना"
            CategoryId.SWEET_DISHES -> "मिठाइयाँ"
            CategoryId.CRICKET_PLAYERS -> "क्रिकेट खिलाड़ी"
            CategoryId.ANIMALS -> "जानवर"
            CategoryId.COUNTRIES -> "देश"
            CategoryId.VEHICLES -> "वाहन"
            CategoryId.GAMES -> "खेल"
            CategoryId.TECHNOLOGY -> "टेक्नोलॉजी"
            CategoryId.SUPERHEROES -> "सुपरहीरो"
        }
    }

    fun difficultyName(diff: DifficultyLevel, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> when (diff) {
            DifficultyLevel.EASY -> "Easy"
            DifficultyLevel.MEDIUM -> "Medium"
            DifficultyLevel.HARD -> "Hard"
            DifficultyLevel.MIXED -> "Mixed"
        }
        AppLanguage.HINDI -> when (diff) {
            DifficultyLevel.EASY -> "आसान"
            DifficultyLevel.MEDIUM -> "मध्यम"
            DifficultyLevel.HARD -> "कठिन"
            DifficultyLevel.MIXED -> "मिश्रित"
        }
    }

    fun qualityName(q: GraphicsQuality, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> when (q) {
            GraphicsQuality.LOW -> "Low"
            GraphicsQuality.MEDIUM -> "Medium"
            GraphicsQuality.HIGH -> "High"
        }
        AppLanguage.HINDI -> when (q) {
            GraphicsQuality.LOW -> "लो"
            GraphicsQuality.MEDIUM -> "मीडियम"
            GraphicsQuality.HIGH -> "हाई"
        }
    }

    fun modeName(m: GameModeType, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> when (m) {
            GameModeType.CLASSIC -> modeClassicTitle
            GameModeType.PRACTICE -> modePracticeTitle
            GameModeType.SUDDEN_DEATH -> modeSuddenDeathTitle
            GameModeType.CHALLENGE -> modeChallengeTitle
            GameModeType.CUSTOM -> modeCustomTitle
        }
        AppLanguage.HINDI -> when (m) {
            GameModeType.CLASSIC -> modeClassicTitle
            GameModeType.PRACTICE -> modePracticeTitle
            GameModeType.SUDDEN_DEATH -> modeSuddenDeathTitle
            GameModeType.CHALLENGE -> modeChallengeTitle
            GameModeType.CUSTOM -> modeCustomTitle
        }
    }

    fun hairName(h: HairStyle, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> h.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
        AppLanguage.HINDI -> when (h) {
            HairStyle.SPIKY -> "स्पाइकी"
            HairStyle.PONYTAIL -> "पोनीटेल"
            HairStyle.SLEEK_LONG -> "लंबे बाल"
            HairStyle.SLEEK_SHORT -> "छोटे बाल"
            HairStyle.CURLY -> "घुंघराले"
            HairStyle.BOB -> "बॉब कट"
            HairStyle.SIDE_PART -> "साइड पार्ट"
            HairStyle.MOHAWK -> "मोहॉक"
            HairStyle.TWIN_BUNS -> "ट्विन बन्स"
        }
    }

    fun glassesName(g: GlassesStyle, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> g.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
        AppLanguage.HINDI -> when (g) {
            GlassesStyle.NONE -> "कोई नहीं"
            GlassesStyle.DARK_SHADES -> "डार्क शेड्स"
            GlassesStyle.ROUND_SPECS -> "गोल चश्मा"
            GlassesStyle.CYBER_VISOR -> "साइबर वाइज़र"
            GlassesStyle.MONOCLE -> "मोनोकल"
            GlassesStyle.STAR_FRAMES -> "स्टार फ्रेम"
        }
    }

    fun hatName(h: HatStyle, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> h.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
        AppLanguage.HINDI -> when (h) {
            HatStyle.NONE -> "कोई नहीं"
            HatStyle.CROWN -> "ताज"
            HatStyle.FEDORA -> "फेडोरा हैट"
            HatStyle.CAP -> "कैप"
            HatStyle.BEANIE -> "बीनी"
            HatStyle.HEADPHONES -> "हेडफ़ोन"
            HatStyle.HELMET -> "हेलमेट"
            HatStyle.HALO -> "हेलो रिंग"
        }
    }

    fun outfitName(o: OutfitStyle, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> o.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
        AppLanguage.HINDI -> when (o) {
            OutfitStyle.RED_HOODIE -> "रेड हुडी"
            OutfitStyle.PURPLE_JACKET -> "पर्पल जैकेट"
            OutfitStyle.DETECTIVE_COAT -> "जासूस कोट"
            OutfitStyle.YELLOW_TEE -> "येलो टी-शर्ट"
            OutfitStyle.CYBER_ARMOR -> "साइबर आर्मर"
            OutfitStyle.JERSEY -> "जर्सी"
            OutfitStyle.TUXEDO -> "टक्सीडो"
            OutfitStyle.NEON_TRACK -> "नियॉन ट्रैक"
        }
    }

    fun accessoryName(a: AccessoryStyle, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> a.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
        AppLanguage.HINDI -> when (a) {
            AccessoryStyle.NONE -> "कोई नहीं"
            AccessoryStyle.MAGNIFIER -> "मैग्निफायर"
            AccessoryStyle.MIC -> "माइक"
            AccessoryStyle.WATCH -> "स्मार्ट वॉच"
            AccessoryStyle.CAPE -> "हीरो केप"
            AccessoryStyle.STAR_BADGE -> "स्टार बैज"
        }
    }
}

private val EnglishStrings = AppStrings(
    appTitle = "IMPOSTER G",
    tagline = "Find the Imposter!",
    offlineBadge = "100% OFFLINE",
    playOffline = "Play Offline",
    playerProfileBtn = "Player Profile",
    achievementsBtn = "Achievements",
    avatarCustomBtn = "Avatar Customization",
    howToPlayBtn = "How to Play",
    settingsBtn = "Settings",
    languageBtn = "Language",

    languageScreenTitle = "Language Selection",
    nextBtn = "Next",
    backBtn = "Back",

    playerSelectionTitle = "Player Selection",
    selectPlayersLabel = "Select Players (1-10)",
    impostersLabel = "Imposters",
    practiceHintBadge = "1-2 Players auto-adds Practice Bots (3+ needed to play)",
    playerNameHint = "Player Name",
    botTag = "BOT",
    humanTag = "PLAYER",
    hostTag = "(Host)",

    avatarCustomTitle = "Avatar Customization",
    hairStyleLabel = "Hair Style",
    glassesLabel = "Glasses",
    hatLabel = "Hat",
    outfitLabel = "Outfit",
    accessoryLabel = "Accessories",
    colorLabel = "Color",
    presetAvatarLabel = "20 Base Avatars",
    resetBtn = "Reset",
    saveBtn = "Save",

    categorySelectionTitle = "Category Selection",
    categoryMixHint = "Tap multiple packs for Category Mix (100+ EN & HI words each)",
    selectAllBtn = "Select All",
    difficultyLabel = "Difficulty",
    skipWordToggle = "Allow Skip Word",
    browseWordsBtn = "Word Database",

    gameModeTitle = "Game Mode",
    modeClassicTitle = "Classic Mode",
    modeClassicSub = "Standard Pass & Play Deduction",
    modePracticeTitle = "Practice Mode",
    modePracticeSub = "1-2 Players vs Smart Offline Bots",
    modeSuddenDeathTitle = "Sudden Death",
    modeSuddenDeathSub = "Fast 30s Challenge & Tie-Breaker",
    modeChallengeTitle = "Challenge Mode",
    modeChallengeSub = "Random Wheel & Secret Missions",
    modeCustomTitle = "Custom Rules",
    modeCustomSub = "Customize Timers, Hints & Imposters",
    discussionTimerLabel = "Discussion Timer",
    votingTimerLabel = "Voting Timer",
    imposterHintsLabel = "Limited Imposter Hints",
    secretMissionsLabel = "Secret Missions",
    challengeWheelLabel = "Random Challenge Wheel",

    gameRulesTitle = "Game Rules",
    rule1 = "Find the Imposter",
    rule2 = "Give Clues",
    rule3 = "Vote Smart",
    rule4 = "Be the Winner!",
    startGameBtn = "Start Game",
    gameLobbyTitle = "Game Lobby",
    playersCountFormat = "Players",
    roomBadgePrefix = "Room",
    spinWheelBtn = "Spin Challenge Wheel",
    spinningLabel = "Spinning...",
    wheelTitle = "Random Challenge Wheel",
    continueBtn = "Continue",

    secretCardTitle = "Secret Card",
    secretWordRevealTitle = "Your Role",
    playerPrefix = "Player",
    revealCardBtn = "Reveal Card",
    hideCardBtn = "Hide Card",
    yourSecretWordLabel = "Your Secret Word",
    youAreImposterTitle = "YOU ARE THE IMPOSTER",
    youAreImposterSub = "Blend in! Give clever clues without knowing the secret word.",
    categoryPrefix = "Category",
    hintPrefix = "Limited Hint",
    secretMissionPrefix = "Secret Mission",
    nextPlayerBtn = "Next Player",
    startDiscussionBtn = "Start Discussion",
    passPhoneTitle = "Pass the Phone!",
    passPhoneSub = "Keep the secret hidden and hand the device to the next player.",
    skipWordBtn = "Skip Word",

    discussionTitle = "Discussion Phase",
    discussPrompt = "Discuss & Find the Imposter!",
    botCluesTitle = "Live Bot Clues",
    moreCluesBtn = "Next Clues",
    useHintBtn = "Use Hint",
    startVotingBtn = "Start Voting",
    votingTitle = "Voting Screen",
    selectSuspectLabel = "Select a Suspect",
    voteBtn = "Vote",
    onlyOnceNote = "(Only Once • No Self-Vote)",
    unlockBallotBtn = "Unlock Private Ballot",
    passBallotTo = "Private Vote For",
    votingResultsTitle = "Voting Results",
    eliminatedPrefix = "Eliminated",
    tieBreakerAlert = "Tie Detected! Triggering Tie-Breaker Vote!",
    imposterRevealTitle = "Imposter Reveal",
    wasTheImposter = "Was the Imposter!",
    wasNotImposter = "Was Innocent!",
    secretWordPrefix = "Secret Word",
    winnerScreenTitle = "Winner Screen",
    crewWinsHeadline = "Team Wins!",
    imposterWinsHeadline = "Imposter Wins!",
    wellPlayedSub = "Well Played!",
    playAgainBtn = "Play Again",
    mainMenuBtn = "Main Menu",

    levelLabel = "Level",
    winsLabel = "Wins",
    lossesLabel = "Losses",
    impWinsLabel = "Imp. Wins",
    crewWinsLabel = "Crew Wins",
    historyBtn = "History",
    gameHistoryTitle = "Game History",
    createProfileBtn = "+ New Profile",
    achievementsTitle = "Achievements",
    unlockedBadge = "Unlocked",
    settingsTitle = "Settings",
    musicToggle = "Music",
    sfxToggle = "Sound Effects",
    vibrationToggle = "Vibration",
    graphicsQualityLabel = "Graphics Quality",
    reduceMotionToggle = "Reduce Motion",
    howToPlayTitle = "How to Play",
    step1Title = "View Card",
    step1Desc = "Each player privately flips their 3D secret card. Crew sees the Secret Word; Imposters only see YOU ARE THE IMPOSTER.",
    step2Title = "Discuss",
    step2Desc = "Take turns giving one smart clue before the timer hits zero. Don't reveal the word directly!",
    step3Title = "Vote",
    step3Desc = "Cast one private vote for your top suspect. Self-voting and duplicate votes are blocked.",
    step4Title = "Find Imposter",
    step4Desc = "Unmask all Imposters to win XP, level up, and unlock 3D trophy badges!",
    localWordsTitle = "Offline Word Database",
    emptyHistoryMsg = "No games played yet. Finish a round to record history!"
)

private val HindiStrings = AppStrings(
    appTitle = "IMPOSTER G",
    tagline = "इम्पोस्टर को पहचानो!",
    offlineBadge = "100% ऑफ़लाइन",
    playOffline = "ऑफ़लाइन खेलें",
    playerProfileBtn = "खिलाड़ी प्रोफ़ाइल",
    achievementsBtn = "उपलब्धियाँ",
    avatarCustomBtn = "अवतार कस्टमाइज़",
    howToPlayBtn = "कैसे खेलें",
    settingsBtn = "सेटिंग्स",
    languageBtn = "भाषा",

    languageScreenTitle = "भाषा चुनें",
    nextBtn = "आगे बढ़ें",
    backBtn = "वापस",

    playerSelectionTitle = "खिलाड़ी चुनें",
    selectPlayersLabel = "खिलाड़ी चुनें (1-10)",
    impostersLabel = "इम्पोस्टर",
    practiceHintBadge = "1-2 खिलाड़ी पर प्रैक्टिस बॉट अपने-आप जुड़ेंगे (न्यूनतम 3 जरूरी)",
    playerNameHint = "खिलाड़ी का नाम",
    botTag = "बॉट",
    humanTag = "खिलाड़ी",
    hostTag = "(होस्ट)",

    avatarCustomTitle = "अवतार कस्टमाइज़ेशन",
    hairStyleLabel = "हेयर स्टाइल",
    glassesLabel = "चश्मा",
    hatLabel = "टोपी",
    outfitLabel = "आउटफिट",
    accessoryLabel = "एक्सेसरीज़",
    colorLabel = "रंग",
    presetAvatarLabel = "20 बेस अवतार",
    resetBtn = "रीसेट",
    saveBtn = "सेव करें",

    categorySelectionTitle = "श्रेणी चुनें",
    categoryMixHint = "मिक्स के लिए कई श्रेणियाँ चुनें (प्रत्येक में 100+ शब्द)",
    selectAllBtn = "सभी चुनें",
    difficultyLabel = "कठिनाई",
    skipWordToggle = "शब्द स्किप अनुमति",
    browseWordsBtn = "शब्द डेटाबेस",

    gameModeTitle = "गेम मोड",
    modeClassicTitle = "क्लासिक मोड",
    modeClassicSub = "स्टैंडर्ड पास-एंड-प्ले गेम",
    modePracticeTitle = "प्रैक्टिस मोड",
    modePracticeSub = "1-2 खिलाड़ी बनाम स्मार्ट बॉट्स",
    modeSuddenDeathTitle = "सडन डेथ",
    modeSuddenDeathSub = "तेज़ 30s चुनौती और टाई-ब्रेकर",
    modeChallengeTitle = "चैलेंज मोड",
    modeChallengeSub = "रैंडम व्हील और सीक्रेट मिशन",
    modeCustomTitle = "कस्टम नियम",
    modeCustomSub = "टाइमर, हिंट और इम्पोस्टर सेट करें",
    discussionTimerLabel = "चर्चा टाइमर",
    votingTimerLabel = "वोटिंग टाइमर",
    imposterHintsLabel = "सीमित इम्पोस्टर हिंट",
    secretMissionsLabel = "सीक्रेट मिशन",
    challengeWheelLabel = "रैंडम चैलेंज व्हील",

    gameRulesTitle = "गेम के नियम",
    rule1 = "इम्पोस्टर को खोजें",
    rule2 = "चतुर सुराग दें",
    rule3 = "समझदारी से वोट करें",
    rule4 = "विजेता बनें!",
    startGameBtn = "गेम शुरू करें",
    gameLobbyTitle = "गेम लॉबी",
    playersCountFormat = "खिलाड़ी",
    roomBadgePrefix = "रूम",
    spinWheelBtn = "चैलेंज व्हील घुमाएं",
    spinningLabel = "घूम रहा है...",
    wheelTitle = "रैंडम चैलेंज व्हील",
    continueBtn = "जारी रखें",

    secretCardTitle = "सीक्रेट कार्ड",
    secretWordRevealTitle = "आपकी भूमिका",
    playerPrefix = "खिलाड़ी",
    revealCardBtn = "कार्ड देखें",
    hideCardBtn = "कार्ड छुपाएं",
    yourSecretWordLabel = "आपका गुप्त शब्द",
    youAreImposterTitle = "आप इम्पोस्टर हैं!",
    youAreImposterSub = "घुल-मिल जाएं! बिना शब्द जाने समझदार सुराग दें।",
    categoryPrefix = "श्रेणी",
    hintPrefix = "सीमित हिंट",
    secretMissionPrefix = "सीक्रेट मिशन",
    nextPlayerBtn = "अगला खिलाड़ी",
    startDiscussionBtn = "चर्चा शुरू करें",
    passPhoneTitle = "फ़ोन आगे बढ़ाएं!",
    passPhoneSub = "अपना राज़ छुपाएं और फ़ोन अगले खिलाड़ी को दें।",
    skipWordBtn = "शब्द बदलें",

    discussionTitle = "चर्चा चरण",
    discussPrompt = "चर्चा करें और इम्पोस्टर खोजें!",
    botCluesTitle = "लाइव बॉट सुराग",
    moreCluesBtn = "अगले सुराग",
    useHintBtn = "हिंट देखें",
    startVotingBtn = "वोटिंग शुरू करें",
    votingTitle = "वोटिंग स्क्रीन",
    selectSuspectLabel = "संदिग्ध चुनें",
    voteBtn = "वोट करें",
    onlyOnceNote = "(केवल एक बार • खुद को वोट नहीं)",
    unlockBallotBtn = "निजी बैलेट खोलें",
    passBallotTo = "निजी वोट",
    votingResultsTitle = "वोटिंग परिणाम",
    eliminatedPrefix = "बाहर किए गए",
    tieBreakerAlert = "वोट बराबर! टाई-ब्रेकर शुरू!",
    imposterRevealTitle = "इम्पोस्टर खुलासा",
    wasTheImposter = "इम्पोस्टर था!",
    wasNotImposter = "निर्दोष था!",
    secretWordPrefix = "गुप्त शब्द",
    winnerScreenTitle = "विजेता स्क्रीन",
    crewWinsHeadline = "टीम की जीत!",
    imposterWinsHeadline = "इम्पोस्टर जीता!",
    wellPlayedSub = "शानदार खेल!",
    playAgainBtn = "फिर से खेलें",
    mainMenuBtn = "मुख्य मेनू",

    levelLabel = "लेवल",
    winsLabel = "जीत",
    lossesLabel = "हार",
    impWinsLabel = "इम्पोस्टर जीत",
    crewWinsLabel = "क्रू जीत",
    historyBtn = "इतिहास",
    gameHistoryTitle = "गेम इतिहास",
    createProfileBtn = "+ नई प्रोफ़ाइल",
    achievementsTitle = "उपलब्धियाँ",
    unlockedBadge = "अनलॉक",
    settingsTitle = "सेटिंग्स",
    musicToggle = "संगीत",
    sfxToggle = "साउंड इफ़ेक्ट्स",
    vibrationToggle = "वाइब्रेशन",
    graphicsQualityLabel = "ग्राफ़िक्स क्वालिटी",
    reduceMotionToggle = "एनीमेशन कम करें",
    howToPlayTitle = "कैसे खेलें",
    step1Title = "कार्ड देखें",
    step1Desc = "हर खिलाड़ी गुप्त रूप से अपना 3D कार्ड पलटता है। टीम को गुप्त शब्द दिखता है और इम्पोस्टर को केवल चेतावनी मिलती है।",
    step2Title = "चर्चा करें",
    step2Desc = "समय खत्म होने से पहले बारी-बारी से एक स्मार्ट सुराग दें। शब्द सीधे न बोलें!",
    step3Title = "वोट करें",
    step3Desc = "संदिग्ध को एक निजी वोट दें। खुद को वोट और दोहरे वोट की अनुमति नहीं है।",
    step4Title = "इम्पोस्टर पकड़ें",
    step4Desc = "सभी इम्पोस्टर पकड़कर XP पाएं, लेवल बढ़ाएं और ट्रॉफी बैज अनलॉक करें!",
    localWordsTitle = "ऑफ़लाइन शब्द डेटाबेस",
    emptyHistoryMsg = "अभी तक कोई गेम नहीं खेला गया। इतिहास देखने के लिए एक राउंड पूरा करें!"
)

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.ENGLISH -> EnglishStrings
    AppLanguage.HINDI -> HindiStrings
}
