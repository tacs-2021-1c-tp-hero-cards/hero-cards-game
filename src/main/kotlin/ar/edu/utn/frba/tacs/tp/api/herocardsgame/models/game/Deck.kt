package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

class Deck(
    var id: Long? = null,
    var name: String,
    var cards: List<Card>
) {

    fun updateId(newId: Long){
        id = newId
    }

    fun rename(newName: String) {
        name = newName
    }

    fun addCard(card: Card) {
        cards = cards.plus(card)
    }

    fun removeCard(cardId: Long) {
        cards = cards.filterNot { it.id == cardId }
    }

    fun removeAllCard(){
        cards = emptyList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Deck

        if (name != other.name) return false
        if (cards != other.cards) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + cards.hashCode()
        return result
    }

}