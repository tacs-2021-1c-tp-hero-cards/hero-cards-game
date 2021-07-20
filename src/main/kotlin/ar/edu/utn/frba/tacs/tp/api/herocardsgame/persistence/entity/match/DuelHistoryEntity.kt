package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelResult
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.service.duel.DuelType
import javax.persistence.*

@Entity
@Table(name = "DUEL_HISTORY")
data class DuelHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val playerAvailableCardIds: String,
    val playerPrizeCardIds: String,
    val opponentAvailableCardIds: String,
    val opponentPrizeCardIds: String,
    @Enumerated(value = EnumType.STRING)
    val duelType: DuelType,
    @Enumerated(value = EnumType.STRING)
    val duelResult: DuelResult
) {
    constructor(duelHistory: DuelHistory) : this(
        id = duelHistory.id,
        playerAvailableCardIds = duelHistory.player.availableCards.joinToString(separator = ",") { it.id.toString() },
        playerPrizeCardIds = duelHistory.player.prizeCards.joinToString(separator = ",") { it.id.toString() },
        opponentAvailableCardIds = duelHistory.opponent.availableCards.joinToString(separator = ",") { it.id.toString() },
        opponentPrizeCardIds = duelHistory.opponent.prizeCards.joinToString(separator = ",") { it.id.toString() },
        duelType = duelHistory.duelType,
        duelResult = duelHistory.duelResult
    )

    fun toModel(cardModels: List<Card>): DuelHistory {
        val player = toPlayerHistoryModel(playerAvailableCardIds, playerPrizeCardIds, cardModels)
        val opponent = toPlayerHistoryModel(opponentAvailableCardIds, opponentPrizeCardIds, cardModels)
        return DuelHistory(id, player, opponent, duelType, duelResult)
    }

    private fun toPlayerHistoryModel(
        availableCardIds: String,
        prizeCardIds: String,
        cardModels: List<Card>
    ): PlayerHistory {
        val availableCards = availableCardIds.split(",")
            .filterNot { it.isBlank() }
            .map { cardId -> cardModels.first { card -> card.id == cardId.toLong() } }

        val prizeCards = prizeCardIds.split(",")
            .filterNot { it.isBlank() }
            .map { cardId -> cardModels.first { card -> card.id == cardId.toLong() } }

        return PlayerHistory(availableCards.first(), availableCards, prizeCards)
    }

}