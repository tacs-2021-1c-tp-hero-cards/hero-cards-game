package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory

class MatchEntity(id: Long? = null, match: Match) {

    val id: Long = id ?: match.id!!
    val playerIds: List<Long> = match.players.map { it.id!! }
    val deckId: Long = match.deck.id
    val deckVersion: Long = match.deck.version
    val status: String = match.status.name
    val duelHistoryIds: List<Long> = match.duelHistoryList.map { it.id!! }

    fun toModel(playerModels: List<Player>, deckModel: DeckHistory, duelHistoryModels: List<DuelHistory>): Match =
        Match(id, playerModels, deckModel, MatchStatus.valueOf(status), duelHistoryModels)
}