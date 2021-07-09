package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PowerstatsTest {

    @Nested
    inner class CalculateInvalidPowers {

        @Test
        fun `All powers are valid`() {
            val powerstats = Powerstats(1, 2, 3, 4, 5, 6, 7)
            val invalidPowerstats = powerstats.calculateInvalidPowers()
            assertTrue(invalidPowerstats.isEmpty())
        }

        @Test
        fun `Powers with negative values are invalid`() {
            val powerstats = Powerstats(-1, 2, 3, -1, 5, 6, 7)
            val invalidPowerstats = powerstats.calculateInvalidPowers()
            assertFalse(invalidPowerstats.isEmpty())
            assertTrue(invalidPowerstats.contains(DuelType.HEIGHT))
            assertTrue(invalidPowerstats.contains(DuelType.SPEED))
        }

    }

    @Nested
    inner class CalculateBetterPowerstats {

        @Test
        fun `Best power is one with greatest value`() {
            val powerstats = Powerstats(1, 7, 2, 10, 3, 5, 4)
            val betterPowerstats = powerstats.calculateBetterPowerstats()
            assertEquals(DuelType.SPEED, betterPowerstats)
        }

        @Test
        fun `If best power is weight choose next power`() {
            val powerstats = Powerstats(1, 8, 2, 6, 3, 5, 4)
            val betterPowerstats = powerstats.calculateBetterPowerstats()
            assertEquals(DuelType.SPEED, betterPowerstats)
        }

    }

    @Nested
    inner class CalculateWorstPowerstats {

        @Test
        fun `Worst power is one with least value`() {
            val powerstats = Powerstats(1, 7, 2, 10, 3, 5, 4)
            val betterPowerstats = powerstats.calculateWorstPowerstats()
            assertEquals(DuelType.HEIGHT, betterPowerstats)
        }

        @Test
        fun `If Worst power is weight choose next power`() {
            val powerstats = Powerstats(1, 0, 2, 6, 3, 5, 4)
            val betterPowerstats = powerstats.calculateWorstPowerstats()
            assertEquals(DuelType.HEIGHT, betterPowerstats)
        }

    }

    @Nested
    inner class CalculateMediumPowerstats {

        @Test
        fun `Medium power is one with medium value`() {
            val powerstats = Powerstats(1, 7, 2, 10, 3, 5, 4)
            val betterPowerstats = powerstats.calculateMediumPowerstats()
            assertEquals(DuelType.POWER, betterPowerstats)
        }

        @Test
        fun `If medium power is weight choose next power`() {
            val powerstats = Powerstats(1, 3, 2, 6, 3, 5, 4)
            val betterPowerstats = powerstats.calculateMediumPowerstats()
            assertEquals(DuelType.POWER, betterPowerstats)
        }

    }

    @Test
    fun calculateRandomPowerstats(){
        val powerstats = Powerstats(1, 3, 2, 6, 3, 5, 4)
        val randomPowerstats = powerstats.calculateRandomPowerstats()
        assertTrue(DuelType.values().contains(randomPowerstats))
    }
}