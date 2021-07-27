package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.DeckRepository
import org.springframework.stereotype.Component

@Component
class DeckIntegration(private val cardIntegration: CardIntegration, private val repository: DeckRepository) {

    fun getDeckById(id: Long): Deck {
        val deckEntity = repository.getById(id) ?: throw ElementNotFoundException("deck", "id", id.toString())
        return deckEntity.toModel(getAllCardByDeckEntity(deckEntity))
    }

    fun getDeckByIdOrName(id: String? = null, name: String? = null): List<Deck> {
        val deckEntities = repository.findDeckByIdAndName(id, name)
        return deckEntities.map { it.toModel(getAllCardByDeckEntity(it)) }
    }

    fun saveDeck(deck: Deck): Deck {
        val allCards = deck.deckHistoryList
            .flatMap { it.cards }
            .plus(deck.cards)
            .distinct()

        return repository.save(DeckEntity(deck)).toModel(allCards)
    }

    fun deleteDeck(id: Long) {
        repository.getById(id) ?: throw ElementNotFoundException("deck", "id", id.toString())
        repository.deleteById(id)
    }

    private fun getAllCardByDeckEntity(deckEntity: DeckEntity): List<Card> {
        val deckCards = deckEntity.cardIds.split(",")
        val deckHistoryCards = deckEntity.deckHistory.flatMap { it.cardIds.split(",") }

        val allCards = deckCards.plus(deckHistoryCards).distinct()

        return allCards.map { cardIntegration.getCardById(it) }
    }

}
