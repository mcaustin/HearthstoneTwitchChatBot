package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.enums.USER_TYPE
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.db.CustomCommandDAO

class CustomCommand(private val twirk: Twirk) : CommandExecutor {

    private val userToken = "*USERTOKEN*"

    private val chatTriggers: Map<String, String> = CustomCommandDAO.getCommands()

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        message?.content?.let {
            handleChatTriggerRequest(sender, it)
        }
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) =
        chatTriggers.containsKey(message?.content?.lowercase())
            || message?.content?.startsWith("!addstatscom") == true
            || message?.content?.startsWith("!editstatscom") == true
            || message?.content?.startsWith("!delstatscom") == true

    private fun handleChatTriggerRequest(sender: TwitchUser?, messageContent: String) {
        val userType = sender?.userType?.value ?: 0

        if (userType >= USER_TYPE.MOD.value) {
            when (messageContent.substringBefore(" ")) {
                "!addstatscom" -> addNewCommand(messageContent)
                "!editstatscom" -> editCommand(messageContent)
                "!delstatscom" -> deleteCommand(messageContent)
            }
        }

        handleExistingCommand(sender, messageContent)
    }

    private fun addNewCommand(messageContent: String) {
        val parts = messageContent.split(" ")
        val trigger = parts[1]
        val message = messageContent.substringAfter(" ").substringAfter(" ")

        if (parts.size > 2 && CustomCommandDAO.addCommand(trigger, message)) {
            twirk.channelMessage("Successfully added command $trigger -> $message")
        }
    }

    private fun editCommand(messageContent: String) {
        val parts = messageContent.split(" ")
        val trigger = parts[1]
        val message = messageContent.substringAfter(" ").substringAfter(" ")

        if (parts.size > 2 && CustomCommandDAO.editCommand(trigger, message)) {
            twirk.channelMessage("Successfully added command $trigger -> $message")
        }
    }

    private fun deleteCommand(messageContent: String) {
        val commandParts = messageContent.trim().split(" ")
        if (commandParts.size == 2) {
            val trigger = commandParts[1]
            if (CustomCommandDAO.removeCommand(trigger)) {
                twirk.channelMessage("Successfully removed command $trigger")
            }
        }
    }

    private fun handleExistingCommand(sender: TwitchUser?, messageContent: String) {
        val viewerName = sender?.displayName ?: ""

        val message = chatTriggers[messageContent.lowercase()]

        message?.let {
            val nameInserted = it.replace(userToken, viewerName)

            twirk.channelMessage(nameInserted)
        }
    }
}
