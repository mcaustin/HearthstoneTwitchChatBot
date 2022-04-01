package com.github.mcaustin.twitch.commands

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser

class ChatReplyCommand(private val twirk: Twirk) : CommandExecutor {

    private val userToken = "*USERTOKEN*"

    private val chatTriggers = mapOf(
        "first" to "Congrats on being first $userToken, your ancestors would be proud of your accomplishments.",
//        "!endstream" to "Thanks for watching! | Keith's links " +
//            "| YouTube: https://www.youtube.com/channel/UCXhJF85m5gEwf1JMxuqMP-g " +
//            "| Twitter: https://twitter.com/keithnumbershs | Discord: https://discord.gg/yHRkyGRYeD",
//        "!deck" to "All of Keith's decks are automatically uploaded to this page: https://bit.ly/360z7W0",
//        "!youtube" to "Keith produces YouTube videos pretty regularly here: https://www.youtube.com/c/KeithNumbersHearthstone",
//        "!onlyfans" to "onlyfans.com/keith_numbers The official keith numbers website has been migrated.",
//        "!website" to "please do not visit http://keithnumbers.com",
//        "^" to "^^",
//        "^^" to "^^^",
//        "^^^" to "^^^^",
//        "!twitter" to "Keith has a Twitter. You should follow it. twitter.com/keithnumbershs",
//        "!lurk" to "$userToken cast conceal on themselves",
//        "!donate" to "Your support is always appreciated but never required: https://streamlabs.com/keith_numbers/tip",
//        "!donation" to "Consider supporting the stream through https://streamlabs.com/keith_numbers/tip :) Donations are never required but they are always appreciated keithnHidog",
//        "!discord" to "Only the highest quality gamers are invited to Keith's discord. But you can still sneak in anyway. https://discord.gg/CPugkHfaqA"
    )

    override fun executeCommand(sender: TwitchUser?, message: TwitchMessage?) {
        message?.content?.let {
            handleChatTriggerRequest(sender, it)
        }
    }

    override fun canHandle(sender: TwitchUser?, message: TwitchMessage?) =
        chatTriggers.containsKey(message?.content?.lowercase())

    private fun handleChatTriggerRequest(sender: TwitchUser?, messageContent: String) {
        val viewerName = sender?.displayName ?: ""

        val message = chatTriggers[messageContent.lowercase()]

        message?.let {
            val nameInserted = it.replace(userToken, viewerName)

            twirk.channelMessage(nameInserted)
        }
    }
}
