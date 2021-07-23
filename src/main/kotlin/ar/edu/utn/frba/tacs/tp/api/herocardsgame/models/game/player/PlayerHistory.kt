package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

data class PlayerHistory(
    val cardPlayed: Card,
    val userName: String,
    val availableCards: List<Card> = emptyList(),
    val prizeCards: List<Card> = emptyList()
) {
    constructor(player: Player) : this(
        cardPlayed = player.availableCards.first(),
        availableCards = player.availableCards,
        prizeCards = player.prizeCards,
        userName = player.user.userName
    )
}