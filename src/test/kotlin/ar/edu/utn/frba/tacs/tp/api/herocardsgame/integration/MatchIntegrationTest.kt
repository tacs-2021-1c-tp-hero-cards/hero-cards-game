package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

internal class MatchIntegrationTest {

    private lateinit var dao: Dao
    private lateinit var instance: MatchIntegration

    private val deckIntegrationMock: DeckIntegration = mock(DeckIntegration::class.java)
    private val playerIntegrationMock: PlayerIntegration = mock(PlayerIntegration::class.java)

    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val userPlayer = User(0L, "userName", "fullName", "password")
    private val player = Player(0L, userPlayer, listOf(batman))

    private val userOpponent = User(1L, "userNameOpponent", "fullName", "password")
    private val opponent = Player(1L, userOpponent, listOf(batmanII))

    private val deck = Deck(0L, "deckNameTest", listOf(batman, batmanII))
    private val match = Match(0L, listOf(player, opponent), deck, MatchStatus.IN_PROGRESS)

    @BeforeEach
    fun init() {
        dao = Dao()
        instance = MatchIntegration(dao, deckIntegrationMock, playerIntegrationMock)
    }

    @Nested
    inner class GetMatchById {

        @Test
        fun `Get match by id`() {
            dao.saveMatch(match)

            `when`(playerIntegrationMock.getPlayerById(0L)).thenReturn(player)
            `when`(playerIntegrationMock.getPlayerById(1L)).thenReturn(opponent)
            `when`(deckIntegrationMock.getDeckById(0L)).thenReturn(deck)

            val found = instance.getMatchById(0L)

            assertEquals(match, found)
        }

        @Test
        fun `Get match by id but not exist`() {
            dao.saveMatch(match.copy(id = 1L))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(0L)
            }
        }

        @Test
        fun `Get match by id but no user exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getMatchById(0L)
            }
        }

    }

    @Test
    fun saveMatch() {
        `when`(playerIntegrationMock.savePlayer(player)).thenReturn(player)
        `when`(playerIntegrationMock.savePlayer(opponent)).thenReturn(opponent)
        `when`(deckIntegrationMock.saveDeck(deck)).thenReturn(deck)

        val saveMatch = instance.saveMatch(match)
        assertEquals(match, saveMatch)

        val found = dao.getMatchById(match.id!!)!!
        assertEquals(match.id, found.id)
        assertEquals(match.deck.id, found.deckId)
        assertEquals(match.status.name, found.status)
        assertTrue(found.playerIds.contains(player.id))
        assertTrue(found.playerIds.contains(opponent.id))
    }

}