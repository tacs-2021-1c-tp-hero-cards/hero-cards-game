package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayerTest {

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    lateinit var user: User
    lateinit var player: Player

    @BeforeEach
    fun init() {
        user = User(0L, "userName", "fullName", "password")
        player = Player(user = user, availableCards = listOf(batman))
    }

    @Nested
    inner class ResolveDuel{

        @Test
        fun winDuel() {
            val winPlayer = player.winDuel(flash)

            assertEquals(user, winPlayer.user)
            assertTrue(winPlayer.prizeCards.contains(batman))
            assertTrue(winPlayer.prizeCards.contains(flash))
            assertTrue(winPlayer.availableCards.isEmpty())
        }

        @Test
        fun loseDuel() {
            val losePlayer = player.loseDuel()

            assertEquals(user, losePlayer.user)
            assertTrue(losePlayer.availableCards.isEmpty())
            assertTrue(losePlayer.prizeCards.isEmpty())
        }

        @Test
        fun tieDuel() {
            val tie = player.tieDuel()

            assertEquals(user, tie.user)
            assertTrue(tie.availableCards.isEmpty())
            assertTrue(tie.prizeCards.contains(batman))
        }

    }

    @Nested
    inner class ResultMatch {

        @Test
        fun `User win match and add a victory`() {
            val stats = player.winMatch().user.stats
            assertEquals(1, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User tied game and add a tie`() {
            val stats = player.tieMatch().user.stats
            assertEquals(0, stats.winCount)
            assertEquals(1, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User loses match and add a loss`() {
            val stats = player.loseMatch().user.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(1, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User started game and add a in progress match`() {
            val stats = player.startMatch().user.stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(1, stats.inProgressCount)
        }

    }

}