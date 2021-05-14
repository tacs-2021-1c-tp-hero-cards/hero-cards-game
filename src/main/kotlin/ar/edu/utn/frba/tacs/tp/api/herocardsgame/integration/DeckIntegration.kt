package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import org.springframework.stereotype.Component

@Component
class DeckIntegration(
    private val deckMap: HashMap<Long, Deck> = hashMapOf()
) {

    fun getAllDeck(): List<Deck> = deckMap.values.toList()

    fun saveDeck(deck: Deck): Deck {
        val deckId = deck.id?: calculateId()
        val newDeck = deck.copy(id = deckId)
        deckMap[deckId] = newDeck
        return newDeck
    }

    fun deleteDeck(deckId: Long) = deckMap.remove(deckId)

    fun calculateId(): Long = deckMap.size.toLong()

}
