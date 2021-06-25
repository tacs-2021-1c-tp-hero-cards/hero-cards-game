package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

internal class PlayerIntegrationTest{

    private lateinit var dao: Dao
    private lateinit var instance: PlayerIntegration

    private val userIntegrationMock: UserIntegration = Mockito.mock(UserIntegration::class.java)
    private val cardIntegrationMock: CardIntegration = Mockito.mock(CardIntegration::class.java)

    private val batman = BuilderContextUtils.buildBatman()
    private val batmanII = BuilderContextUtils.buildBatmanII()

    private val user = User(0L, "userName", "fullName", "password")
    private val player = Player(0L, user, listOf(batman), listOf(batmanII))
    private val playerHistory = PlayerHistory(0L, batman, listOf(batman, batmanII))


    @BeforeEach
    fun init() {
        dao = Dao()
        instance = PlayerIntegration(dao, userIntegrationMock, cardIntegrationMock)
    }

    @Nested
    inner class GetPlayerById {

        @Test
        fun `Get player by id`() {
            dao.savePLayer(player)

            `when`(userIntegrationMock.getUserById(0L)).thenReturn(user)
            `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById(batmanII.id.toString())).thenReturn(batmanII)

            val found = instance.getPlayerById(0L)

            assertEquals(player, found)
        }

        @Test
        fun `Get player by id but not exist`() {
            dao.savePLayer(player.copy(id = 1L))

            assertThrows(ElementNotFoundException::class.java) {
                instance.getPlayerById(0L)
            }
        }

        @Test
        fun `Get player by id but no user exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getPlayerById(0L)
            }
        }

    }

    @Test
    fun savePlayer() {
        `when`(userIntegrationMock.saveUser(user)).thenReturn(user)
        `when`(cardIntegrationMock.saveCard(batman)).thenReturn(batman)
        `when`(cardIntegrationMock.saveCard(batmanII)).thenReturn(batmanII)

        val savePlayer = instance.savePlayer(player)
        assertEquals(player, savePlayer)

        val found = dao.getPlayerById(player.id!!)!!
        assertEquals(player.id, found.id)
        assertEquals(player.id, found.userId)
        assertTrue(found.availableCardIds.contains(batman.id))
        assertTrue(found.prizeCardIds.contains(batmanII.id))

    }

    @Nested
    inner class GetPlayerHistoryById {

        @Test
        fun `Get player history by id`() {
            dao.savePlayerHistory(playerHistory)

            `when`(cardIntegrationMock.getCardById(batman.id.toString())).thenReturn(batman)
            `when`(cardIntegrationMock.getCardById(batmanII.id.toString())).thenReturn(batmanII)

            val found = instance.getPlayerHistoryById(0L)

            assertEquals(playerHistory, found)
        }

        @Test
        fun `Get player history by id but not exist`() {
            dao.savePlayerHistory(playerHistory)

            assertThrows(ElementNotFoundException::class.java) {
                instance.getPlayerHistoryById(1L)
            }
        }

        @Test
        fun `Get player history by id but no user exists in the system`() {
            assertThrows(ElementNotFoundException::class.java) {
                instance.getPlayerHistoryById(0L)
            }
        }

    }

    @Test
    fun savePlayerHistory() {
        val savePlayerHistory = instance.savePlayerHistory(playerHistory)
        assertEquals(playerHistory, savePlayerHistory)

        val found = dao.getPlayerHistoryById(playerHistory.id!!)!!
        assertEquals(playerHistory.id, found.id)
        assertTrue(found.availableCardIds.contains(batman.id))
        assertTrue(found.availableCardIds.contains(batmanII.id))
        assertTrue(found.prizeCardIds.isEmpty())
    }


}