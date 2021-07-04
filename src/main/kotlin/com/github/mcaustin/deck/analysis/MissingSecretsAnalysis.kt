package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.Deck
import com.github.mcaustin.deck.Mechanics.SECRET

/**
 * When you have cards that work with secrets, but no actual secrets.
 */
class MissingSecretsAnalysis : DeckAnalyzer {

    override fun analyze(deck: Deck): AnalysisResult? {

        //Anti-secret tech
        val interactsWithEnemySecretCards = deck.cards.map { it.first }.filter { it.referencedTags?.contains(SECRET) ?: false }
            .filter {
                val filteredText = it.text?.replace("<b>","")
                filteredText?.contains("enemy Secret", true) == true
                    || filteredText?.contains("opponent's Secrets", true)  == true
            }

        //Find cards that work with secrets, but take out anti-secret tech
        val secretSynergyCards = deck.cards.map { it.first }.filter { it.referencedTags?.contains(SECRET) ?: false }
            .filter { !interactsWithEnemySecretCards.contains(it) }

        if (secretSynergyCards.isNotEmpty()) {
            val secretCards = deck.cards.map { it.first }.filter { it.mechanics?.contains(SECRET) ?: false }

            if (secretCards.isEmpty()) {
                return AnalysisResult("***Warning*** Found ${secretSynergyCards.size} cards that use secrets" +
                    " (${secretSynergyCards.first().name}), but deck contains NO secrets.", true)
            }
        }
        return null
    }
}
