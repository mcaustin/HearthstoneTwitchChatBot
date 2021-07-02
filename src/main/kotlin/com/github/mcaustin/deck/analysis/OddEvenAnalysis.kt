package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.CardDbfIds.BAKU_THE_MOONEATER
import com.github.mcaustin.deck.CardDbfIds.GENN_GREYMANE
import com.github.mcaustin.deck.Deck

class OddEvenAnalysis: DeckAnalyzer {

    override fun analyze(deck: Deck): String? {
        if (oddCardCheck(deck)) return "Warning! Even cards found in a Baku Deck"
        if (evenCardCheck(deck)) return "Warning! Odd cards found in a Genn Deck"

        return null
    }

    private fun oddCardCheck(deck: Deck): Boolean {
        val containsBaku = deck.cards.any {
            it.first.dbfId == BAKU_THE_MOONEATER
        }

        if (containsBaku) {
            val evenCards = deck.cards.filter {
                it.first.cost?.rem(2) == 0
            }
            if (evenCards.isNotEmpty()) {
                return true
            }
        }
        return false
    }

    private fun evenCardCheck(deck: Deck): Boolean {
        val containsGenn = deck.cards.any {
            it.first.dbfId == GENN_GREYMANE
        }

        if (containsGenn) {
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
