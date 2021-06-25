package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MatchEntityTest {

    private val id = 0L

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

    private val deck = Deck(0L, 0L, "deckName", listOf(batman, flash))
    private val deckHistory = DeckHistory(deck)

    private val duelHistory =
        DuelHistory(0L, PlayerHistory(player), PlayerHistory(opponent), DuelType.POWER, DuelResult.WIN)

    @Test
    fun toEntityWithId() {
        val model = Match(id, listOf(player, opponent), deckHistory, MatchStatus.IN_PROGRESS, listOf(duelHistory))

        val entity = MatchEntity(match = model)
        assertEquals(id, entity.id)
        assertEquals(id, entity.deckId)
        assertTrue(entity.playerIds.contains(0L))
        assertTrue(entity.playerIds.contains(1L))
        assertEquals(MatchStatus.IN_PROGRESS.name, entity.status)
        assertTrue(entity.duelHistoryIds.contains(0L))
    }

    @Test
    fun toEntityWithOutId() {
        val model = Match(null, listOf(player, opponent), deckHistory, MatchStatus.IN_PROGRESS, listOf(duelHistory))

        val entity = MatchEntity(1L, model)
        assertEquals(1L, entity.id)
        assertEquals(id, entity.deckId)
        assertTrue(entity.playerIds.contains(0L))
        assertTrue(entity.playerIds.contains(1L))
        assertEquals(MatchStatus.IN_PROGRESS.name, entity.status)
        assertTrue(entity.duelHistoryIds.contains(0L))
    }

    @Test
    fun toModel() {
        val entity =
            MatchEntity(
                id,
                Match(null, listOf(player, opponent), deckHistory, MatchStatus.IN_PROGRESS, listOf(duelHistory))
            )

        val model = entity.toModel(listOf(player, opponent), deckHistory, listOf(duelHistory))
        assertEquals(id, model.id)
        assertEquals(deckHistory, model.deck)
        assertEquals(MatchStatus.IN_PROGRESS, model.status)
        assertTrue(model.players.contains(player))
        assertTrue(model.players.contains(opponent))

        val duelHistory = model.duelHistoryList.first()
        assertEquals(0L, duelHistory.id)
        assertEquals(PlayerHistory(player), duelHistory.player)
        assertEquals(PlayerHistory(opponent), duelHistory.opponent)
        assertEquals(DuelType.POWER, duelHistory.duelType)
        assertEquals(DuelResult.WIN, duelHistory.duelResult)
    }
}