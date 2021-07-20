package twitch

import com.github.mcaustin.twitch.DonkeyHarvester
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import java.time.Duration

class DonkeyHarvesterTest: DonkeyHarvester.NewGameConsumer {

    private val donkeyHarvester = DonkeyHarvester()
    private var lastRecordedGame: DonkeyHarvester.DonkeyGame? = null

    @Test
    fun testHarvest() {
        donkeyHarvester.startPolling()
        donkeyHarvester.registerCallback(this)
        while (true) {
            Thread.sleep(Duration.ofSeconds(10).toMillis())
            println("sleeping...")
        }
    }

    override fun newGamesFound(games: List<DonkeyHarvester.DonkeyGame>) {
        val newGame = games.first()
        if (newGame == lastRecordedGame) {
            println("Game is the same.")
        } else {
            lastRecordedGame = newGame
            println("Hero Class: ${lastRecordedGame!!.heroClass} Legend Rank: ${lastRecordedGame!!.legendRank}")
        }
    }
}
