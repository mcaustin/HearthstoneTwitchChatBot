package com.github.mcaustin.deck.analysis

import com.github.mcaustin.deck.Deck

interface DeckAnalyzer {

    /**
     * Analyze a deck and provide an optional message
     */
    fun analyze(deck: Deck): AnalysisResult?
}
