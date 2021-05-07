package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class DuelStrategyTest {

    private val instance = DuelStrategy()

    @Test
    fun duelMAx_win() = assertEquals(DuelResult.WIN, instance.duelMax(1, 0))

    @Test
    fun duelMAx_lose() = assertEquals(DuelResult.LOSE, instance.duelMax(0, 1))

    @Test
    fun duelMAx_tie() = assertEquals(DuelResult.TIE, instance.duelMax(1, 1))

    @Test
    fun duelMin_win() = assertEquals(DuelResult.WIN, instance.duelMin(0, 1))

    @Test
    fun duelMin_lose() = assertEquals(DuelResult.LOSE, instance.duelMin(1, 0))

    @Test
    fun duelMin_tie() = assertEquals(DuelResult.TIE, instance.duelMin(1, 1))

}