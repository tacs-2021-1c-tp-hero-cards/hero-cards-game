package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class HumanTest {

    lateinit var human: Human

    @BeforeEach
    fun init() {
        human = Human(userName = "userName", fullName = "fullName", password = "password")
    }

    @Nested
    inner class ResultMatch {

        @Test
        fun `User win match and add a victory`() {
            val stats = human.winMatch().stats
            assertEquals(1, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User tied game and add a tie`() {
            val stats = human.tieMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(1, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User loses match and add a loss`() {
            val stats = human.loseMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(1, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `User started game and add a in progress match`() {
            val stats = human.startMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(1, stats.inProgressCount)
        }

        @Test
        fun `User ended game and dec a in progress match`() {
            val stats = human.startMatch().endMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

    }

}