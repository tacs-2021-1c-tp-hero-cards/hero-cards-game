package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.IADifficulty
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class CardTest {

    private val powerstatsMock = mock(Powerstats::class.java)
    private val card = Card(0L, "cardNameTest", powerstatsMock, "cardImageUrl")

    private val otherPowerstatsMock = mock(Powerstats::class.java)
    private val otherCard = Card(1L, "otherCardNameTest", otherPowerstatsMock, "otherCardImageUrl")

    @Nested
    inner class DuelHeight {
        @Test
        fun win() {
            `when`(powerstatsMock.height).thenReturn(1)
            `when`(otherPowerstatsMock.height).thenReturn(0)
            assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.HEIGHT))
        }

        @Test
        fun lose() {
            `when`(powerstatsMock.height).thenReturn(0)
            `when`(otherPowerstatsMock.height).thenReturn(1)
            assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.HEIGHT))
        }

        @Test
        fun tie() {
            `when`(powerstatsMock.height).thenReturn(1)
            `when`(otherPowerstatsMock.height).thenReturn(1)
            assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.HEIGHT))
        }
    }

    @Nested
    inner class DuelWeight {
        @Test
        fun win() {
            `when`(powerstatsMock.weight).thenReturn(0)
            `when`(otherPowerstatsMock.weight).thenReturn(1)
            assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.WEIGHT))
        }

        @Test
        fun lose() {
            `when`(powerstatsMock.weight).thenReturn(1)
            `when`(otherPowerstatsMock.weight).thenReturn(0)
            assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.WEIGHT))
        }

        @Test
        fun tie() {
            `when`(powerstatsMock.weight).thenReturn(1)
            `when`(otherPowerstatsMock.weight).thenReturn(1)
            assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.WEIGHT))
        }
    }

    @Nested
    inner class DuelIntelligence {
        @Test
        fun duelIntelligence_win() {
            `when`(powerstatsMock.intelligence).thenReturn(1)
            `when`(otherPowerstatsMock.intelligence).thenReturn(0)
            assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.INTELLIGENCE))
        }

        @Test
        fun duelIntelligence_lose() {
            `when`(powerstatsMock.intelligence).thenReturn(0)
            `when`(otherPowerstatsMock.intelligence).thenReturn(1)
            assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.INTELLIGENCE))
        }

        @Test
        fun duelIntelligence_tie() {
            `when`(powerstatsMock.intelligence).thenReturn(1)
            `when`(otherPowerstatsMock.intelligence).thenReturn(1)
            assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.INTELLIGENCE))
        }
    }

    @Nested
    inner class DuelSpeed {
        @Test
        fun duelSpeed_win() {
            `when`(powerstatsMock.speed).thenReturn(1)
            `when`(otherPowerstatsMock.speed).thenReturn(0)
            assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.SPEED))
        }

        @Test
        fun duelSpeed_lose() {
            `when`(powerstatsMock.speed).thenReturn(0)
            `when`(otherPowerstatsMock.speed).thenReturn(1)
            assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.SPEED))
        }

        @Test
        fun duelSpeed_tie() {
            `when`(powerstatsMock.speed).thenReturn(1)
            `when`(otherPowerstatsMock.speed).thenReturn(1)
            assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.SPEED))
        }
    }

    @Nested
    inner class DuelPower {
        @Test
        fun win() {
            `when`(powerstatsMock.power).thenReturn(1)
            `when`(otherPowerstatsMock.power).thenReturn(0)
            assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.POWER))
        }

        @Test
        fun lose() {
            `when`(powerstatsMock.power).thenReturn(0)
            `when`(otherPowerstatsMock.power).thenReturn(1)
            assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.POWER))
        }

        @Test
        fun tie() {
            `when`(powerstatsMock.power).thenReturn(1)
            `when`(otherPowerstatsMock.power).thenReturn(1)
            assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.POWER))
        }
    }

    @Nested
    inner class DuelCombat {
        @Test
        fun win() {
            `when`(powerstatsMock.combat).thenReturn(1)
            `when`(otherPowerstatsMock.combat).thenReturn(0)
            assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.COMBAT))
        }

        @Test
        fun lose() {
            `when`(powerstatsMock.combat).thenReturn(0)
            `when`(otherPowerstatsMock.combat).thenReturn(1)
            assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.COMBAT))
        }

        @Test
        fun tie() {
            `when`(powerstatsMock.combat).thenReturn(1)
            `when`(otherPowerstatsMock.combat).thenReturn(1)
            assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.COMBAT))
        }
    }

    @Nested
    inner class DuelStrength {
        @Test
        fun win() {
            `when`(powerstatsMock.strength).thenReturn(1)
            `when`(otherPowerstatsMock.strength).thenReturn(0)
            assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.STRENGTH))
        }

        @Test
        fun lose() {
            `when`(powerstatsMock.strength).thenReturn(0)
            `when`(otherPowerstatsMock.strength).thenReturn(1)
            assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.STRENGTH))
        }

        @Test
        fun tie() {
            `when`(powerstatsMock.strength).thenReturn(1)
            `when`(otherPowerstatsMock.strength).thenReturn(1)
            assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.STRENGTH))
        }
    }

    @Nested
    inner class ValidateInvalidPowerstats {
        @Test
        fun `All powerstats are valid`() {
            val card = Card(0L, "cardNameTest", Powerstats(1, 2, 3, 4, 5, 6, 7), "cardImageUrl")
            assertFalse(card.validateInvalidPowerstats())
        }

        @Test
        fun `Height and speed are invalid powers`() {
            val card = Card(0L, "cardNameTest", Powerstats(-1, 2, 3, -1, 5, 6, 7), "cardImageUrl")
            assertTrue(card.validateInvalidPowerstats())
        }
    }

    @Nested
    inner class CalculateDuelTypeAccordingDifficulty {

        val powerstats = Powerstats(1, 2, 3, 4, 5, 6, 7)

        @Test
        fun `By choosing hard of difficulty, get the type of power duel with the highest value`() {
            val result =
                card.copy(powerstats = powerstats).calculateDuelTypeAccordingDifficulty(IADifficulty.HARD)
            assertEquals(DuelType.STRENGTH, result)
        }

        @Test
        fun `By choosing half of difficulty, get the type of power duel with the highest value`() {
            val result =
                card.copy(powerstats = powerstats).calculateDuelTypeAccordingDifficulty(IADifficulty.HALF)
            assertEquals(DuelType.SPEED, result)
        }

        @Test
        fun `By choosing easy of difficulty, get the type of power duel with the highest value`() {
            val result =
                card.copy(powerstats = powerstats).calculateDuelTypeAccordingDifficulty(IADifficulty.EASY)
            assertEquals(DuelType.HEIGHT, result)
        }

        @Test
        fun `By choosing random of difficulty, get the type of power duel with the highest value`() {
            val result =
                card.copy(powerstats = powerstats).calculateDuelTypeAccordingDifficulty(IADifficulty.HARD)
            assertTrue(DuelType.values().contains(result))
        }
    }
}