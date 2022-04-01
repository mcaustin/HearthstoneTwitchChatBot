package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.db.DeckStatsDAO
import com.github.mcaustin.db.ViewerDeckRequestLocalDbDAO
import java.lang.UnsupportedOperationException

class SubmissionsCommand(private val twirk: Twirk): CommandExecutor {

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        if (!canHandle(sender, message)) {
            throw UnsupportedOperationException("Can't handle that command")
        }

        message?.content?.let {
            handleStatsRequest(sender, it)
        }
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) = message?.content?.startsWith("!submissions") ?: false

    private fun handleStatsRequest(sender: TwitchUser?, messageContent: String) {
        val tokens = messageContent.split(" ")
        val viewerName = getViewerName(tokens, sender)

        viewerName?.let {
            val requestCount = DeckStatsDAO.getViewerDeckCount(it)
            var plural = ""
            if (requestCount != 1) {
                plural = "s"
            }
            twirk.channelMessage("I have recorded $requestCount deck submission${plural} for $it")
        }
    }

    private fun getViewerName(
        tokens: List<String>,
        sender: TwitchUser?
    ): String? {
        return if (tokens.size == 2) {
            tokens[1]
        } else if (sender != null && tokens.size == 1) {
            sender.displayName
        } else {
            null
        }
    }
}
