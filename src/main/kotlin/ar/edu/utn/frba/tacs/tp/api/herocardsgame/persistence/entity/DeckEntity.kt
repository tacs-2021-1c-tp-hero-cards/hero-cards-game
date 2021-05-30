package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck

class DeckEntity(id: Long? = null, deck: Deck) {

    val id: Long = id ?: deck.id!!
    val name: String = deck.name
    val cardIds: List<Long> = deck.cards.map { it.id }

    fun toModel(cardModels: List<Card>): Deck =
        Deck(id, name, cardModels)

}