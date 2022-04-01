package com.github.mcaustin.db

import com.github.mcaustin.deck.Card
import com.github.mcaustin.deck.DeckCodeBuilder
import org.apache.logging.log4j.LogManager
import org.mapdb.DBMaker
import org.mapdb.Serializer

object DeckStatsDAO {
    private const val VIEWER_STATS_DB = "ViewerStats.db"

    private const val VIEWER_DECK_COUNT_MAP = "viewerIdDeckCountMap"
    private const val VIEWER_LAST_DECK_MAP = "viewerIdLastDeckMap"
    private const val CARD_COUNT_MAP = "cardIdCountMap"
    private const val HERO_COUNT_MAP = "heroCountMap"

    private val logger = LogManager.getLogger(DeckStatsDAO::class.java)

    private val deckCodeBuilder = DeckCodeBuilder()
    private val cardMap: Map<Int, Card> = deckCodeBuilder.cardDictionary.cardMap

    private val viewerStatsDb = DBMaker
        .fileDB(VIEWER_STATS_DB)
        .closeOnJvmShutdown()
        .make()

    private val viewerDeckCount =
        viewerStatsDb.hashMap(VIEWER_DECK_COUNT_MAP)
            .keySerializer(Serializer.STRING)
            .valueSerializer(Serializer.INTEGER)
            .createOrOpen()

    private val viewerLastDeckMap =
        viewerStatsDb.hashMap(VIEWER_LAST_DECK_MAP)
            .keySerializer(Serializer.STRING)
            .valueSerializer(Serializer.STRING)
            .createOrOpen()

    private val cardCountMap =
        viewerStatsDb.hashMap(CARD_COUNT_MAP)
            .keySerializer(Serializer.INTEGER)
            .valueSerializer(Serializer.INTEGER)
            .createOrOpen()

    private val heroCountMap =
        viewerStatsDb.hashMap(HERO_COUNT_MAP)
            .keySerializer(Serializer.STRING)
            .valueSerializer(Serializer.INTEGER)
            .createOrOpen()

    fun getViewerDeckCount(viewerId: String) = viewerDeckCount[viewerId] ?: 0

    private fun getHeroCount(heroClass: String) = heroCountMap[heroClass] ?: 0

    fun getViewerLastDeck(viewerId: String) = viewerLastDeckMap[viewerId]

    fun getTopHeroes(number: Int) = heroCountMap.entries.sortedByDescending { it.value }.take(number)

    fun getTopCards(number: Int) =
        cardCountMap.entries
            .sortedByDescending { it.value }
            .map { Pair(cardMap[it.key], it.value) }
            .take(number)

    fun getTopViewers(number: Int) = viewerDeckCount.entries.sortedByDescending { it.value }.take(number)

    private fun getCardCount(cardId: Int) = cardCountMap[cardId] ?: 0

    fun addRequest(viewerDeckRequest: ViewerDeckRequest) {
        val viewerId = viewerDeckRequest.viewerId
        logger.info("Adding viewer deck request for $viewerId with code ${viewerDeckRequest.deckCode}")
        viewerDeckCount[viewerId] = getViewerDeckCount(viewerId) + 1
        viewerLastDeckMap[viewerId] = viewerDeckRequest.deckCode

        viewerDeckRequest.deckCode?.let { deckString ->
            val deck = deckCodeBuilder.buildDeck(deckString)
            heroCountMap[deck.heroClass] = getHeroCount(deck.heroClass) + 1
            deck.cards.forEach { cardPair ->
                cardPair.first.dbfId.let {
                    cardCountMap[it] = getCardCount(it) + cardPair.second
                }
            }
        }
    }
}
