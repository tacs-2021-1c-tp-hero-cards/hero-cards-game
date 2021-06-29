package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType

data class Match(
    val id: Long? = null,
    val players: List<Player>,
    val deck: DeckHistory,
    val status: MatchStatus,
    val duelHistoryList: List<DuelHistory> = emptyList()
) {

    fun resolveDuel(duelType: DuelType): Match {
        validateNotFinalizedOrCancelled()

        val player = players.first()
        val opponent = players.last()

        val playerCard = player.availableCards.first()
        val opponentCard = opponent.availableCards.first()

        val duelResult = playerCard.duel(opponentCard, duelType)

        val playerList = when (duelResult) {
            DuelResult.WIN -> listOf(player.winDuel(opponentCard), opponent.loseDuel())
            DuelResult.LOSE -> listOf(player.loseDuel(), opponent.winDuel(playerCard))
            else -> listOf(player.tieDuel(), opponent.tieDuel())
        }

        return this.copy(
            players = playerList,
            duelHistoryList = duelHistoryList.plus(DuelHistory(player, opponent, duelType, duelResult))
        )
    }

    fun updateTurn(): Match =
        this.copy(players = players.reversed())

    fun updateStatusMatch(): Match =
        if (players.any { it.availableCards.isEmpty() }) {
            this.copy(status = MatchStatus.FINALIZED, players = players.first().calculateWinPlayer(players.last()))
        } else {
            this
        }

    fun abortMatch(): Match {
        this.validateNotFinalizedOrCancelled()
        return copy(
            players = listOf(players.first().loseMatch(), players.last().winMatch()),
            status = MatchStatus.CANCELLED
        )
    }

    fun validateNotFinalizedOrCancelled() {
        if (this.status == MatchStatus.FINALIZED || this.status == MatchStatus.CANCELLED) {
            throw InvalidMatchException(id!!)
        }
    }

}