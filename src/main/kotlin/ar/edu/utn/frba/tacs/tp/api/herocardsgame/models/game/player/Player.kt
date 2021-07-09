package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

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

    fun winMatch(): Player = copy(user = user.winMatch().endMatch())

    fun tieMatch(): Player = copy(user = user.tieMatch().endMatch())

    fun loseMatch(): Player = copy(user = user.loseMatch().endMatch())

    fun startMatch(): Player = copy(user = user.startMatch())

    fun endMatch(): Player = copy(user = user.endMatch())

    fun calculateWinPlayer(opponent: Player): List<Player> =
        when (prizeCards.size.compareTo(opponent.prizeCards.size)) {
            1 -> listOf(this.winMatch(), opponent.loseMatch())
            -1 -> listOf(this.loseMatch(), opponent.winMatch())
            else -> listOf(this.tieMatch(), opponent.tieMatch())
        }
}