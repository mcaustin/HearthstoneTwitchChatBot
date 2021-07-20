package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.twitch.DonkeyHarvester
import org.apache.logging.log4j.LogManager
import java.text.DecimalFormat

class PlayStatsCommand(
    private val twirk: Twirk,
    donkeyHarvester: DonkeyHarvester
): CommandExecutor, DonkeyHarvester.NewGameConsumer {

    private val logger = LogManager.getLogger(this.javaClass)
    private var games: List<DonkeyHarvester.DonkeyGame>? = null

    init {
        donkeyHarvester.registerCallback(this)
    }

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        logger.info("Sending Request")
        try {
            val lastPlayedGame = games?.first() ?: return

            val winStreak = winStreakCalculator(games!!)
            val winStats = winPercentageCalculator(games!!)
            val decimalFormat = DecimalFormat("#00.0")
            val pct = decimalFormat.format(winStats.first.toDouble()/winStats.second.toDouble() * 100)

            val messageBuilder = StringBuilder("Hi ${sender?.displayName ?: "there"}, Keith was last playing ${lastPlayedGame.heroClass}")
            messageBuilder.append(" at rank ${lastPlayedGame.legendRank}.")
            messageBuilder.append(" Win Rate: ${winStats.first}/${winStats.second} ($pct%)")

            if (winStreak > 0) {
                messageBuilder.append("  Keith is currently on a $winStreak-game win streak!")
            }
            logger.info("Send channel message: $messageBuilder")
            twirk.channelMessage(messageBuilder.toString())
        } catch (e: Exception) {
            logger.error("couldn't process request", e)
        }
    }

    private fun winPercentageCalculator(
        games: List<DonkeyHarvester.DonkeyGame>
    ): Pair<Int, Int> {
        var gamesWon = 0
        var totalGames = 0
        for (i in 0 until games.size - 1) {
            val currentGame = games[i]
            val previousGame = games[i + 1]
            val wonLastGame = currentGame.legendRank.toInt() < previousGame.legendRank.toInt()
            if (wonLastGame) {
                gamesWon++
            }
            totalGames++
        }
        return Pair(gamesWon, totalGames)
    }

    private fun winStreakCalculator(
        games: List<DonkeyHarvester.DonkeyGame>
    ): Int {
        var wonLastGame: Boolean
        var winStreak = 0
        for (i in 0 until games.size - 1) {
            val currentGame = games[i]
            val previousGame = games[i + 1]
            wonLastGame = currentGame.legendRank.toInt() < previousGame.legendRank.toInt()
            if (wonLastGame) {
                winStreak++
            } else {
                break
            }
        }
        return winStreak
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) =
        message?.content?.equals("!playstats") == true

    override fun newGamesFound(games: List<DonkeyHarvester.DonkeyGame>) {
        this.games = games
    }
}
