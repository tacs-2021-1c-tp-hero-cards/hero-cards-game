package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory

class PlayerHistoryEntity(version: Long? = null, playerHistory: PlayerHistory) {

    val id: Long = playerHistory.id
    val version: Long = version ?: playerHistory.version!!
    val availableCardIds: List<Long> = playerHistory.availableCards.map { it.id }
    val prizeCardIds: List<Long> = playerHistory.prizeCards.map { it.id }

    fun toModel(availableCardModels: List<Card>, prizeCardModels: List<Card>): PlayerHistory =
        PlayerHistory(id, version, availableCardModels.first(), availableCardModels, prizeCardModels)

}