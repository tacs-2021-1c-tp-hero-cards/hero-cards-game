package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchFactory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.MatchRepository
import org.springframework.stereotype.Component

@Component
class MatchIntegration(
    private val cardIntegration: CardIntegration,
    private val factory: MatchFactory,
    private val repository: MatchRepository
) {

    fun getMatchById(id: Long): Match {
        val matchEntity = repository.getById(id) ?: throw ElementNotFoundException("match", "id", id.toString())
        val cards = matchEntity.deckHistory.cardIds.split(",").map { cardIntegration.getCardById(it) }
        return matchEntity.toModel(cards)
    }

    fun saveMatch(match: Match): Match =
        repository.save(factory.toEntity(match)).toModel(match.deck.cards)

}
