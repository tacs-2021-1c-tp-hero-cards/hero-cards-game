package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDeckVersionException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

data class Deck(
    val id: Long? = null,
    val name: String,
    val cards: List<Card> = emptyList(),
    val deckHistoryList: List<DeckHistory> = emptyList()
) {

    fun mixCards(): Deck = copy(cards = cards.shuffled())

    fun updateDeck(newName: String? = null, newCards: List<Card> = emptyList()): Deck =
        Deck(
            id = id, name = newName ?: this.name,
            cards = newCards.ifEmpty { this.cards },
            deckHistoryList = deckHistoryList.plus(DeckHistory(this))
        )

    fun searchDeckVersion(deckVersion: Long): DeckHistory =
        deckHistoryList.firstOrNull { it.deckVersion == deckVersion } ?: throw InvalidDeckVersionException(
            this.id!!,
            deckVersion
        )

}