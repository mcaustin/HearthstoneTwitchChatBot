package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.RuntimeException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class AnalyzeCommand(
    private val twirk: Twirk
    ): CommandExecutor {

    private val logger = LogManager.getLogger(this.javaClass)

    private val client: HttpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build()

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        logger.info("Sending Request")

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.d0nkey.top/streamer-decks?format=1&twitch_id=156646165"))
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        logger.info("Got response code:${response.statusCode()}")
        try {
            val document = Jsoup.parse(response.body())
            logger.info("Parsed response to html")
            val tableBody = document.getElementsByTag("tbody").first()

            val rows = tableBody.getElementsByTag("tr")

            val lastPlayedGame = collectResults(rows.first())

            var winStreak = 0
            var wonLastGame: Boolean
            for (i in 0 until rows.size-1) {
                val currentGame = collectResults(rows[i])
                val previousGame = collectResults(rows[i+1])
                wonLastGame = currentGame.legendRank.toInt() < previousGame.legendRank.toInt()
                if (wonLastGame) {
                    winStreak++
                } else {
                    break
                }
            }

            val messageBuilder = StringBuilder("Hi ${sender?.displayName ?: "there"}, Keith was last playing ${lastPlayedGame.heroClass}")
            messageBuilder.append(" at rank ${lastPlayedGame.legendRank}. Deck Code: ${lastPlayedGame.deckCode}")

            if (winStreak > 0) {
                messageBuilder.append("  Keith is currently on a $winStreak-game win streak!")
            }
            logger.info("Send channel message: $messageBuilder")
            twirk.channelMessage(messageBuilder.toString())
        } catch (e: RuntimeException) {
            logger.error("couldn't process request", e)
        }



    }

    private fun collectResults(row: Element): DonkeyResults {
        val deckCode = row.getElementsByAttribute("phx-value-deckcode").first().attr("phx-value-deckcode")
        val heroClass = row.getElementsByClass("deck-text").first().children().first().children()[1].text()
        val legendRank = row.getElementsByClass("tag legend-rank")[1].text()
        return DonkeyResults(
            deckCode, heroClass, legendRank
        )
    }

    data class DonkeyResults(
        val deckCode: String,
        val heroClass: String,
        val legendRank: String
    )

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) =
        message?.content?.equals("!spy") == true
}
