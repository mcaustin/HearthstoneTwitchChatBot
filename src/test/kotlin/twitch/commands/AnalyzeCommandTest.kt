package twitch.commands

import com.gikk.twirk.TwirkBuilder
import com.github.mcaustin.twitch.commands.AnalyzeCommand
import org.junit.jupiter.api.Test

class AnalyzeCommandTest {

    private val analyzeCommand = AnalyzeCommand(TwirkBuilder("","","").build())

    @Test
    fun testAnalyze() {
        analyzeCommand.executeCommand(null, null)
    }
}
