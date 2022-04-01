package twitch

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.twitch.commands.ChatReplyCommand
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class ChatReplyCommandTest {

    private val twirk: Twirk = mockk()
    private val twitchUser: TwitchUser = mockk()
    private val twitchMessage: TwitchMessage = mockk()
    private val chatReplyCommand = ChatReplyCommand(twirk)

    @Test
    fun testFirst() {
        every { twitchMessage.content }.returns("FIRST")
        every { twitchUser.displayName }.returns("twitchUser")
        every { twirk.channelMessage(any()) }.answers {  }

        chatReplyCommand.executeCommand(twitchUser, twitchMessage)
    }
}
