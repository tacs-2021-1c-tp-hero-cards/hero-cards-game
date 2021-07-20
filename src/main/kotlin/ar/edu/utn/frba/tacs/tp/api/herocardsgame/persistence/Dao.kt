package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.CardEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.DuelHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player.PlayerEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player.PlayerHistoryEntity
import org.springframework.stereotype.Component

@Component
class Dao(
    private val cardMap: HashMap<Long, CardEntity> = hashMapOf(),
    private val playerMap: HashMap<Long, PlayerEntity> = hashMapOf(),
    private val matchMap: HashMap<Long, MatchEntity> = hashMapOf(),

    private val duelHistoryMap: HashMap<Long, DuelHistoryEntity> = hashMapOf(),
    private val playerHistoryMap: HashMap<Long, PlayerHistoryEntity> = hashMapOf()
) {

    fun <T> calculateId(entity: T): Long =
        when (entity) {
            is Player -> playerMap
            is Match -> matchMap
            is DuelHistory -> duelHistoryMap
            else -> cardMap
        }.size.toLong()

    fun <T> calculateVersion(entity: T): Long = playerHistoryMap.size.toLong()

    //Cards
    fun getAllCard(): List<CardEntity> = cardMap.values.toList()

    fun saveCard(card: Card): CardEntity {
        val entity = CardEntity(card)
        cardMap[entity.id] = entity
        return entity
    }

    fun removeLastUse() {
        cardMap.values.minByOrNull { it.lastUse }.let { cardMap.remove(it?.id) }
    }

    //Players
    fun getPlayerById(id: Long): PlayerEntity? = playerMap[id]

    fun savePLayer(player: Player): PlayerEntity {
        val playerId = player.id ?: calculateId(player)
        val entity = PlayerEntity(playerId, player)

        playerMap[playerId] = entity

        return entity
    }

    fun getPlayerHistoryByVersion(id: Long): PlayerHistoryEntity? = playerHistoryMap[id]

    fun savePlayerHistory(playerHistory: PlayerHistory): PlayerHistoryEntity {
        val playerHistoryVersion = calculateVersion(playerHistory)
        val entity = PlayerHistoryEntity(playerHistoryVersion, playerHistory)

        playerHistoryMap[playerHistoryVersion] = entity

        return entity
    }

    //Matches
    fun getMatchById(id: Long): MatchEntity? = matchMap[id]

    fun saveMatch(match: Match): MatchEntity {
        val matchId = match.id ?: calculateId(match)
        val entity = MatchEntity(matchId, match)

        matchMap[matchId] = entity

        return entity
    }

    fun getDuelHistoryById(id: Long): DuelHistoryEntity? = duelHistoryMap[id]

    fun saveDuelHistory(duelHistory: DuelHistory): DuelHistoryEntity {
        val duelHistoryId = calculateId(duelHistory)
        val entity = DuelHistoryEntity(duelHistoryId, duelHistory)

        duelHistoryMap[duelHistoryId] = entity

        return entity
    }

}