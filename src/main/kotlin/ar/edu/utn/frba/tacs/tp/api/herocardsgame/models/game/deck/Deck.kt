package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidDeckVersionException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

data class Deck(
    val id: Long? = null,
    val version: Long? = null,
    val name: String,
    val cards: List<Card> = emptyList(),
    val deckHistoryList: List<DeckHistory> = emptyList()
) {

    fun mixCards(): Deck = copy(cards = cards.shuffled())

    fun updateDeck(newName: String?, newCards: List<Card>): Deck =
        Deck(
            id = id, name = newName ?: this.name,
            cards = newCards.ifEmpty { this.cards },
            deckHistoryList = deckHistoryList.plus(DeckHistory(this))
        )

    fun searchDeckVersion(deckVersion: Long): DeckHistory {
        if (this.version == deckVersion) {
            return DeckHistory(id!!, version, name, cards)
        }

        return deckHistoryList.firstOrNull { it.version == deckVersion } ?: throw InvalidDeckVersionException(
            this.id!!,
            deckVersion
        )
    }

}