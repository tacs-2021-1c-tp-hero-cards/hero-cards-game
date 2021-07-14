package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidMatchException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType

data class Match(
    val id: Long? = null,
    val player: Player,
    val opponent: Player,
    val deck: DeckHistory,
    val status: MatchStatus,
    val duelHistoryList: List<DuelHistory> = emptyList()
) {

    fun resolveDuel(duelType: DuelType? = null): Match {
        validateNotFinalizedOrCancelled()

        val playerCard = player.availableCards.first()
        val opponentCard = opponent.availableCards.first()

        val newDuelType = duelType ?: calculateDuelTypeAccordingDifficulty(player.user as IA, playerCard)

        val duelResult = playerCard.duel(opponentCard, newDuelType)

        val playerList = when (duelResult) {
            DuelResult.WIN -> listOf(player.winDuel(opponentCard), opponent.loseDuel())
            DuelResult.LOSE -> listOf(player.loseDuel(), opponent.winDuel(playerCard))
            else -> listOf(player.tieDuel(), opponent.tieDuel())
        }

        return this.copy(
            player = playerList.first(),
            opponent = playerList.last(),
            duelHistoryList = duelHistoryList.plus(DuelHistory(player, opponent, newDuelType, duelResult))
        )
    }

    private fun calculateDuelTypeAccordingDifficulty(iaUser: IA, card: Card): DuelType =
        card.calculateDuelTypeAccordingDifficulty(iaUser.difficulty)

    fun updateTurn(): Match = copy(player = opponent, opponent = player)

    fun updateStatusMatch(): Match =
        if (player.availableCards.isEmpty() || opponent.availableCards.isEmpty()) {
            val playersResult = player.calculateWinPlayer(opponent)
            this.copy(status = MatchStatus.FINALIZED, player = playersResult.first(), opponent = playersResult.last())
        } else {
            this
        }

    fun abortMatch(): Match {
        this.validateNotFinalizedOrCancelled()
        return copy(
            player = player.loseMatch(),
            opponent = opponent.winMatch(),
            status = MatchStatus.CANCELLED
        )
    }

    fun validateNotFinalizedOrCancelled() {
        if (this.status == MatchStatus.FINALIZED || this.status == MatchStatus.CANCELLED) {
            throw InvalidMatchException(id!!, "finished")
        }
    }

    fun confirmMatch(confirmation: Boolean): Match {
        if (this.status != MatchStatus.PENDING) {
            throw InvalidMatchException(id!!, this.status.name)
        }

        return if (confirmation) {
            copy(player = player.startMatch(), opponent = opponent.startMatch(), status = MatchStatus.IN_PROGRESS)
        } else {
            copy(status = MatchStatus.CANCELLED)
        }
    }

}