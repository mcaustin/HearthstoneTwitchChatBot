package com.github.mcaustin.db

import com.github.mcaustin.deck.Card
import com.github.mcaustin.deck.DeckCodeBuilder
import org.apache.logging.log4j.LogManager
import org.mapdb.DBMaker
import org.mapdb.Serializer

object CustomCommandDAO {
    private const val CUSTOM_COMMANDS_DB = "CustomCommands.db"

    private const val CUSTOM_COMMAND_MAP = "customCommandMap"

    private val logger = LogManager.getLogger(CustomCommandDAO::class.java)

    private val viewerStatsDb = DBMaker
        .fileDB(CUSTOM_COMMANDS_DB)
        .closeOnJvmShutdown()
        .make()

    private val customCommandMap =
        viewerStatsDb.hashMap(CUSTOM_COMMAND_MAP)
            .keySerializer(Serializer.STRING)
            .valueSerializer(Serializer.STRING)
            .createOrOpen()

    fun getCommands() = customCommandMap

    fun addCommand(trigger: String, commandText: String): Boolean {
        if (customCommandMap.containsKey(trigger)) {
            logger.info("Already had a command for $trigger -> ${customCommandMap[trigger]}")
            return false
        }
        customCommandMap[trigger] = commandText
        logger.info("Added command $trigger -> ${customCommandMap[trigger]}")
        return true
    }

    fun editCommand(trigger: String, commandText: String): Boolean {
        if (!customCommandMap.containsKey(trigger)) {
            logger.info("No existing command for $trigger")
            return false
        }
        customCommandMap[trigger] = commandText
        logger.info("Edited command $trigger -> ${customCommandMap[trigger]}")
        return true
    }

    fun removeCommand(trigger: String): Boolean {
        if (!customCommandMap.containsKey(trigger)) {
            logger.info("No existing command for $trigger")
            return false
        }
        customCommandMap.remove(trigger)
        logger.info("Removed command $trigger")
        return true
    }
}
