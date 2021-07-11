package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
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
    private val userIntegration: UserIntegration,
    private val notificationClientService: NotificationClientService
) {

    fun createMatch(token: String, userId: String, userType: UserType, deckId: String): Match {
        val deck =
            deckService.searchDeck(deckId = deckId).firstOrNull() ?: throw ElementNotFoundException(
                "deck",
                "id",
                deckId
            )
        val players = buildPlayers(token, userId, userType, deck)
        val newMatch = matchIntegration.saveMatch(
            Match(
                players = players,
                deck = DeckHistory(deck),
                status = MatchStatus.PENDING
            )
        )

        notificationClientService.notifyCreateMatch(userId, userType, newMatch)

        return newMatch
    }

    fun buildPlayers(token: String, userId: String, userType: UserType, deck: Deck): List<Player> {

        val player = Player(
            user = userIntegration.searchHumanUserByIdUserNameFullNameOrToken(token = token).firstOrNull()
                ?: throw ElementNotFoundException("human user", "token", token)
        )

        val opponent = Player(user = userIntegration.getUserById(userId.toLong(), userType))

        var players = listOf(player, opponent)

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

    fun nextDuel(matchId: String, token: String? = null, duelType: DuelType? = null): Match {
        val match = searchMatchById(matchId)
        validateUserDuel(match, token)
        val newMatch = matchIntegration.saveMatch(match.resolveDuel(duelType).updateTurn().updateStatusMatch())

        notificationClientService.notifyResultDuel(newMatch)

        return newMatch
    }

    fun abortMatch(matchId: String, token: String): Match {
        val match = searchMatchById(matchId)
        validateUserDuel(match, token)
        val newMatch = match.abortMatch()
        return matchIntegration.saveMatch(newMatch)
    }

    private fun validateUserDuel(match: Match, token: String?) {
        val user = match.players.first().user

        if (user.userType == UserType.HUMAN && (userIntegration.getUserById(
                user.id!!,
                user.userType
            ) as Human).token != token
        ) {
            throw InvalidTurnException(token!!)
        }
    }

    fun matchConfirmation(matchId: String, confirmation: Boolean): Match {
        val matchFound = searchMatchById(matchId)
        return matchIntegration.saveMatch(matchFound.confirmMatch(confirmation))
    }
}