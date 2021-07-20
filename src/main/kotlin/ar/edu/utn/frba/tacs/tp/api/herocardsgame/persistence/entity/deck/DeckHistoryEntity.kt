package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import javax.persistence.*

@Entity
@Table(name = "DECK_HISTORY")
data class DeckHistoryEntity(
    @Id
    @GeneratedValue
    val id: Long? = null,
    val name: String,
    val cardIds: String,
) {
    constructor(deckHistory: DeckHistory) : this(
        id =  deckHistory.deckVersion,
        name =  deckHistory.name,
        cardIds =  deckHistory.cards.joinToString(separator = ",") { it.id.toString() }
    )

    fun toModel(deckId: Long, cardModels: List<Card>): DeckHistory =
        DeckHistory(deckId, id, name, cardModels)
}