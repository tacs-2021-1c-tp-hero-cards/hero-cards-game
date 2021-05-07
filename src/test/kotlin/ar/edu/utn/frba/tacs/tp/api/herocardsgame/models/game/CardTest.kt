package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class CardTest {

    private val powerstatsMock = mock(Powerstats::class.java)
    private val card = Card(0L, "cardNameTest", powerstatsMock)

    private val otherPowerstatsMock = mock(Powerstats::class.java)
    private val otherCard = Card(1L, "otherCardNameTest", otherPowerstatsMock)

    @Test
    fun duelHeight_win() {
        `when`(powerstatsMock.height).thenReturn(1)
        `when`(otherPowerstatsMock.height).thenReturn(0)
        assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.HEIGHT))
    }

    @Test
    fun duelHeight_lose() {
        `when`(powerstatsMock.height).thenReturn(0)
        `when`(otherPowerstatsMock.height).thenReturn(1)
        assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.HEIGHT))
    }

    @Test
    fun duelHeight_tie() {
        `when`(powerstatsMock.height).thenReturn(1)
        `when`(otherPowerstatsMock.height).thenReturn(1)
        assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.HEIGHT))
    }

    @Test
    fun duelWeight_win() {
        `when`(powerstatsMock.weight).thenReturn(0)
        `when`(otherPowerstatsMock.weight).thenReturn(1)
        assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.WEIGHT))
    }

    @Test
    fun duelWeight_lose() {
        `when`(powerstatsMock.weight).thenReturn(1)
        `when`(otherPowerstatsMock.weight).thenReturn(0)
        assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.WEIGHT))
    }

    @Test
    fun duelWeight_tie() {
        `when`(powerstatsMock.weight).thenReturn(1)
        `when`(otherPowerstatsMock.weight).thenReturn(1)
        assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.WEIGHT))
    }

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

    @Test
    fun duelPower_win() {
        `when`(powerstatsMock.power).thenReturn(1)
        `when`(otherPowerstatsMock.power).thenReturn(0)
        assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.POWER))
    }

    @Test
    fun duelPower_lose() {
        `when`(powerstatsMock.power).thenReturn(0)
        `when`(otherPowerstatsMock.power).thenReturn(1)
        assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.POWER))
    }

    @Test
    fun duelPower_tie() {
        `when`(powerstatsMock.power).thenReturn(1)
        `when`(otherPowerstatsMock.power).thenReturn(1)
        assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.POWER))
    }

    @Test
    fun duelCombat_win() {
        `when`(powerstatsMock.combat).thenReturn(1)
        `when`(otherPowerstatsMock.combat).thenReturn(0)
        assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.COMBAT))
    }

    @Test
    fun duelCombat_lose() {
        `when`(powerstatsMock.combat).thenReturn(0)
        `when`(otherPowerstatsMock.combat).thenReturn(1)
        assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.COMBAT))
    }

    @Test
    fun duelCombat_tie() {
        `when`(powerstatsMock.combat).thenReturn(1)
        `when`(otherPowerstatsMock.combat).thenReturn(1)
        assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.COMBAT))
    }

    @Test
    fun duelStrength_win() {
        `when`(powerstatsMock.strength).thenReturn(1)
        `when`(otherPowerstatsMock.strength).thenReturn(0)
        assertEquals(DuelResult.WIN, card.duel(otherCard, DuelType.STRENGTH))
    }

    @Test
    fun duelStrength_lose() {
        `when`(powerstatsMock.strength).thenReturn(0)
        `when`(otherPowerstatsMock.strength).thenReturn(1)
        assertEquals(DuelResult.LOSE, card.duel(otherCard, DuelType.STRENGTH))
    }

    @Test
    fun duelStrength_tie() {
        `when`(powerstatsMock.strength).thenReturn(1)
        `when`(otherPowerstatsMock.strength).thenReturn(1)
        assertEquals(DuelResult.TIE, card.duel(otherCard, DuelType.STRENGTH))
    }

}