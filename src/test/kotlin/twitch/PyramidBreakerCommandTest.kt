package twitch

import com.gikk.twirk.Twirk
import com.gikk.twirk.types.twitchMessage.TwitchMessage
import com.gikk.twirk.types.users.TwitchUser
import com.github.mcaustin.twitch.commands.PyramidBreakerCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class PyramidBreakerCommandTest {

    private val twirk: Twirk = mockk()
    private val twitchUser: TwitchUser = mockk()
    private val twitchMessage: TwitchMessage = mockk()
    private val pyramidBreakerCommand = PyramidBreakerCommand(twirk)

    init {
        every { twitchMessage.content }.returns("FIRST")
        every { twitchUser.displayName }.returns("twitchUser")
        every { twirk.channelMessage(any()) }.answers {  }
    }

    @Test
    fun test3Tokens() {
        Assertions.assertThat(pyramidBreakerCommand.parseTokens("a a a")).isEqualTo(Pair("a", 3))
    }

    @Test
    fun testBrokenTokens() {
        Assertions.assertThat(pyramidBreakerCommand.parseTokens("I play this")).isNull()
    }

    @Test
    fun testSingleToken() {
        Assertions.assertThat(pyramidBreakerCommand.parseTokens("I")).isEqualTo(Pair("I", 1))
    }

    @Test
    fun pyramidOf3() {
        every { twitchMessage.content }.returns("a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        verify(exactly = 1) { twirk.channelMessage(any()) }
    }

    @Test
    fun pyramidOf4() {
        every { twitchMessage.content }.returns("a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        verify(exactly = 1) { twirk.channelMessage(any()) }
    }

    @Test
    fun selfBreakPyramid() {
        every { twitchMessage.content }.returns("a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a b a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        verify(inverse = true) { twirk.channelMessage(any()) }
    }

    @Test
    fun otherBreakPyramid() {
        every { twitchUser.displayName }.returns("twitchUser")
        every { twitchMessage.content }.returns("a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a")
        every { twitchUser.displayName }.returns("differentUser")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a")
        every { twitchUser.displayName }.returns("twitchUser")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        every { twitchMessage.content }.returns("a a a")
        pyramidBreakerCommand.executeCommand(twitchUser, twitchMessage)

        verify(inverse = true) { twirk.channelMessage(any()) }
    }
}
