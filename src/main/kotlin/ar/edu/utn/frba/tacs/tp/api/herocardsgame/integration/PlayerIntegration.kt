package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import org.springframework.stereotype.Component

@Component
class PlayerIntegration(
    private val dao: Dao,
    private val userIntegration: UserIntegration,
    private val cardIntegration: CardIntegration
) {

    fun getPlayerById(id: Long): Player {
        val playerEntity = dao.getPlayerById(id) ?: throw ElementNotFoundException("player", "id", id.toString())
        val user = userIntegration.getHumanUserById(playerEntity.userId)
        val prizeCards = playerEntity.prizeCardIds.map { cardIntegration.getCardById(it.toString()) }
        val availableCards = playerEntity.availableCardIds.map { cardIntegration.getCardById(it.toString()) }
        return playerEntity.toModel(user, availableCards, prizeCards)
    }

    fun getPlayerHistoryByVersion(version: Long): PlayerHistory {
        val entity = dao.getPlayerHistoryByVersion(version) ?: throw ElementNotFoundException("playerHistory", "version", version.toString())
        val availableCards = entity.availableCardIds.map { cardIntegration.getCardById(it.toString()) }
        val prizeCards = entity.prizeCardIds.map { cardIntegration.getCardById(it.toString()) }

        return entity.toModel(availableCards, prizeCards)
    }

    fun savePlayer(player: Player): Player {
        val savedUser = userIntegration.saveUser(player.human)
        val savedPrizeCards = player.prizeCards.map { cardIntegration.saveCard(it) }
        val savedAvailableCards = player.availableCards.map { cardIntegration.saveCard(it) }
        return dao.savePLayer(player).toModel(savedUser, savedAvailableCards, savedPrizeCards)
    }

    fun savePlayerHistory(playerHistory: PlayerHistory): PlayerHistory =
        dao.savePlayerHistory(playerHistory).toModel(playerHistory.availableCards, playerHistory.prizeCards)

}