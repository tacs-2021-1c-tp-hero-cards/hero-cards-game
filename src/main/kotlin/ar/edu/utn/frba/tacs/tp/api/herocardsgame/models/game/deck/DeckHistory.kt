package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

data class DeckHistory(
    val id: Long,
    val version: Long,
    val name: String,
    val cards: List<Card> = emptyList()
) {
    constructor(deck: Deck) : this(deck.id!!, deck.version!!, deck.name, deck.cards)
}