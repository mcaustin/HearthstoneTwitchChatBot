package com.github.mcaustin

import com.github.mcaustin.twitch.TwitchBot

fun main(args: Array<String>) {
    if (args.size != 3) {
        printUsageInstructions()
    } else {
        println("Program started.")
        TwitchBot(
            channelName = args[0],
            accountName = args[1],
            oAuthSecret = args[2])
            .startBot()
    }
}

fun printUsageInstructions() {
    println("Usage java -jar HearthstoneTwitchChatBot-1.0-all.jar <twitch-channel-name> <twitch-bot-account-name> <twitch-oauth-secret>")
    println("Example Usage")
    println("HearthstoneTwitchChatBot-1.0-all.jar keith_numbers mybotname oauth:911aaaa5bbbbb22i444ep2sl33369c")
}

