package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory

class DeckEntity(id: Long? = null, version: Long? = null, deck: Deck) {

    val id: Long = id ?: deck.id!!
    val version: Long = version ?: deck.version!!
    val name: String = deck.name
    val cardIds: List<Long> = deck.cards.map { it.id }
    val deckHistoryIds: List<Long> = deck.deckHistoryList.map { it.version!! }

    fun toModel(cardModels: List<Card>, deckHistoryModels: List<DeckHistory>): Deck =
        Deck(id, version, name, cardModels, deckHistoryModels)

}