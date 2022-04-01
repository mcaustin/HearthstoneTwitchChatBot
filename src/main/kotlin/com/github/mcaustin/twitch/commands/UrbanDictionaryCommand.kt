package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.google.common.io.Files
import org.apache.commons.text.StringEscapeUtils
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files.readAllLines
import java.nio.file.Path
import java.time.Duration

class UrbanDictionaryCommand(private val twirk: Twirk) : CommandExecutor {

    private val trigger = "!define"

    private val urbanDictionary = "https://www.urbandictionary.com/define.php?term="
    private val filterList = readAllLines(Path.of("badwords.txt"), StandardCharsets.UTF_8)

    private val logger = LogManager.getLogger(this::class.java)

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        message?.content?.let {
            handleChatTriggerRequest(sender, it)
        }
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) =
        message?.content?.lowercase()?.startsWith(trigger) == true

    private fun handleChatTriggerRequest(sender: TwitchUser?, messageContent: String) {
        val searchTerm = messageContent.substringAfter(" ")

        var definition = request(searchTerm)

        if (definition?.trim()?.isNotBlank() == true) {
            filterList.forEach {
                definition = definition!!.replace(it, "*".repeat(it.length), true)
            }
            twirk.channelMessage("$searchTerm: $definition")
        }

    }

    private fun request(term: String): String? {
        val urlEncodedString = URLEncoder.encode(term, "UTF-8")
        val client: HttpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("$urbanDictionary$urlEncodedString"))
            .timeout(Duration.ofSeconds(5))
            .GET()
            .build()

        logger.info("dispatching request: ${request.uri()}")

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        val responseBody = response.body()

        val body = Jsoup.parse(responseBody).body()

        val definition = body.getElementsByClass("meaning").first().text()

        if (definition.isNotEmpty()) {
            return definition
        } else {
            logger.info("No definition found.")
        }
        return null
    }


}
