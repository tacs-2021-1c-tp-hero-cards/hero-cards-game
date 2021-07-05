package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

data class Player(
    val id: Long? = null,
    val human: Human,
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

    fun winMatch(): Player = copy(human = human.winMatch().endMatch())

    fun tieMatch(): Player = copy(human = human.tieMatch().endMatch())

    fun loseMatch(): Player = copy(human = human.loseMatch().endMatch())

    fun startMatch(): Player = copy(human = human.startMatch())

    fun endMatch(): Player = copy(human = human.endMatch())

    fun calculateWinPlayer(opponent: Player): List<Player> =
        when (prizeCards.size.compareTo(opponent.prizeCards.size)) {
            1 -> listOf(this.winMatch(), opponent.loseMatch())
            -1 -> listOf(this.loseMatch(), opponent.winMatch())
            else -> listOf(this.tieMatch(), opponent.tieMatch())
        }
}