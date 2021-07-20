package com.github.mcaustin.twitch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class DonkeyHarvester {

    private val logger = LogManager.getLogger(this.javaClass)
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    var currentStats: List<DonkeyGame>? = null

    private val callBackList: MutableList<NewGameConsumer> = mutableListOf()

    fun registerCallback(callBack: NewGameConsumer) {
        callBackList.add(callBack)
    }

    fun startPolling() {
        coroutineScope.launch {
            var lastGame: DonkeyGame? = null
            while (true) {
                pollDonkey()?.let {
                    val newGame = it.first()
                    if (lastGame != newGame) {
                        currentStats = it

                        val iterator = callBackList.listIterator()
                        for (consumer in iterator) {
                            consumer.newGamesFound(currentStats!!)
                        }
                    }
                    lastGame = newGame
                }
                delay(Duration.ofMinutes(1).toMillis())
            }
        }
    }

    private fun pollDonkey(): List<DonkeyGame>? {
        logger.info("Sending Request")
        try {
            val client = createClient()

            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.d0nkey.top/streamer-decks?limit=21&twitch_id=156646165"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()
            logger.info("Request built")

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            logger.info("Got response code:${response.statusCode()}")

            val document = Jsoup.parse(response.body())
            logger.info("Parsed response to html")
            val tableBody = document.getElementsByTag("tbody").first()

            val rows = tableBody.getElementsByTag("tr")

            return rows.map { collectResults(it) }
        } catch (e: Exception) {
            logger.error("couldn't process request", e)
        }
        return null
    }

    private fun createClient(): HttpClient {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build()
    }

    private fun collectResults(row: Element): DonkeyGame {
        val deckCode = row.getElementsByAttribute("phx-value-deckcode").first().attr("phx-value-deckcode")
        val heroClass = row.getElementsByClass("deck-text").first().children().first().children()[1].text()
        val legendRank = row.getElementsByClass("tag legend-rank")[1].text()
        return DonkeyGame(
            heroClass = heroClass,
            legendRank = legendRank,
            deckCode = deckCode
        )
    }

    data class DonkeyGame(
        val heroClass: String,
        val legendRank: String,
        val deckCode: String
    )

    interface NewGameConsumer {
        fun newGamesFound(games: List<DonkeyGame>)
    }
}
