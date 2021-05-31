package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

data class Deck(
    val id: Long? = null,
    val name: String,
    val cards: List<Card> = emptyList(),
    val usable: Boolean = true
) {

    fun mixCards(): Deck = copy(cards = cards.shuffled())

    fun updateDeck(name: String?, cards: List<Card>): Deck =
        Deck(name = name ?: this.name, cards = cards.ifEmpty { this.cards })

}