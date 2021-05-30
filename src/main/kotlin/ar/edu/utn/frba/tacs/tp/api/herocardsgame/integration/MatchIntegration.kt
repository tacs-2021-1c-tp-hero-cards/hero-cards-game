package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import org.springframework.stereotype.Component

@Component
class MatchIntegration(
    private val dao: Dao,
    private val deckIntegration: DeckIntegration,
    private val playerIntegration: PlayerIntegration
) {

    fun getMatchById(id: Long): Match {
        val matchEntity = dao.getMatchById(id)?: throw ElementNotFoundException("match", id.toString())
        val players = matchEntity.playerIds.map { playerIntegration.getPlayerById(it) }
        val deck = deckIntegration.getDeckById(matchEntity.deckId)

        return matchEntity.toModel(players, deck)
    }

    fun saveMatch(match: Match): Match {
        val savedPlayers = match.players.map { playerIntegration.savePlayer(it) }
        val savedDeck = deckIntegration.saveDeck(match.deck)
        val savedMatch = match.copy(players = savedPlayers, deck = savedDeck)

        return dao.saveMatch(savedMatch).toModel(savedPlayers, savedDeck)
    }

}
