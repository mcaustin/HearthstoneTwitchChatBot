package com.github.mcaustin.deck

class Deck(
    private val cardDictionary: CardDictionary?,
    val format: FormatType,
    val heroDbfId: Int,
    val deckCode: String
) {

    var cards: MutableList<Pair<Card, Int>> = mutableListOf()
    val heroClass = cardDictionary?.cardMap?.get(heroDbfId)?.cardClass ?: "Unknown"

    private val notableCards = cardDictionary?.cardMap?.filter { it.value.rarity == "LEGENDARY" }
        ?.map { Pair(it.key, it.value.name?.replace("\\s".toRegex(), "") ?: "UNKNOWN") } ?: emptyList()

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
        val cardCount = cards.sumOf { it.second }

        val computedFormat = if (cardCount == 15) {
            "DUELS"
        } else {
            format
        }

        val description =
            StringBuilder("$computedFormat $heroClass")

        val displayTags = tags
        if (displayTags.isNotEmpty()) {
            description.append(" [ ")

            if (displayTags.size > MAX_TAGS) {
                displayTags.subList(0, MAX_TAGS).forEach { description.append("$it ") }
                description.append("...")
            } else {
                displayTags.forEach { description.append("$it ") }
            }
            description.append("]")
        }

        return description.toString()
    }

    companion object {
        const val MAX_TAGS = 12
    }
}
