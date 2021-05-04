package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

internal class MatchServiceTest {

    private val matchIntegrationMock = mock(MatchIntegration::class.java)
    private val deckServiceMock = mock(DeckService::class.java)
    private val userServiceMock = mock(UserService::class.java)
    private val instance = MatchService(matchIntegrationMock, deckServiceMock, userServiceMock)

    private val userName = "userName"
    private val otherUserName = "otherUserName"

    private val user1 = User(0L, userName, "fullNameTest1", "passwordTest1")
    private val user2 = User(1L, otherUserName, "fullNameTest2", "passwordTest2")
    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()
    private val deck = Deck(0L, "nameDeckTest", listOf(batman, flash))

    @Test
    fun buildPlayers() {
        `when`(userServiceMock.searchUser(id = 0L)).thenReturn(listOf(user1))
        `when`(userServiceMock.searchUser(id = 1L)).thenReturn(listOf(user2))

        val players = instance.buildPlayers(listOf("0", "1"), deck)

        assertEquals(2, players.size)

        val player1 = players.first { it.userName == userName }
        val availableCards1 = player1.availableCards
        assertEquals(1, availableCards1.size)
        assertEquals(batman, availableCards1.first())

        val player2 = players.first { it.userName == otherUserName }
        val availableCards2 = player2.availableCards
        assertEquals(1, availableCards2.size)
        assertEquals(flash, availableCards2.first())
    }

    @Test
    fun newShift() {
        val player = Player(userName = userName)
        val otherPlayer = Player(userName = otherUserName)

        val players = instance.newShift(listOf(player, otherPlayer))
        assertEquals(otherPlayer, players.first())
        assertEquals(player, players.last())

        val otherPlayers = instance.newShift(players)
        assertEquals(player, otherPlayers.first())
        assertEquals(otherPlayer, otherPlayers.last())
    }
}