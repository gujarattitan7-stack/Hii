package com.example.data.words

import com.example.domain.AchievementDef
import com.example.domain.CategoryId
import com.example.domain.ChallengeModifier
import com.example.domain.DifficultyLevel
import com.example.domain.SecretMission
import com.example.domain.WordEntry

object WordBank {

    val allWords: List<WordEntry> by lazy {
        WordBankPart1.cinemaActors +
            WordBankPart1.food +
            WordBankPart1.sweetDishes +
            WordBankPart1.cricketPlayers +
            WordBankPart2.animals +
            WordBankPart2.countries +
            WordBankPart2.vehicles +
            WordBankPart3.games +
            WordBankPart3.technology +
            WordBankPart3.superheroes
    }

    val challengeModifiers: List<ChallengeModifier> = listOf(
        ChallengeModifier("ch_one_word", "One-Word Clues", "एक-शब्द सुराग", "Every player can only speak ONE word as their clue!", "हर खिलाड़ी केवल एक शब्द का सुराग बोल सकता है!"),
        ChallengeModifier("ch_speed", "Turbo 30s Clock", "टर्बो 30 सेकंड", "Discussion timer drops to a frantic 30 seconds — +30% XP!", "चर्चा का समय केवल 30 सेकंड — +30% XP!", timeOverrideSeconds = 30, xpMultiplier = 1.3f),
        ChallengeModifier("ch_no_hints", "Blackout Hints", "ब्लैकआउट हिंट", "Extra hints are locked this round — +25% XP!", "इस राउंड में अतिरिक्त हिंट बंद हैं — +25% XP!", xpMultiplier = 1.25f, extraHintBlocked = true),
        ChallengeModifier("ch_mime", "Silent Gesture", "मूक अभिनय सुराग", "First clue must include a 3-second hand gesture!", "पहले सुराग के साथ 3 सेकंड का इशारा जरूरी है!"),
        ChallengeModifier("ch_double_xp", "Golden Studio x2", "गोल्डन स्टूडियो x2", "Double XP awarded to all winners this round!", "इस राउंड के विजेताओं को दोगुना XP मिलेगा!", xpMultiplier = 2.0f),
        ChallengeModifier("ch_reverse", "Reverse Circle", "उल्टा घेरा", "Start clues from the last player moving counter-clockwise!", "आखिरी खिलाड़ी से उल्टी दिशा में सुराग शुरू करें!"),
        ChallengeModifier("ch_rhyme", "Rhyme Time", "तुकबंदी सुराग", "Try to rhyme your clue with another player's clue!", "अपना सुराग किसी अन्य खिलाड़ी के सुराग से तुक में दें!"),
        ChallengeModifier("ch_bluff_bonus", "High Stakes +50%", "हाई स्टेक्स +50%", "Sudden pressure — 1.5x XP for sharp detectives or slick imposters!", "कड़ा मुकाबला — सभी विजेताओं को 1.5x XP!", xpMultiplier = 1.5f)
    )

    val secretMissions: List<SecretMission> = listOf(
        SecretMission("ms_agree", "Agree enthusiastically with the player to your left", "अपने बाएं बैठे खिलाड़ी की बात से जोरदार सहमति जताएं", 50),
        SecretMission("ms_color", "Include any color name inside your clue", "अपने सुराग में किसी रंग का नाम शामिल करें", 55),
        SecretMission("ms_question", "Ask someone a dramatic follow-up question during briefing", "चर्चा में किसी से एक नाटकीय सवाल पूछें", 45),
        SecretMission("ms_calm", "Keep a straight poker face while everyone argues", "सबके बहस करने के दौरान बिल्कुल शांत चेहरा रखें", 50),
        SecretMission("ms_last_vote", "Defend at least one suspicious player before voting", "वोटिंग से पहले कम से कम एक संदिग्ध का बचाव करें", 60),
        SecretMission("ms_number", "Mention a number naturally in your clue or defense", "अपने सुराग या बचाव में स्वाभाविक रूप से एक संख्या बोलें", 50),
        SecretMission("ms_food", "Compare your clue to something tasty or colorful", "अपने सुराग की तुलना किसी स्वादिष्ट या रंगीन चीज़ से करें", 55),
        SecretMission("ms_accuse", "Point gently at two different suspects during discussion", "चर्चा के दौरान दो अलग-अलग लोगों पर संदेह जताएं", 50)
    )

    val achievements: List<AchievementDef> = listOf(
        AchievementDef("ach_first_win", "First Win", "पहली जीत", "Complete and win your very first round", "अपना पहला राउंड पूरा करें और जीतें", 1, "trophy"),
        AchievementDef("ach_imp_hunter", "Imposter Hunter", "इम्पोस्टर शिकारी", "Win 5 rounds on the Crew team", "क्रू टीम से 5 राउंड जीतें", 5, "shield"),
        AchievementDef("ach_imp_gamer", "Imposter Gamer", "इम्पोस्टर गेमर", "Win 3 rounds as the Imposter", "इम्पोस्टर बनकर 3 राउंड जीतें", 3, "mask"),
        AchievementDef("ach_perfect_name", "Perfect Name", "परफेक्ट नेम", "Customize and save your 3D avatar profile", "अपना 3D अवतार प्रोफ़ाइल कस्टमाइज़ और सेव करें", 1, "star"),
        AchievementDef("ach_game_master", "Game Master", "गेम मास्टर", "Play 10 total rounds offline", "ऑफ़लाइन 10 कुल राउंड खेलें", 10, "crown"),
        AchievementDef("ach_practice_3", "Bot Master", "बॉट मास्टर", "Finish 3 Practice Mode rounds", "3 प्रैक्टिस मोड राउंड पूरे करें", 3, "bot"),
        AchievementDef("ach_sudden_death", "Sudden Death Survivor", "सडन डेथ योद्धा", "Resolve a Sudden Death or Tie-Breaker round", "सडन डेथ या टाई-ब्रेकर राउंड पूरा करें", 1, "flame"),
        AchievementDef("ach_wheel_5", "Wheel Spinner", "व्हील स्पिनर", "Play 5 rounds with Challenge Wheel active", "चैलेंज व्हील के साथ 5 राउंड खेलें", 5, "wheel"),
        AchievementDef("ach_polyglot", "Bilingual Spy", "द्विभाषी जासूस", "Play rounds in both English and Hindi", "अंग्रेज़ी और हिन्दी दोनों में खेलें", 2, "globe"),
        AchievementDef("ach_full_house", "Full Party 10", "फुल पार्टी 10", "Play a match with 8 to 10 players", "8 से 10 खिलाड़ियों के साथ मैच खेलें", 1, "party")
    )

    fun wordsFor(categories: Set<CategoryId>, difficulty: DifficultyLevel): List<WordEntry> {
        val catFiltered = allWords.filter { it.category in categories }.ifEmpty { allWords }
        return if (difficulty == DifficultyLevel.MIXED) {
            catFiltered
        } else {
            catFiltered.filter { it.difficulty == difficulty }.ifEmpty { catFiltered }
        }
    }
}
