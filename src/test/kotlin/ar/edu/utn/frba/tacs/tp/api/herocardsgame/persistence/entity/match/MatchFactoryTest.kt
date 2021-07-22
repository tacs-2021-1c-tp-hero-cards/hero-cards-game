package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.Stats
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.BuilderContextUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MatchFactoryTest {

    private val userFactory = UserFactory()
    private val instance = MatchFactory(userFactory)

    private val batman = BuilderContextUtils.buildBatman()
    private val flash = BuilderContextUtils.buildFlash()

    private val id: Long = 0L
    private val userName: String = "userName"
    private val fullName: String = "fullName"
    private val password: String = "password"
    private val token: String = "token"
    private val winCount: Int = 1
    private val tieCount: Int = 0
    private val loseCount: Int = 2
    private val inProgressCount: Int = 4

    private val user =
        Human(0L, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))
    private val player = Player(user, listOf(batman), emptyList())

    private val userOpponent =
        Human(1L, userName, fullName, password, token, Stats(winCount, tieCount, loseCount, inProgressCount))
    private val opponent = Player(userOpponent, listOf(flash), emptyList())

    private val deck = Deck(0L, "deckName", listOf(batman, flash))
    private val deckHistory = DeckHistory(deck)
    private val duelHistory =
        DuelHistory(0L, PlayerHistory(player), PlayerHistory(opponent), DuelType.POWER, DuelResult.WIN)

    @Test
    fun toEntityWithId() {
        val model = Match(id, player, opponent, deckHistory, MatchStatus.IN_PROGRESS, listOf(duelHistory))

        val entity = instance.toEntity(model)
        assertEquals(id, entity.id)
        assertTrue(entity.player.contains(userFactory.toEntity(user)))
        assertEquals(batman.id.toString(), entity.playerAvailableCardIds)
        assertEquals("", entity.playerPrizeCardIds)
        assertTrue(entity.player.contains(userFactory.toEntity(userOpponent)))
        assertEquals(flash.id.toString(), entity.opponentAvailableCardIds)
        assertEquals("", entity.opponentPrizeCardIds)
        assertEquals(id, entity.deckId)
        assertEquals(DeckHistoryEntity(deckHistory), entity.deckHistory)
        assertEquals(MatchStatus.IN_PROGRESS, entity.status)
        assertTrue(entity.duelHistory.contains(DuelHistoryEntity(duelHistory)))
    }

    @Test
    fun toEntityWithOutId() {
        val model = Match(null, player, opponent, deckHistory, MatchStatus.IN_PROGRESS, listOf(duelHistory))

        val entity = instance.toEntity(model)
        assertNull(entity.id)
        assertTrue(entity.player.contains(userFactory.toEntity(user)))
        assertTrue(entity.player.contains(userFactory.toEntity(userOpponent)))
        assertEquals(batman.id.toString(), entity.playerAvailableCardIds)
        assertEquals("", entity.playerPrizeCardIds)
        assertEquals(flash.id.toString(), entity.opponentAvailableCardIds)
        assertEquals("", entity.opponentPrizeCardIds)
        assertEquals(id, entity.deckId)
        assertEquals(DeckHistoryEntity(deckHistory), entity.deckHistory)
        assertEquals(MatchStatus.IN_PROGRESS, entity.status)
        assertTrue(entity.duelHistory.contains(DuelHistoryEntity(duelHistory)))
    }
}