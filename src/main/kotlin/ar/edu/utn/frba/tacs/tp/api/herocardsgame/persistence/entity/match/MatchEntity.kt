package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.MatchStatus
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.UserEntity
import javax.persistence.*

@Entity
@Table(name = "MATCHES")
data class MatchEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToMany(cascade = [CascadeType.MERGE])
    val player: List<UserEntity>,
    val playerIdTurn: Long,
    val playerIdCreatedMatch: Long,
    val playerAvailableCardIds: String,
    val playerPrizeCardIds: String,
    val opponentAvailableCardIds: String,
    val opponentPrizeCardIds: String,
    val deckId: Long,
    @ManyToOne(cascade = [CascadeType.ALL])
    val deckHistory: DeckHistoryEntity,
    @Enumerated(value = EnumType.STRING)
    val status: MatchStatus,
    @OneToMany(cascade = [CascadeType.ALL])
    val duelHistory: List<DuelHistoryEntity> = emptyList()
) {
    fun toModel(cardModels: List<Card>): Match {
        val playerModel = toPlayerModel(
            player.first { it.id == playerIdTurn },
            playerAvailableCardIds,
            playerPrizeCardIds,
            cardModels
        )
        val opponentModel = toPlayerModel(
            player.first { it.id != playerIdTurn },
            opponentAvailableCardIds,
            opponentPrizeCardIds,
            cardModels
        )
        val deckHistoryModel = deckHistory.toModel(deckId, cardModels)
        val duelHistoryModel = duelHistory.map { it.toModel(cardModels) }
        return Match(id, playerModel, opponentModel, deckHistoryModel, status, duelHistoryModel)
    }

    private fun toPlayerModel(
        user: UserEntity,
        availableCardIds: String,
        prizeCardIds: String,
        cardModels: List<Card>
    ): Player {
        val availableCards = availableCardIds.split(",")
            .filterNot { it.isBlank() }
            .map { cardId -> cardModels.first { card -> card.id == cardId.toLong() } }

        val priceCards = prizeCardIds.split(",")
            .filterNot { it.isBlank() }
            .map { cardId -> cardModels.first { card -> card.id == cardId.toLong() } }

        return Player(user.toModel(), user.id == playerIdCreatedMatch, availableCards, priceCards)
    }

}