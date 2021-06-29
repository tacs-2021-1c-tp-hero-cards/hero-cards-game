package ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

data class PlayerHistory(
    val id: Long,
    val version: Long? = null,
    val cardPlayed: Card,
    val availableCards: List<Card> = emptyList(),
    val prizeCards: List<Card> = emptyList()
) {
    constructor(player: Player) : this(
        id = player.id!!,
        cardPlayed = player.availableCards.first(),
        availableCards = player.availableCards,
        prizeCards = player.prizeCards
    )
}
