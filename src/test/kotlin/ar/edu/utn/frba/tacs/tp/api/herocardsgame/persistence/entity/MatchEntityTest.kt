package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MatchEntityTest {

    val id = 0L

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val userName: String = "userName"
    private val fullName: String = "fullName"
    private val password: String = "password"
    private val token: String = "token"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4

    private val user =
        User(0L, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))

    private val player = Player(0L, user, listOf(batman), emptyList())

    private val userOpponent =
        User(1L, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))

    private val opponent = Player(1L, userOpponent, listOf(flash), emptyList())

    val deck = Deck(0L, "deckName", listOf(batman, flash))

    @Test
    fun toEntityWithId() {
        val model = Match(id, listOf(player, opponent), deck, MatchStatus.IN_PROGRESS)

        val entity = MatchEntity(match = model)
        assertEquals(id, entity.id)
        assertEquals(id, entity.deckId)
        assertTrue(entity.playerIds.contains(0L))
        assertTrue(entity.playerIds.contains(1L))
        assertEquals(MatchStatus.IN_PROGRESS.name, entity.status)
    }

    @Test
    fun toEntityWithOutId() {
        val model = Match(null, listOf(player, opponent), deck, MatchStatus.IN_PROGRESS)

        val entity = MatchEntity(1L, model)
        assertEquals(1L, entity.id)
        assertEquals(id, entity.deckId)
        assertTrue(entity.playerIds.contains(0L))
        assertTrue(entity.playerIds.contains(1L))
        assertEquals(MatchStatus.IN_PROGRESS.name, entity.status)
    }

    @Test
    fun toModel() {
        val entity =
            MatchEntity(
                id,
                Match(null, listOf(player, opponent), deck, MatchStatus.IN_PROGRESS)
            )

        val model = entity.toModel(listOf(player, opponent), deck)
        assertEquals(id, model.id)
        assertEquals(deck, model.deck)
        assertEquals(MatchStatus.IN_PROGRESS, model.status)
        assertTrue(model.players.contains(player))
        assertTrue(model.players.contains(opponent))
    }
}