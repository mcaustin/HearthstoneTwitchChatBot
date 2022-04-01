package deck.analysis

import com.github.mcaustin.deck.CardDataDownloader
import com.github.mcaustin.deck.CardDictionary
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStream

class CardsMap {

    @Test
    fun test() {
        val cardDictionary = CardDictionary(getCardJsonData())

        val output = FileWriter("mappedCards.json")

        cardDictionary.cardMap.forEach {
            val id = it.key
            val card = it.value
            output.write("\"${id}\": \"${card.name}\",\n")
            output.flush()
        }
        output.close()


    }

    /**
     * Creates a file based on the given path to the resource folder.
     * @return A File referencing the given path.
     */
    private fun getCardJsonData(): InputStream {
        return FileInputStream(File(CardDataDownloader.CARD_FILE_NAME))
    }
}
