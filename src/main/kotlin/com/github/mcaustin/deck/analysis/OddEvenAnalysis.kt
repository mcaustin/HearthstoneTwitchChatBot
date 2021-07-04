package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.Card
import com.github.mcaustin.deck.CardDbfIds.BAKU_THE_MOONEATER
import com.github.mcaustin.deck.CardDbfIds.CTHUN_SHATTERED
import com.github.mcaustin.deck.CardDbfIds.GENN_GREYMANE
import com.github.mcaustin.deck.CardDbfIds.PRINCE_MALCHEZAAR
import com.github.mcaustin.deck.Deck

class OddEvenAnalysis: DeckAnalyzer {

    override fun analyze(deck: Deck): AnalysisResult? {
        if (oddCardCheck(deck)) return AnalysisResult("***Warning*** Even cards found in a Baku Deck", true)
        if (evenCardCheck(deck)) return AnalysisResult("***Warning*** Odd cards found in a Genn Deck", true)

        if (containsBaku(deck)) {
            val evenShufflers = shufflesEvenCards(deck)
            if (evenShufflers.isNotEmpty()) {
                return AnalysisResult("***Warning*** Odd deck may shuffle odd cards in due to: ${evenShufflers.first().name}", true)
            }
        }

        if (containsGenn(deck)) {
            val oddShufflers = shufflesOddCards(deck);
            if (oddShufflers.isNotEmpty()) {
                return AnalysisResult("***Warning*** Even deck may shuffle odd cards in due to: ${oddShufflers.first().name}", true)
            }
        }

        return null
    }

    private fun oddCardCheck(deck: Deck): Boolean {
        if (containsBaku(deck)) {
            val evenCards = deck.cards.filter {
                it.first.cost?.rem(2) == 0
            }
            if (evenCards.isNotEmpty()) {
                return true
            }
        }
        return false
    }

    private fun shufflesOddCards(deck: Deck): List<Card> {
        return deck.cards.map { it.first }
            .filter {
                it.dbfId == CTHUN_SHATTERED
            }
    }

    private fun shufflesEvenCards(deck: Deck): List<Card> {
        return deck.cards.map { it.first }
            .filter {
                it.dbfId == PRINCE_MALCHEZAAR
            }
    }

    private fun containsGenn(deck: Deck) = deck.cards.any { it.first.dbfId == GENN_GREYMANE }
    private fun containsBaku(deck: Deck) = deck.cards.any { it.first.dbfId == BAKU_THE_MOONEATER }

    private fun evenCardCheck(deck: Deck): Boolean {
        if (containsGenn(deck)) {
            val oddCards = deck.cards.filter {
                it.first.cost?.rem(2) == 1
            }
            if (oddCards.isNotEmpty()) {
                return true
            }
        }
        return false
    }


}
