package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.CardDbfIds.NZOTH_DEATHRATTLE
import com.github.mcaustin.deck.CardDbfIds.NZOTH_MENAGERIE
import com.github.mcaustin.deck.Deck
import java.lang.StringBuilder

/**
 * There are 2 NZoths - menagerie and deathrattle, check both
 */
class NZothAnalysis: DeckAnalyzer {

    override fun analyze(deck: Deck): AnalysisResult {
        val messageBuilder = StringBuilder()
        var warning = false

        if (containsDeathRattleNZoth(deck)) {
            val deathRattleCount = countDeathRattles(deck)
            if (deathRattleCount < 1) {
                messageBuilder.append(" Deck contains NZoth, but no deathrattles")
                warning = true
            }
        }

        if (containsMenagerieNZoth(deck)) {
            val tribeCount = countTribes(deck)

            if (tribeCount < 3) {
                messageBuilder.append(" Deck contains N'Zoth, but only $tribeCount minion types")
                warning = true
            }
        }
        if (warning) {
            messageBuilder.insert(0, "***Warning***")
        }
        return AnalysisResult(messageBuilder.toString(), warning)
    }

    private fun countDeathRattles(deck: Deck) = deck.cards
        .filter { it.first.type == "MINION" }
        .count { it.first.mechanics?.contains("DEATHRATTLE") == true }

    private fun countTribes(deck: Deck) = deck.cards.map { it.first }.mapNotNull { it.race }.toSet().size


    private fun containsDeathRattleNZoth(deck: Deck) = deck.cards.any { it.first.dbfId == NZOTH_DEATHRATTLE }
    private fun containsMenagerieNZoth(deck: Deck) = deck.cards.any { it.first.dbfId == NZOTH_MENAGERIE }
}
