package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import javax.persistence.*

@Entity
@Table(name = "DECK")
data class DeckEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val cardIds: String,
    @OneToMany(cascade = [CascadeType.ALL])
    val deckHistory: List<DeckHistoryEntity>
){
    constructor(deck: Deck) : this(
        id = deck.id,
        name = deck.name,
        cardIds = deck.cards.joinToString(separator = ",") { it.id.toString() },
        deckHistory = deck.deckHistoryList.map { DeckHistoryEntity(it) }
    )

    fun toModel(cardModels: List<Card>): Deck {
        val cards = cardIds.split(",")
            .map { cardId -> cardModels.first { card -> card.id == cardId.toLong() } }

        val deckHistories = deckHistory.map {
            val cards = it.cardIds.split(",")
                .map { cardId -> cardModels.first { card -> card.id == cardId.toLong() } }
            it.toModel(id!!, cards)
        }

        return Deck(id, name, cards, deckHistories)
    }
}