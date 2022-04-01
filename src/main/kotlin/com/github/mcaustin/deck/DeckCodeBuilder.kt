package com.github.mcaustin.deck

import com.github.mcaustin.deck.CardDataDownloader.CARD_FILE_NAME
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Base64

class DeckCodeBuilder {

    private val logger = LogManager.getLogger(DeckCodeBuilder::class.java)

    val cardDictionary = CardDictionary(getCardJsonData())

    /**
     * Creates a file based on the given path to the resource folder.
     * @return A File referencing the given path.
     */
    private fun getCardJsonData(): InputStream {
        return FileInputStream(File(CARD_FILE_NAME))
    }

    fun buildDeck(deckString: String): Deck {
        logger.debug("Received DeckString $deckString")
        val deckBytes = Base64.getDecoder().decode(deckString)
        val deckInts = getVarInts(deckBytes)
        var offset = 0

        offset++ //first byte is 0

        //version
        deckInts[offset++]

        //format
        val format = deckInts[offset++]

        //Number of applicable heros (should be 1)
        deckInts[offset++]

        val heroDbfId = deckInts[offset++]

        val deck = Deck(
            cardDictionary,
            format = FormatType.values()[format],
            heroDbfId = heroDbfId,
            deckCode = deckString
        )

        //Single Copy cards
        val numSingleCopyCards = deckInts[offset++]

        for (i in 1..numSingleCopyCards) {
            val cardDbfId = deckInts[offset++]
            deck.addCard(cardDbfId, 1)
        }

        //2-Copy cards
        val doubleCopyCards = deckInts[offset++]

        for (i in 1..doubleCopyCards) {
            val cardDbfId = deckInts[offset++]
            deck.addCard(cardDbfId, 2)
        }

        //Multi-copy cards
        val multiCopyCards = deckInts[offset++]

        for (i in 1..multiCopyCards) {
            val cardDbfId = deckInts[offset++]
            val count = deckInts[offset++]
            deck.addCard(cardDbfId, count)
        }

        logger.debug(deck.description())

        return deck
    }

    private fun getVarInts(src: ByteArray): List<Int> {
        var offset = 0
        val resultInts = mutableListOf<Int>()

        while (offset < src.size) {
            val result = IntArray(1)
            offset = getVarInt(src, offset, result)
            resultInts += result[0]
        }

        return resultInts
    }

    private fun getVarInt(src: ByteArray, originalOffset: Int, dst: IntArray): Int {
        var offset = originalOffset
        var result = 0
        var shift = 0
        var b: Int
        do {
            if (shift >= 32) {
                // Out of range
                throw IndexOutOfBoundsException("varint too long")
            }
            // Get 7 bits from next byte
            b = src[offset++].toInt()
            result = result or (b and 0x7F shl shift)
            shift += 7
        } while (b and 0x80 != 0)
        dst[0] = result
        return offset
    }
}
