package com.github.mcaustin.db

import com.github.mcaustin.deck.Card
import com.github.mcaustin.deck.DeckCodeBuilder
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mapdb.DBMaker
import org.mapdb.Serializer

object ViewerDeckRequestLocalDbDAO {

    private const val DATABASE_NAME = "viewerdecks.db"
    private const val VIEWER_DECK_SET_NAME = "viewerdecks"

    private val db = DBMaker
        .fileDB(DATABASE_NAME)
        .closeOnJvmShutdown()
        .make()

    private val deckCodeBuilder = DeckCodeBuilder()

    private val viewerDeckMap =
        db.hashSet(VIEWER_DECK_SET_NAME, Serializer.STRING).createOrOpen()

    fun addRequest(viewerDeckRequest: ViewerDeckRequest) {
        viewerDeckMap.add(Json.encodeToString(viewerDeckRequest))
        db.commit()
    }

    fun getAll() = viewerDeckMap.map {
        val deckRequest: ViewerDeckRequest =Json.decodeFromString(it)
        deckRequest
    }

    fun findRequests(viewerId: String): List<ViewerDeckRequest> {
        return viewerDeckMap.map {
            val deckRequest: ViewerDeckRequest = Json.decodeFromString(it)
            deckRequest
        }.filter {
            it.viewerId == viewerId
        }
    }

    fun requestCount(viewerId: String): Int {
        return viewerDeckMap.map {
            val deckRequest: ViewerDeckRequest = Json.decodeFromString(it)
            deckRequest
        }.filter {
            it.viewerId == viewerId
        }.count()
    }

    fun groupRequestsByClass(): Map<String, Int> {
        val viewerDecks = viewerDeckMap.map {
            val deckRequest: ViewerDeckRequest = Json.decodeFromString(it)
            deckRequest
        }
        val deckMap = HashMap<String, Int>()

        viewerDecks.forEach {
            val heroClass = it.heroClass ?: "UNKNOWN"
            if (!deckMap.containsKey(heroClass)) {
                deckMap[heroClass] = 0
            }
            deckMap[heroClass] = deckMap.getValue(heroClass) + 1
        }
        return deckMap
    }

    fun groupRequestsByViewer(): Map<String, Int> {
        val viewerDecks = viewerDeckMap.map {
            val deckRequest: ViewerDeckRequest = Json.decodeFromString(it)
            deckRequest
        }
        val viewerRequestMap = HashMap<String, Int>()

        viewerDecks.forEach {
            val viewer = it.viewerId
            if (!viewerRequestMap.containsKey(viewer)) {
                viewerRequestMap[viewer] = 0
            }
            viewerRequestMap[viewer] = viewerRequestMap.getValue(viewer) + 1
        }
        return viewerRequestMap
    }

    fun cardsByOccurrence(): Map<Card, Int> {
        val viewerDecks = viewerDeckMap.map {
            val deckRequest: ViewerDeckRequest = Json.decodeFromString(it)
            deckRequest
        }
        val cardMap = HashMap<Card, Int>()

        viewerDecks.mapNotNull { it.deckCode }
            .map { deckCodeBuilder.buildDeck(it) }
            .forEach { deck ->
                deck.cards.forEach { cardPair ->

                    val card = cardPair.first
                    val occurrences = cardPair.second
                    if (!cardMap.containsKey(card)) {
                        cardMap[card] = 0
                    }
                    cardMap[card] = cardMap[card]?.plus(occurrences) ?: occurrences
                }
            }
        return cardMap
    }
}
