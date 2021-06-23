package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory

class DeckHistoryEntity(deckHistory: DeckHistory) {

    val id: Long = deckHistory.id
    val version: Long = deckHistory.version
    val name: String = deckHistory.name
    val cardIds: List<Long> = deckHistory.cards.map { it.id }

    fun toModel(cardModels: List<Card>): DeckHistory =
        DeckHistory(id, version, name, cardModels)

}