package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchIntegration: MatchIntegration,
    private val deckService: DeckService,
    private val userIntegration: UserIntegration
) {

    fun createMatch(usersId: List<String>, deckId: String): Match {
        val deck =
            deckService.searchDeck(deckId = deckId).firstOrNull() ?: throw ElementNotFoundException("deck", "id", deckId)
        val players = buildPlayers(usersId, deck)
        val newMatch = Match(players = players, deck = DeckHistory(deck), status = MatchStatus.IN_PROGRESS)
        return matchIntegration.saveMatch(newMatch)
    }

    fun buildPlayers(usersId: List<String>, deck: Deck): List<Player> {
        var players = usersId.map {
            Player(human = userIntegration.getHumanUserById(it.toLong())).startMatch()
        }

        deck.mixCards().cards.forEach {
            players = dealCards(players, it)
        }

        return players.shuffled()
    }

    fun dealCards(players: List<Player>, card: Card): List<Player> {
        val player = players.first()
        val availableCards = player.availableCards
        return players.drop(1) + player.copy(availableCards = availableCards + card)
    }

    fun searchMatchById(matchId: String): Match = matchIntegration.getMatchById(matchId.toLong())

    fun nextDuel(matchId: String, token: String, duelType: DuelType): Match {
        val match = searchMatchById(matchId)
        validateUserDuel(match, token)
        val newMatch = match.resolveDuel(duelType).updateTurn().updateStatusMatch()
        return matchIntegration.saveMatch(newMatch)
    }

    fun abortMatch(matchId: String, token: String): Match {
        val match = searchMatchById(matchId)
        validateUserDuel(match, token)
        val newMatch = match.abortMatch()
        return matchIntegration.saveMatch(newMatch)
    }

    private fun validateUserDuel(match: Match, token: String) {
        if (userIntegration.getHumanUserById(match.players.first().human.id!!).token != token) {
            throw InvalidTurnException(token)
        }
    }
}