package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserFactory
import org.springframework.stereotype.Component

@Component
class MatchFactory(private val userFactory: UserFactory) {

    fun toEntity(match: Match) =
        MatchEntity(
            id = match.id,
            player = listOf(userFactory.toEntity(match.player.user),userFactory.toEntity(match.opponent.user)),
            playerIdTurn = match.player.user.id!!,
            playerAvailableCardIds = match.player.availableCards.joinToString(separator = ",") { it.id.toString() },
            playerPrizeCardIds = match.player.prizeCards.joinToString(separator = ",") { it.id.toString() },
            opponentAvailableCardIds = match.opponent.availableCards.joinToString(
                separator =
                ","
            ) { it.id.toString() },
            opponentPrizeCardIds = match.opponent.prizeCards.joinToString(separator = ",") { it.id.toString() },
            deckId = match.deck.deckId,
            deckHistory = DeckHistoryEntity(match.deck),
            status = match.status,
            duelHistory = match.duelHistoryList.map { DuelHistoryEntity(it) }
        )
}