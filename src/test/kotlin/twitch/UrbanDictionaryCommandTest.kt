package twitch

import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.twitch.commands.UrbanDictionaryCommand
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class UrbanDictionaryCommandTest {

    private val message: TwitchMessage = mockk()
    private val twitchUser: TwitchUser = mockk()
    private val command = UrbanDictionaryCommand(mockk())

    init {
        every { twitchUser.displayName }.returns("test")
    }

    @Test
    fun testDefine() {
        every { message.content }.returns("!define milky handshake")

        command.executeCommand(twitchUser, message)
    }
}
