package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player

class MatchEntity(id: Long? = null, match: Match) {

    val id: Long = id ?: match.id!!
    val playerId: Long = match.player.id!!
    val opponentId: Long = match.opponent.id!!
    val deckId: Long = match.deck.deckId
    val deckVersion: Long = match.deck.deckVersion!!
    val status: String = match.status.name
    val duelHistoryIds: List<Long> = match.duelHistoryList.map { it.id!! }

    fun toModel(
        playerModel: Player,
        opponentModel: Player,
        deckModel: DeckHistory,
        duelHistoryModels: List<DuelHistory>
    ): Match =
        Match(id, playerModel, opponentModel, deckModel, MatchStatus.valueOf(status), duelHistoryModels)
}