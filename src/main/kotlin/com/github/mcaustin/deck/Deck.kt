package com.github.mcaustin.deck

import com.github.mcaustin.deck.CardDbfIds.ANTONIDAS
import com.github.mcaustin.deck.CardDbfIds.BAKU_THE_MOONEATER
import com.github.mcaustin.deck.CardDbfIds.CAVERNS_BELOW
import com.github.mcaustin.deck.CardDbfIds.CELESTIAL_ALIGNMENT
import com.github.mcaustin.deck.CardDbfIds.COLDLIGHT_ORACLE
import com.github.mcaustin.deck.CardDbfIds.DARKGLARE
import com.github.mcaustin.deck.CardDbfIds.DARKMOON_YOGG
import com.github.mcaustin.deck.CardDbfIds.DEAD_MANS_HAND
import com.github.mcaustin.deck.CardDbfIds.GENN_GREYMANE
import com.github.mcaustin.deck.CardDbfIds.KINGSBANE
import com.github.mcaustin.deck.CardDbfIds.KING_TOGWAGGLE
import com.github.mcaustin.deck.CardDbfIds.LINECRACKER
import com.github.mcaustin.deck.CardDbfIds.MAJOR_DOMO
import com.github.mcaustin.deck.CardDbfIds.MALYGOS
import com.github.mcaustin.deck.CardDbfIds.MAYOR_NOGGENFOGGER
import com.github.mcaustin.deck.CardDbfIds.MECHATHUN
import com.github.mcaustin.deck.CardDbfIds.MOZAKI
import com.github.mcaustin.deck.CardDbfIds.OG_YOGG
import com.github.mcaustin.deck.CardDbfIds.OPEN_WAYGATE
import com.github.mcaustin.deck.CardDbfIds.PATCHES
import com.github.mcaustin.deck.CardDbfIds.RENO_JACKSON
import com.github.mcaustin.deck.CardDbfIds.SAYGE_SEER
import com.github.mcaustin.deck.CardDbfIds.SHUDDERWOCK
import com.github.mcaustin.deck.CardDbfIds.SPECTRAL_PILLAGER
import com.github.mcaustin.deck.CardDbfIds.TESS_GREYMANE
import com.github.mcaustin.deck.CardDbfIds.UNSEEN_SABOTEUR
import com.github.mcaustin.deck.CardDbfIds.ZEPHYRS_THE_GREAT
import java.lang.StringBuilder

class Deck(
    private val cardDictionary: CardDictionary?,
    val format: FormatType,
    val heroDbfId: Int,
    val deckCode: String
) {

    var cards: MutableList<Pair<Card, Int>> = mutableListOf()
    val heroClass = cardDictionary?.cardMap?.get(heroDbfId)?.cardClass ?: "Unknown"

    private val notableCards = listOf(
        Pair(BAKU_THE_MOONEATER, "[Baku]"),
        Pair(GENN_GREYMANE, "[Genn]"),
        Pair(SHUDDERWOCK, "[Shudderwock]"),
        Pair(DEAD_MANS_HAND, "[Dead-Mans-Hand]"),
        Pair(MOZAKI, "[Mozaki]"),
        Pair(MECHATHUN, "[Mechathun]"),
        Pair(MAYOR_NOGGENFOGGER, "[Mayor-Noggenfogger]"),
        Pair(UNSEEN_SABOTEUR, "[Unseen-Saboteur]"),
        Pair(DARKGLARE, "[Darkglare]"),
        Pair(KING_TOGWAGGLE, "[King-Togwaggle]"),
        Pair(MALYGOS, "[Malygos]"),
        Pair(CELESTIAL_ALIGNMENT, "[Celestial-Alignment]"),
        Pair(PATCHES, "[Patches]"),
        Pair(RENO_JACKSON, "[Reno]"),
        Pair(ZEPHYRS_THE_GREAT, "[Zephyrs]"),
        Pair(DARKMOON_YOGG, "[Darkmoon-Yogg]"),
        Pair(OG_YOGG, "[OG-Yogg]"),
        Pair(COLDLIGHT_ORACLE, "[Coldlight]"),
        Pair(CAVERNS_BELOW, "[Caverns-Below]"),
        Pair(MAJOR_DOMO, "[Domo]"),
        Pair(SPECTRAL_PILLAGER, "[Pillager]"),
        Pair(OPEN_WAYGATE, "[Waygate]"),
        Pair(ANTONIDAS, "[Antonidas]"),
        Pair(TESS_GREYMANE, "[Tess]"),
        Pair(LINECRACKER, "[Linecracker]"),
        Pair(SAYGE_SEER, "[Sayge]"),
        Pair(KINGSBANE, "[Kingsbane]")
    )

    val tags: List<String>
        get() {
            val computedList = mutableListOf<String>()
            notableCards.forEach { notableCard ->
                if (cards.any { it.first.dbfId == notableCard.first }) {
                    computedList.add(notableCard.second)
                }
            }
            return computedList
        }

    private var singleCopyCards = 0
    private var doubleCopyCards = 0

    private fun countDuplicateCardsInDifferentSets(): Int {
        val normalizedCardIds = cards
            .filter { it.second == 1 }
            .map { it.first }
            .mapNotNull { it.id?.removePrefix("CORE_") }

        return normalizedCardIds.groupingBy { it }.eachCount().filter { it.value > 1 }.size
    }

    fun addCard(dbfId: Int, copies: Int) {
        val card = cardDictionary?.cardMap?.get(dbfId) ?: Card(dbfId = dbfId)

        cards.add(Pair(card, copies))

        if (copies == 1) {
            singleCopyCards++
        } else if (copies == 2) {
            doubleCopyCards++
        }
    }

    fun description(): String {
        val computedDoubleCopyCards = cards.filter { it.second == 2 }.size + countDuplicateCardsInDifferentSets()
        val computedSingleCards = cards.filter { it.second == 1 }.size - (countDuplicateCardsInDifferentSets()*2)

        val description =
            StringBuilder("$format $heroClass 1-ofs:($computedSingleCards) 2-ofs:($computedDoubleCopyCards)")

        notableCards.forEach { notableCard ->
            if (cards.any { it.first.dbfId == notableCard.first }) {
                description.append(" ${notableCard.second}")
            }
        }

        return description.toString()
    }
}
