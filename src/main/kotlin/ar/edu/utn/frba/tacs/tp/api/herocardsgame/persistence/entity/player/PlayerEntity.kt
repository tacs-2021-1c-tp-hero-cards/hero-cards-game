package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player

class PlayerEntity(id: Long? = null, player: Player) {

    val id: Long = id ?: player.id!!
    val userId: Long = player.user.id!!
    val userType: String = player.user.userType.name
    val availableCardIds: List<Long> = player.availableCards.map { it.id }
    val prizeCardIds: List<Long> = player.prizeCards.map { it.id }

    fun toModel(userModel: User, availableCardModels: List<Card>, prizeCardModels: List<Card>): Player =
        Player(id, userModel, availableCardModels, prizeCardModels)

}