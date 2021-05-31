package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.DeckIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import org.springframework.stereotype.Service

@Service
class DeckService(
    private val cardIntegration: CardIntegration,
    private val deckIntegration: DeckIntegration
) {

    fun createDeck(nameDeck: String, cardIds: List<String>): Deck {
        val cards = cardIds.map { cardIntegration.getCardById(it) }
        val deck = Deck(name = nameDeck, cards = cards)
        return deckIntegration.saveDeck(deck)
    }

    fun deleteDeck(deckId: String) {
        deckIntegration.deleteDeck(deckId.toLong())
    }

    fun searchDeck(deckId: String? = null, deckName: String? = null): List<Deck> =
        deckIntegration.getDeckByIdOrName(deckId?.toLong(), deckName)

    fun updateDeck(deckId: String, name: String?, cards: List<String>): Deck {
        val deck = deckIntegration.getDeckById(deckId.toLong())
        deckIntegration.saveDeck(deck.copy(usable = false))

        val newDeck = deck.updateDeck(name, cards.map { cardIntegration.getCardById(it) })
        return deckIntegration.saveDeck(newDeck)
    }
}