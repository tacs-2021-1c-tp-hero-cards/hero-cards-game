package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Player

class MatchEntity(id: Long? = null, match: Match) {

    val id: Long = id ?: match.id!!
    val playerIds: List<Long> = match.players.map { it.id!! }
    val deckId: Long = match.deck.id!!
    val status: String = match.status.name

    fun toModel(playerModels: List<Player>, deckModel: Deck): Match =
        Match(id, playerModels, deckModel, MatchStatus.valueOf(status))
}