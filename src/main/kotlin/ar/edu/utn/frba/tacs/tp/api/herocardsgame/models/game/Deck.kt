package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

data class Deck(
    val id: Long? = null,
    val name: String,
    val cards: List<Card> = emptyList()
) {

    fun addCard(card: Card) = copy(cards = cards + card)

    fun removeCard(cardId: Long) = copy(cards = cards.filterNot { it.id == cardId })

    fun mixCards(): Deck = copy(cards = cards.shuffled())

    fun rename(newName: String?): Deck = newName?.let { copy(name = it) } ?: this

    fun replaceCards(cards: List<Card>): Deck = if (cards.isNotEmpty()) copy(cards = cards) else this

}