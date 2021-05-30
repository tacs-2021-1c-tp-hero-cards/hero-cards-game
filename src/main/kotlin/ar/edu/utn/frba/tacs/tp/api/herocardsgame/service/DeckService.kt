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

    fun saveDeck(nameDeck: String, cardIds: List<String>): Deck {
        val cards = cardIds.map { cardIntegration.getCardById(it) }
        val deck = Deck(name = nameDeck, cards = cards)
        return deckIntegration.saveDeck(deck)
    }

    fun deleteDeck(deckId: String) {
        deckIntegration.deleteDeck(deckId.toLong())
    }

    fun addCardInDeck(deckId: String, cardId: String): Deck {
        val newDeck = deckIntegration.getDeckById(deckId.toLong()).addCard(cardIntegration.getCardById(cardId))
        return deckIntegration.saveDeck(newDeck)
    }

    fun deleteCardInDeck(deckId: String, cardId: String): Deck {
        val newDeck = deckIntegration.getDeckById(deckId.toLong()).removeCard(cardId.toLong())
        return deckIntegration.saveDeck(newDeck)
    }

    fun searchDeck(deckId: String? = null, deckName: String? = null): List<Deck> =
        deckIntegration.getDeckByIdOrName(deckId?.toLong(), deckName)

    fun updateDeck(deckId: String, newName: String?, cards: List<String>): Deck {
        val newDeck = deckIntegration
            .getDeckById(deckId.toLong())
            .rename(newName)
            .replaceCards(cards.map { cardIntegration.getCardById(it) })
        return deckIntegration.saveDeck(newDeck)
    }
}