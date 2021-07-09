package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class IATest{

    lateinit var ia: IA

    @BeforeEach
    fun init() {
        ia = IA(userName = "userName", difficulty = IADifficulty.HARD)
    }

    @Nested
    inner class ResultMatch {

        @Test
        fun `IA win match and add a victory`() {
            val stats = ia.winMatch().stats
            assertEquals(1, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `IA tied game and add a tie`() {
            val stats = ia.tieMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(1, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `IA loses match and add a loss`() {
            val stats = ia.loseMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(1, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

        @Test
        fun `IA started game and add a in progress match`() {
            val stats = ia.startMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(1, stats.inProgressCount)
        }

        @Test
        fun `IA ended game and dec a in progress match`() {
            val stats = ia.startMatch().endMatch().stats
            assertEquals(0, stats.winCount)
            assertEquals(0, stats.tieCount)
            assertEquals(0, stats.loseCount)
            assertEquals(0, stats.inProgressCount)
        }

    }

}