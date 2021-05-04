package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

data class Player(
    val userName: String,
    var availableCards: List<Card> = emptyList(),
    var prizeCards: List<Card> = emptyList()
){

    fun addAvailableCards(card: Card){
        availableCards = availableCards.plus(card)
    }

    fun addPrizeCards(card: Card){
        prizeCards = prizeCards.plus(card)
    }

}