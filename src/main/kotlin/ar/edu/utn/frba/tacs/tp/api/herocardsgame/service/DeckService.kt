package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.SuperHeroIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import org.springframework.stereotype.Service

@Service
class DeckService(
    private val superHeroIntegration: SuperHeroIntegration
) {

    private val deckMap: HashMap<Long, Deck> = hashMapOf()

    fun addDeck(nameDeck: String, cardIds: List<String>): Deck {
        val id = deckMap.size.toLong()
        val deck = buildDeck(id, nameDeck, cardIds)

        deckMap[id] = deck

        return deck
    }

    private fun buildDeck(id: Long, name: String, cardIds: List<String>): Deck {
        val cards = cardIds.map { superHeroIntegration.getCard(it) }
        return Deck(id, name, cards)
    }

    fun deleteDeck(deckId: String){
        deckMap.remove(deckId.toLong())
    }

    fun getAllDeck(): List<Deck> = deckMap.values.toList()

    fun searchDeck(deckId: String? = null, deckName: String? = null): List<Deck> =
        getAllDeck()
            .filter { deckName == null || deckName == it.name }
            .filter { deckId == null || deckId == it.id.toString() }

    fun addCardInDeck(deckId: String, cardId: String){
        val decks  = searchDeck(deckId = deckId)
        decks.ifEmpty { throw ElementNotFoundException("deck", deckId) }
        decks.first().addCard(superHeroIntegration.getCard(cardId))
    }

    fun deleteCardInDeck(deckId: String, cardId: String){
        val decks  = searchDeck(deckId = deckId)
        decks.ifEmpty { throw ElementNotFoundException("deck", deckId) }
        decks.first().removeCard(cardId.toLong())
    }

}