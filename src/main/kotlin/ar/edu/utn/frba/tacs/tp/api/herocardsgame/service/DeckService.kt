package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.DeckIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import org.springframework.stereotype.Service

@Service
class DeckService(
    private val superHeroIntegration: SuperHeroIntegration,
    private val deckIntegration: DeckIntegration
) {

    fun addDeck(nameDeck: String, cardIds: List<String>): Deck =
        deckIntegration.saveDeck(buildDeck(nameDeck, cardIds))

    fun deleteDeck(deckId: String) {
        searchDeckById(deckId)
        deckIntegration.deleteDeck(deckId.toLong())
    }

    fun buildDeck(name: String, cardIds: List<String>): Deck {
        val cards = cardIds.map { superHeroIntegration.getCard(it) }
        return Deck(name = name, cards = cards)
    }

    fun addCardInDeck(deckId: String, cardId: String): Deck {
        val newDeck = searchDeckById(deckId).addCard(superHeroIntegration.getCard(cardId))
        return deckIntegration.saveDeck(newDeck)
    }

    fun deleteCardInDeck(deckId: String, cardId: String): Deck {
        val newDeck = searchDeckById(deckId).removeCard(cardId.toLong())
        return deckIntegration.saveDeck(newDeck)
    }

    fun searchDeckById(deckId: String): Deck {
        val decks = searchDeck(deckId = deckId)
        decks.ifEmpty { throw ElementNotFoundException("deck", deckId) }
        return decks.first()
    }

    fun searchDeck(deckId: String? = null, deckName: String? = null): List<Deck> =
        deckIntegration.getAllDeck()
            .filter { deckName == null || deckName == it.name }
            .filter { deckId == null || deckId == it.id.toString() }

    fun updateDeck(deckId: String, newName: String?, cards: List<String>): Deck {
        val newDeck = searchDeckById(deckId)
            .rename(newName)
            .replaceCards(cards.map { superHeroIntegration.getCard(it) })
        return deckIntegration.saveDeck(newDeck)
    }
}