package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PlayerTest {

    private val playerId = 0L
    private val playerUserName = "userNameTest"

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val player = Player(id = playerId, userName = playerUserName, availableCards = listOf(batman))

    @Test
    fun winDuel(){
        val winPlayer = player.winDuel(flash)

        assertEquals(playerId, winPlayer.id)
        assertEquals(playerUserName, winPlayer.userName)
        assertTrue(winPlayer.prizeCards.contains(batman))
        assertTrue(winPlayer.prizeCards.contains(flash))
        assertTrue(winPlayer.availableCards.isEmpty())
    }

    @Test
    fun loseDuel(){
        val winPlayer = player.loseDuel()

        assertEquals(playerId, winPlayer.id)
        assertEquals(playerUserName, winPlayer.userName)
        assertTrue(winPlayer.availableCards.isEmpty())
        assertTrue(winPlayer.prizeCards.isEmpty())
    }

    @Test
    fun tieDuel(){
        val winPlayer = player.tieDuel()

        assertEquals(playerId, winPlayer.id)
        assertEquals(playerUserName, winPlayer.userName)
        assertTrue(winPlayer.availableCards.isEmpty())
        assertTrue(winPlayer.prizeCards.contains(batman))
    }

}