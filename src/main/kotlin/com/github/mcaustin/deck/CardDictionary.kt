package com.github.mcaustin.deck

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.logging.log4j.LogManager
import java.io.InputStream

class CardDictionary(inputStream: InputStream) {

    private val logger = LogManager.getLogger(CardDictionary::class.java)
    private val objectMapper = ObjectMapper().registerKotlinModule()

    val cardMap: Map<Int, Card>

    init {
        val typeReference = objectMapper.typeFactory.constructCollectionType(List::class.java, Card::class.java)

        val jsonCards: List<Card> = objectMapper.readValue(inputStream, typeReference)

        logger.debug("Parsed ${jsonCards.size} cards")

        cardMap = jsonCards.associateBy { it.dbfId }
    }

}
