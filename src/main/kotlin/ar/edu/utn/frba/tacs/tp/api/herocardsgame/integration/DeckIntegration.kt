package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import org.springframework.stereotype.Component

@Component
class DeckIntegration(
    private val deckMap: HashMap<Long, Deck> = hashMapOf()
) {

    fun getAllDeck(): List<Deck> = deckMap.values.toList()

    fun saveDeck(deckId: Long = calculateId(), deck: Deck): Deck {
        deck.updateId(deckId)
        deckMap[deckId] = deck
        return deck
    }

    fun deleteDeck(deckId: Long) =
        if (deckMap.containsKey(deckId)) {
            deckMap.remove(deckId)
        } else {
            throw ElementNotFoundException("deck", deckId.toString())
        }

    fun calculateId(): Long = deckMap.size.toLong()

}
