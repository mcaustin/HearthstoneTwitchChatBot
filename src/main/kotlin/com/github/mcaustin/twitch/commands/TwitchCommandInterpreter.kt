package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.deck.DeckCodeBuilder

class TwitchCommandInterpreter(twirk: Twirk, deckCodeBuilder: DeckCodeBuilder) {

    private val commandInterpreters = listOf(
        SubmitDeckCodeCommand(deckCodeBuilder, twirk),
        LastDeckCommand(twirk),
        SubmissionsCommand(twirk),
        DeckStatsCommand(twirk),
        ChatReplyCommand(twirk),
        CustomCommand(twirk),
        UrbanDictionaryCommand(twirk)
    )

    fun interpret(sender: TwitchUser?, message: TwitchMessage?) {
        commandInterpreters
            .filter { it.canHandle(sender, message) }
            .forEach { it.executeCommand(sender, message) }
    }
}
