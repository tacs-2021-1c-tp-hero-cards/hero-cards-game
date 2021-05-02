package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

class Deck(
    val id: Long,
    var name: String,
    var cards: List<Card>
) {

    fun rename(newName: String) {
        name = newName
    }

    fun addCard(card: Card) {
        cards = cards.plus(card)
    }

    fun removeCard(cardId: Long) {
        cards = cards.filterNot { it.id == cardId }
    }

}