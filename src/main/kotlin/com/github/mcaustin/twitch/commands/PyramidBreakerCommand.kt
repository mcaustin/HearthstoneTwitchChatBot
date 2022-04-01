package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser

class PyramidBreakerCommand(private val twirk: Twirk) : CommandExecutor {

    private var lastUser: String? = null
    private var lastToken: String? = null
    private var tokenAmount: Int = 0
    private val minPyramidSize = 3

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        val currentUser = sender?.displayName ?: ""
        val sameUser = lastUser == currentUser
        lastUser = currentUser

        val messageContent = message?.content
        val tokens = parseTokens(messageContent)
        if (tokens == null) {
            reset()
            return
        }

        if (!sameUser || tokens.first != lastToken) {
            //Not the same user, reset tracking to null or a first user
            if (tokens.second == 1) {
                newUser(currentUser, tokens.first)
            } else {
                reset()
            }
        } else if (++tokenAmount == tokens.second) {
            //We have the same user AND the tokens are the same, just verify the count now
            if (tokenAmount >= minPyramidSize) {
                val twitchMessage = "Wow looks like you are building a nice \"${tokens.first}\" pyramid ${sender?.displayName ?: ""}!"

                println(twitchMessage)
                twirk.channelMessage(twitchMessage)
                reset()
            }
        } else {
            reset()
        }
    }

    /**
     * If this string is repeated copies of the same text, return that text and the amount of copies.
     * null otherwise
     */
    fun parseTokens(messageContent: String?): Pair<String, Int>? {
        return messageContent?.trim()?.let {
            val tokenized = it.split(" ")
            val firstToken = tokenized.first()

            if (it.replace(firstToken, "").trim().isNotEmpty()) {
                //This contained other characters
                null
            } else {
                Pair(firstToken, tokenized.size)
            }
        }
    }

    private fun reset() {
        lastUser = null
        lastToken = null
        tokenAmount = 0
    }

    private fun newUser(newUser: String, newToken: String) {
        lastUser = newUser
        lastToken = newToken
        tokenAmount = 1
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) = true
}
