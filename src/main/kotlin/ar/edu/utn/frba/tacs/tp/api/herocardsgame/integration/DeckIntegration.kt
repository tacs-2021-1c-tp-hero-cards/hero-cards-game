package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import org.springframework.stereotype.Component

@Component
class DeckIntegration(private val dao: Dao, private val cardIntegration: CardIntegration) {

    fun getDeckById(id: Long): Deck =
        getDeckByIdOrName(id = id).firstOrNull() ?: throw ElementNotFoundException("deck", id.toString())

    fun getDeckByIdOrName(id: Long? = null, name: String? = null): List<Deck> {
        val deckEntities = dao.getAllDeck()
            .filter { name == null || name == it.name }
            .filter { id == null || id == it.id }

        val allCards = deckEntities
            .flatMap { it.cardIds }
            .distinct()
            .map { cardIntegration.getCardById(it.toString()) }

        return deckEntities.map {
            val cards = it.cardIds.map { cardId -> allCards.first { card -> card.id == cardId } }
            it.toModel(cards)
        }
    }

    fun saveDeck(deck: Deck): Deck {
        val savedCards = deck.cards.map { cardIntegration.saveCard(it) }
        val savedDeck = deck.copy(cards = savedCards)

        return dao.saveDeck(savedDeck).toModel(savedCards)
    }

    fun deleteDeck(id: Long) {
        getDeckById(id)
        dao.deleteDeck(id)
    }

}
