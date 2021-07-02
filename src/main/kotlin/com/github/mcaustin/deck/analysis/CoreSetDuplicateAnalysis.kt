package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.Card
import com.github.mcaustin.deck.Deck

/**
 * Checks for card duplication in the core set / other sets
 */
class CoreSetDuplicateAnalysis : DeckAnalyzer {

    override fun analyze(deck: Deck): String? {

        val coreCards = deck.cards
            .filter { it.second == 1 }
            .filter {
                it.first.set.equals("CORE")
            }.map { it.first }

        val singleNonCoreCards = deck.cards
            .filter { it.second == 1 }
            .map { it.first }
            .filter { !it.set.equals("CORE") }

        coreCards.forEach {
            val nonCoreDuplicate = findNonCoreDuplicate(it, singleNonCoreCards)

            if (nonCoreDuplicate != null) {
                return "2 copies of card [${it.name}] found in sets [${it.set}] and [${nonCoreDuplicate.set}]"
            }
        }

        return null
    }

    private fun findNonCoreDuplicate(coreCard: Card, cards: List<Card>): Card? {
        val nonCoreId = coreCard.id?.removePrefix("CORE_")

        return cards.firstOrNull {
            it.id.equals(nonCoreId)
        }
    }
}
