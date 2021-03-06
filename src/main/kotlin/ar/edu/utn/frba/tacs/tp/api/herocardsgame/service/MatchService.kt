package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidTurnException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.MatchIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.UserIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.UserType
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.request.MatchUserResponse
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
                player = players.first(),
                opponent = players.last(),
                deck = DeckHistory(deck),
                status = MatchStatus.PENDING
            ).confirmMatchAutomatic(userType)
        )

        notificationClientService.notifyCreateMatch(userId, userType, newMatch)

        return newMatch
    }

    fun buildPlayers(token: String, userId: String, userType: UserType, deck: Deck): List<Player> {

        val player = Player(
            user = userIntegration.searchHumanUserByIdUserNameFullNameOrToken(token = token).firstOrNull()
                ?: throw ElementNotFoundException("human user", "token", token), true
        )

        val opponent = Player(user = userIntegration.getUserById(userId.toLong()))

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

    fun nextDuel(matchId: String, token: String, duelType: DuelType? = null): Match {
        val match = searchMatchById(matchId)
        validateUserTurn(match, token, duelType == null)
        val newMatch = matchIntegration.saveMatch(match.resolveDuel(duelType).updateTurn().updateStatusMatch())
        notificationClientService.notifyResultDuel(newMatch)
        return newMatch
    }

    fun abortMatch(matchId: String, token: String): Match {
        val match = searchMatchById(matchId)
        validateUserTurn(match, token)
        val newMatch = matchIntegration.saveMatch(match.abortMatch())
        notificationClientService.notifyAbort(newMatch)
        return newMatch
    }

    private fun validateUserTurn(match: Match, token: String?, isIA: Boolean = false) {
        val user = match.player.user
        if (user.userType == UserType.HUMAN &&
            (userIntegration.searchHumanUserByIdUserNameFullNameOrToken(user.id.toString())
                .first().token != token || isIA)
        ) {
            throw InvalidTurnException(token!!)
        }
    }

    fun matchConfirmation(matchId: String, confirmation: Boolean, token: String): Match {
        val matchFound = searchMatchById(matchId)
        val newMatch = matchIntegration.saveMatch(matchFound.confirmMatch(confirmation))
        notificationClientService.notifyConfirmMatch(token, newMatch)
        return newMatch
    }

    fun searchMatchByUserId(userId: String, onlyCreatedByUser: Boolean): List<MatchUserResponse> =
        matchIntegration.findMatchByUserId(userId.toLong(), onlyCreatedByUser)
            .map {
                val opponent = if (it.player.user.id == userId.toLong()) it.opponent else it.player
                MatchUserResponse(it, opponent)
            }

}