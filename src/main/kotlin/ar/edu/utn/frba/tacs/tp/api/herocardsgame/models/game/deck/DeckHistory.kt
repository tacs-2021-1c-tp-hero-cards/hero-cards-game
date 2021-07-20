package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

data class DeckHistory(
    val deckId: Long,
    val deckVersion: Long? = null,
    val name: String,
    val cards: List<Card> = emptyList()
) {
    constructor(deck: Deck) : this(deckId = deck.id!!, name = deck.name, cards = deck.cards)
}