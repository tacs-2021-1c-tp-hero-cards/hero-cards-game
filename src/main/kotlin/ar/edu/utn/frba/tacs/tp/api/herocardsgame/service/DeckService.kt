package ar.edu.utn.frba.tacs.tp.api.herocardsgame.service

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.InvalidPowerstatsException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.CardIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.DeckIntegration
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DeckService(
    private val cardIntegration: CardIntegration,
    private val deckIntegration: DeckIntegration
) {

    private val log: Logger = LoggerFactory.getLogger(DeckService::class.java)

    fun createDeck(nameDeck: String, cardIds: List<String>): Deck {
        log.info("Create deck with name: $nameDeck and cardIds: [${cardIds.joinToString(",")}]")

        val cards = searchCards(cardIds)
        val deck = Deck(name = nameDeck, cards = cards)
        return deckIntegration.saveDeck(deck)
    }

    fun deleteDeck(deckId: String) {
        log.info("Delete deck with id: $deckId")
        deckIntegration.deleteDeck(deckId.toLong())
    }

    fun searchDeck(deckId: String? = null, deckName: String? = null): List<Deck> {
        log.info("Search deck with id: $deckId or deckName: $deckName")
        return deckIntegration.getDeckByIdOrName(deckId?.toLong(), deckName)
    }

    fun updateDeck(deckId: String, name: String?, cardIds: List<String>): Deck {
        log.info("Update deck with id: $deckId with newName: $name or newCardIds: [${cardIds.joinToString(",")}]")

        val deck = deckIntegration.getDeckById(deckId.toLong())
        log.info("Deck to update its oldName: ${deck.name} and oldCardIds: [${deck.cards.map { it.id }.joinToString(",")}]")

        val cards = searchCards(cardIds)
        val newDeck = deck.updateDeck(name, cards)
        return deckIntegration.saveDeck(newDeck)
    }

    private fun searchCards(cardIds: List<String>): List<Card> {
        val cards = cardIds.map { cardIntegration.getCardById(it) }
        cards.filter { it.validateInvalidPowerstats() }.map { throw InvalidPowerstatsException(it.id) }
        return cards
    }
}