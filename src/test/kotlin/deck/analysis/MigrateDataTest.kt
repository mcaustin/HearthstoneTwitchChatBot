package deck.analysis

import com.github.mcaustin.db.DeckStatsDAO
import com.github.mcaustin.db.ViewerDeckRequestLocalDbDAO
import org.junit.jupiter.api.Test
import javax.swing.text.View

class MigrateDataTest {

    @Test
    fun migrate() {

//        ViewerDeckRequestLocalDbDAO.getAll().forEach {
//            DeckStatsDAO.addRequest(it)
//        }

        println(DeckStatsDAO.getViewerDeckCount("discardedfood"))
        println(DeckStatsDAO.getTopViewers(3))
        println(DeckStatsDAO.getTopCards(3))

    }

}
