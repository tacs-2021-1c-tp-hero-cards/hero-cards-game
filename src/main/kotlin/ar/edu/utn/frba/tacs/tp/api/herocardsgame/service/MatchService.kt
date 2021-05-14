package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchIntegration: MatchIntegration,
    private val deckService: DeckService,
    private val userService: UserService
) {

    fun createMatch(usersId: List<String>, deckId: String): Match {
        val deck = deckService.searchDeckById(deckId)
        val players = buildPlayers(usersId, deck)
        val match = Match(players = players, deck = deck, status = MatchStatus.IN_PROGRESS)
        return matchIntegration.saveMatch(match = match)
    }

    fun buildPlayers(usersId: List<String>, deck: Deck): List<Player> {
        var players = usersId.map {
            val searchUser = userService.searchUserById(it)
            Player(userName = searchUser.userName, id = searchUser.id!!)
        }

        deck.mixCards().cards.forEach {
            players = dealCards(players, it)
        }

        return players
    }

    fun dealCards(players: List<Player>, card: Card): List<Player> {
        val player = players.first()
        val availableCards = player.availableCards
        return players.drop(1) + player.copy(availableCards = availableCards + card)
    }

    fun searchMatchById(matchId: String): Match {
        val matches = matchIntegration.getAllMatches().filter { matchId.toLong() == it.id }
        matches.ifEmpty { throw ElementNotFoundException("match", matchId) }
        return matches.first()
    }

    fun nextDuel(matchId: String, token: String, duelType: DuelType): Match {
        val match = searchMatchById(matchId)

        match.validateNotFinalizedOrCancelled()
        validateUserDuel(match, token)

        return matchIntegration.saveMatch(match.resolveDuel(duelType).updateTurn().updateStatusMatch())
    }

    private fun validateUserDuel(match: Match, token: String) {
        if (userService.searchUser(id = match.players.first().id, token = token).isEmpty()) {
            throw InvalidTurnException(token)
        }
    }

    fun abortMatch(matchId: String, token: String): Match {
        val match = searchMatchById(matchId)

        match.validateNotFinalizedOrCancelled()
        validateUserDuel(match, token)

        return matchIntegration.saveMatch(match.abortMatch())
    }
}