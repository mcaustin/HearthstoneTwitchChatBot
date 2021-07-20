package twitch.commands

import com.gikk.twirk.TwirkBuilder
import com.github.mcaustin.twitch.commands.PlayStatsCommand
import org.junit.jupiter.api.Test

class AnalyzeCommandTest {

    private val analyzeCommand = PlayStatsCommand(TwirkBuilder("","","").build(), donkeyHarvester)

    @Test
    fun testAnalyze() {
        analyzeCommand.executeCommand(null, null)
    }
}
