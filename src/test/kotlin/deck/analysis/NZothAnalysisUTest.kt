package deck.analysis

import com.github.mcaustin.deck.DeckCodeBuilder
import com.github.mcaustin.deck.analysis.NZothAnalysis
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class NZothAnalysisUTest {

    private val nZothAnalysis = NZothAnalysis()
    private val deckCodeBuilder = DeckCodeBuilder()

    @Test
    fun testRenoMage() {
        val deckCode = "AAEBAf0EHsABxQT3DfoOwxaFF9i7AsrDAt/EAsPqAs7vAsb4AqCAA72ZA5+bA8KhA/yjA5KkA7+kA/CvA5GxA4S2A4y2A+G2A+DMA+XRA5LkA53uA6bvA7CKBAAA";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = nZothAnalysis.analyze(deck)

        Assertions.assertThat(results.warning).isFalse //No N'Zoth
    }

    @Test
    fun testMenagerieWith5Types() {
        val deckCode = "AAEBAZ8FHpXNA90K4+sD2f4CzOsDyaAE4NEDysEDoaAE/LgD+d4Dg6ED/KMDk9AD7Q+VpgOW6AP76AOanwSR7AP03wO/0QON4QP6DoXeA8MWucECpu8DkbEDm9gDAAA=";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = nZothAnalysis.analyze(deck)

        Assertions.assertThat(results.warning).isFalse //No N'Zoth
    }

    @Test
    fun testMenagerieWith2Types() {
        val deckCode = "AAEBAZ8FHrdslc0D3Qrj6wPZ/gLM6wO/AaugBMmgBODRA8rBA6GgBPy4A7WfBPneA4OhA5PQA6cI7Q/76AOanwSR7APfFL/RA/oOhd4Dwxa5wQKm7wOb2AMAAA==";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = nZothAnalysis.analyze(deck)

        Assertions.assertThat(results.warning).isTrue //Only murlocs
        Assertions.assertThat(results.message).isNotBlank //Only murlocs
    }

    @Test
    fun testDeathRattleNZoth() {
        val deckCode = "AAEBAQcGkAf7DPgR4KwCxfMCkvgCDP4NgQ7GwwLfxALMzQLP5wKb8wL09QKD+wKfnwSIoASJoAQA";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = nZothAnalysis.analyze(deck)

        Assertions.assertThat(results.warning).isFalse //3 death rattles (2 duplicates)
    }

    @Test
    fun `testDeathRattleNZoth with No deathrattles`() {
        val deckCode = "AAEBAQcGhrAC+BH7DJAHkvgC4KwCDLrOAuLMA4P7AomgBN/EAs/nAp+fBMbDApvzAvT1AszNAoigBAA=";
        val deck = deckCodeBuilder.buildDeck(deckCode)

        val results = nZothAnalysis.analyze(deck)

        Assertions.assertThat(results.warning).isTrue //No death rattles
    }

}
