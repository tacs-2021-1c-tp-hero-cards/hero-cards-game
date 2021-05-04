package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
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
        return matchIntegration.saveMatch(match)
    }

    fun buildPlayers(usersId: List<String>, deck: Deck): List<Player> {
        var players = usersId.map {
            val searchUser = userService.searchUser(id = it.toLong()).first()
            Player(userName = searchUser.userName)
        }

        userService.searchUser()

        deck.cards.forEach {
            players.first().addAvailableCards(it)
            players = newShift(players)
        }

        return players
    }

    fun newShift(players: List<Player>): List<Player> = players.drop(1) + players.first()

    fun searchMatchById(matchId: String): Match = matchIntegration.getAllMatches().first { matchId.toLong() == it.id }

}