package com.github.mcaustin.deck.analysis

import com.gikk.twirk.Twirk
import com.github.mcaustin.deck.Deck
import org.apache.commons.text.StringEscapeUtils
import org.apache.logging.log4j.LogManager
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class NetDeckAnalysis(private val twirk: Twirk) : DeckAnalyzer {

    private val logger = LogManager.getLogger(NetDeckAnalysis::class.java)

    override fun analyze(deck: Deck): AnalysisResult? {

        val urlEncodedString = URLEncoder.encode(deck.deckCode, "UTF-8")
        val client: HttpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://hearthstone-decks.net/?s=$urlEncodedString"))
            .timeout(Duration.ofSeconds(5))
            .GET()
            .build()

        logger.info("dispatching request: ${request.uri()}")

        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        response.thenApply {
            val responseBody = it.body()

            val locatorString = "<h3 class=\"elementor-post__title\""

            val subString = responseBody.substringAfter(locatorString, "")
            var title = subString.substringBefore("</a>", "")
            title = title.substringAfterLast(">", "")

            if (title.isNotEmpty()) {
                title = StringEscapeUtils.unescapeHtml4(title).trim()
                twirk.channelMessage("Net deck alert! $title")
            } else {
                logger.info("No netdeck found.")
            }
        }

        return null
    }
}
