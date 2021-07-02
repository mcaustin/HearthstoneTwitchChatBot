package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.Card
import com.github.mcaustin.deck.CardDictionary
import com.github.mcaustin.deck.Deck
import java.lang.StringBuilder

/**
 * Check for decks that have cards that require no duplicates, and duplicates.
 */
class HighlanderAnalysis(private val cardDictionary: CardDictionary) : DeckAnalyzer {

    override fun analyze(deck: Deck): String {
        val duplicateSynergyCards = findCardsWithNonDuplicateSynergy(deck)
        val splitSetDuplicates = findSplitSetDuplicate(deck)
        val normalDuplicates = deck.cards.filter { it.second > 1 }.map { it.first }

        val analysisBuilder = StringBuilder()

        if (duplicateSynergyCards.isNotEmpty()) {
            analysisBuilder.append(" [contains ${duplicateSynergyCards.size} highlander cards] ")

            if (splitSetDuplicates.isNotEmpty() || normalDuplicates.isNotEmpty()) {
                analysisBuilder.append("***WARNING*** Deck has ${splitSetDuplicates.size + normalDuplicates.size} duplicate cards.")
            }
        } else {
            if (splitSetDuplicates.isEmpty() && normalDuplicates.isEmpty()) {
                analysisBuilder.append("***WARNING*** Deck has no duplicates, but no highlander synergy cards.")
            }
        }

        return analysisBuilder.toString()
    }

    private fun findCardsWithNonDuplicateSynergy(deck: Deck): List<Card> {
        val allSynergyCards =
            cardDictionary.cardMap.filter { it.value.text?.replace("\\n", "")?.contains("no duplicates") ?: false }
                .map { it.value.dbfId }

        return deck.cards.map { it.first }.filter {
            allSynergyCards.contains(it.dbfId)
        }
    }

    private fun findSplitSetDuplicate(deck: Deck): List<Card> {
        val coreCards = deck.cards
            .filter { it.second == 1 }
            .filter {
                it.first.set.equals("CORE")
            }.map { it.first }

        val singleNonCoreCards = deck.cards
            .filter { it.second == 1 }
            .map { it.first }
            .filter { !it.set.equals("CORE") }

        return coreCards.filter {
            findNonCoreDuplicate(it, singleNonCoreCards) != null
        }
    }

    private fun findNonCoreDuplicate(coreCard: Card, cards: List<Card>): Card? {
        val nonCoreId = coreCard.id?.removePrefix("CORE_")

        return cards.firstOrNull {
            it.id.equals(nonCoreId)
        }
    }
}
