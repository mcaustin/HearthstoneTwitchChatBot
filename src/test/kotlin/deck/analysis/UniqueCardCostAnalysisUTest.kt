package deck.analysis

import com.github.mcaustin.deck.DeckCodeBuilder
import com.github.mcaustin.deck.analysis.NZothAnalysis
import com.github.mcaustin.deck.analysis.UniqueCardCostAnalysis
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UniqueCardCostAnalysisUTest {

    private val uniqueCardCostAnalysis = UniqueCardCostAnalysis()
    private val deckCodeBuilder = DeckCodeBuilder()

    @Test
    fun testManyNonUniqueCosts() {
        val deckCode = "AAEBAa0GDvzoA+0B494D494D7QGc4gKd4gKe4gL8E6eHA4nNAgmO7gLj6QII1s4D49ED64gD8qwD2cEC8u4Dm+sDoPcDAA==";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = uniqueCardCostAnalysis.analyze(deck)

        println(results?.message)
        Assertions.assertThat(results?.warning).isTrue //a lot of duplicate costs for all 4
    }

    @Test
    fun testPrince2() {
        val deckCode = "AAEBAa0GDu0B494D494D7QGc4gL8E6eHA4nNAgmO7gLj6QKO7gLj6QLIwAMI1s4D49ED64gD8qwD2cEC8u4Dm+sDoPcDAA==";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = uniqueCardCostAnalysis.analyze(deck)

        println(results?.message)
        Assertions.assertThat(results?.warning).isTrue
    }

    @Test
    fun testKazak() {
        val deckCode = "AAEBAa0GEO0B494D494D7QH8E6eHA4nNAgmO7gLj6QKO7gLj6QLIwAP86APL5gLXrAMH1s4D49ED64gD8qwD2cECm+sDoPcDAA==";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = uniqueCardCostAnalysis.analyze(deck)

        println(results?.message)
        Assertions.assertThat(results?.warning).isTrue
    }

}
