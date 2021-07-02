package com.github.mcaustin.twitch.commands

import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser

interface CommandExecutor {

    fun executeCommand(sender: TwitchUser?, message: TwitchMessage?)

    fun canHandle(sender: TwitchUser?, message: TwitchMessage?): Boolean
}
