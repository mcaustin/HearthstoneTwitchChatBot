package deck.analysis

import com.github.mcaustin.deck.DeckCodeBuilder
import com.github.mcaustin.deck.analysis.OddEvenAnalysis
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class OddEvenAnalysisUTest {

    private val oddEvenAnalysis = OddEvenAnalysis()
    private val deckCodeBuilder = DeckCodeBuilder()

    @Test
    fun testRenoMage() {
        val deckCode = "AAEBAf0EHsABxQT3DfoOwxaFF9i7AsrDAt/EAsPqAs7vAsb4AqCAA72ZA5+bA8KhA/yjA5KkA7+kA/CvA5GxA4S2A4y2A+G2A+DMA+XRA5LkA53uA6bvA7CKBAAA";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = oddEvenAnalysis.analyze(deck)

        Assertions.assertThat(results).isNull() // Not a odd / even deck
    }

    @Test
    fun `test normal Odd Rogue`() {
        val deckCode = "AAEBAaIHBq8E+g6RvAKL5QKe+ALz3QMM0gPUBZsVpu8CubgDqssDm80DiNADpNED99QDkp8ElJ8EAA==";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = oddEvenAnalysis.analyze(deck)

        Assertions.assertThat(results).isNull() // No problems
    }

    @Test
    fun `test Odd Rogue with even cards`() {
        val deckCode = "AAEBAaIHCJKfBIvlApG8AvPdA4DCAq8E+g6e+AILmxWqywOUnwTUBbm4A5vNA6TRA9IDpu8CiNAD99QDAA==";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = oddEvenAnalysis.analyze(deck)

        Assertions.assertThat(results).isNotNull
        Assertions.assertThat(results!!.warning).isEqualTo(true)
    }
}
