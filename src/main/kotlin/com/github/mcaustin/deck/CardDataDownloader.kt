package com.github.mcaustin.deck

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.logging.log4j.LogManager
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object CardDataDownloader {

    const val CARD_FILE_NAME = "carddata.json"

    private const val url = "https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json"
    private val logger = LogManager.getLogger(this.javaClass)
    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun createCardsFile(): Boolean {

        try {
            val client = createClient()

            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()
            logger.info("Request built")

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            logger.info("Got response code:${response.statusCode()}")

            val typeReference =
                objectMapper.typeFactory.constructCollectionType(List::class.java, Card::class.java)

            val cards: List<Card> = objectMapper.readValue(response.body(), typeReference)

            val cardsFile = File(CARD_FILE_NAME)

            objectMapper.writeValue(cardsFile, cards)
            logger.info("Successfully downloaded card json file from $url to $CARD_FILE_NAME")
            return true
        } catch (e: Exception) {
            logger.error("couldn't process request", e)
            return false
        }

    }

    private fun createClient(): HttpClient {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()
    }
}
