package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User

data class Player(
    val id: Long? = null,
    val user: User,
    val availableCards: List<Card> = emptyList(),
    val prizeCards: List<Card> = emptyList()
) {

    fun winDuel(priceCard: Card): Player =
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

    fun winMatch(): Player = copy(user = user.winMatch())

    fun tieMatch(): Player = copy(user = user.tieMatch())

    fun loseMatch(): Player = copy(user = user.loseMatch())

    fun startMatch(): Player = copy(user = user.startMatch())
}