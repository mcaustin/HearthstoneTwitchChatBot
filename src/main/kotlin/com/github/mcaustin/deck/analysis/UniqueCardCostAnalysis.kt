package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.Card
import com.github.mcaustin.deck.CardDbfIds.KAZAKUS_GOLEM_SHAPER
import com.github.mcaustin.deck.CardDbfIds.PRINCE_KELESETH
import com.github.mcaustin.deck.CardDbfIds.PRINCE_TALDARAM
import com.github.mcaustin.deck.CardDbfIds.PRINCE_VALANAR
import com.github.mcaustin.deck.Deck

/**
 * Certain cards must be the only x-cost card to work.
 */
class UniqueCardCostAnalysis : DeckAnalyzer {

    private val uniqueCardCostCardDbfIdsCostMap = mapOf(
        Pair(PRINCE_VALANAR, 4),
        Pair(KAZAKUS_GOLEM_SHAPER, 4),
        Pair(PRINCE_KELESETH, 2),
        Pair(PRINCE_TALDARAM, 3)
    )

    override fun analyze(deck: Deck): AnalysisResult? {

        val cardsToCheck = uniqueCardCostCardDbfIdsCostMap.filter { uniqueCostPair ->
            deck.cards.any { it.first.dbfId == uniqueCostPair.key }
        }

        cardsToCheck.forEach { uniqueCardPair ->
            val costCheckValue = uniqueCardPair.value

            val uniqueCard = deck.cards.map { it.first }.first { uniqueCardPair.key == it.dbfId }
            val cardsWithCost = deck.cards.filter { it.first.cost == costCheckValue }.size

            if (cardsWithCost > 1) {
                return AnalysisResult(
                    "***Warning*** Deck contains ${uniqueCard.name} and $cardsWithCost cards with cost $costCheckValue",
                    true
                )
            }
        }

        return null
    }
}
