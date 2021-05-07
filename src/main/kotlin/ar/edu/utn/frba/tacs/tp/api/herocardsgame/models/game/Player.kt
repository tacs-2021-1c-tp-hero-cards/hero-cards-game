package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

data class Player(
    val id: Long,
    val userName: String,
    val availableCards: List<Card> = emptyList(),
    val prizeCards: List<Card> = emptyList()
) {

    fun winDuel(priceCard: Card) : Player =
        this.copy(
            prizeCards = prizeCards + priceCard + availableCards.first(),
            availableCards = availableCards.drop(1)
        )

    fun loseDuel(): Player =
        this.copy(availableCards = availableCards.drop(1))

    fun tieDuel(): Player =
        this.copy(
            prizeCards = prizeCards + availableCards.first(),
            availableCards = availableCards.drop(1)
        )

}