package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.Human
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.user.IA
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.DuelHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.match.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.player.PlayerHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.DuelHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.match.MatchEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player.PlayerEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.player.PlayerHistoryEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.HumanEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.user.IAEntity
import org.springframework.stereotype.Component

@Component
class Dao(
    private val humanMap: HashMap<Long, HumanEntity> = hashMapOf(),
    private val cardMap: HashMap<Long, CardEntity> = hashMapOf(),
    private val deckMap: HashMap<Long, DeckEntity> = hashMapOf(),
    private val playerMap: HashMap<Long, PlayerEntity> = hashMapOf(),
    private val matchMap: HashMap<Long, MatchEntity> = hashMapOf(),
    private val iaMap: HashMap<Long, IAEntity> = hashMapOf(),

    private val deckHistoryMap: HashMap<Long, DeckHistoryEntity> = hashMapOf(),
    private val duelHistoryMap: HashMap<Long, DuelHistoryEntity> = hashMapOf(),
    private val playerHistoryMap: HashMap<Long, PlayerHistoryEntity> = hashMapOf()
) {

    fun <T> calculateId(entity: T): Long =
        when (entity) {
            is Human -> humanMap
            is Deck -> deckMap
            is Player -> playerMap
            is Match -> matchMap
            is DuelHistory -> duelHistoryMap
            is IA -> iaMap
            else -> cardMap
        }.size.toLong()

    fun <T> calculateVersion(entity: T): Long =
        when (entity) {
            is PlayerHistory -> playerHistoryMap
            else -> deckHistoryMap
        }.size.toLong()

    //Humans
    fun getAllHuman(): List<HumanEntity> = humanMap.values.toList()

    fun getHumanById(id: Long): HumanEntity? = humanMap[id]

    fun saveHuman(human: Human): HumanEntity {
        val userId = human.id ?: calculateId(human)
        val entity = HumanEntity(userId, human.copy(id = userId))

        humanMap[userId] = entity

        return entity
    }

    //IAs
    fun getAllIA(): List<IAEntity> = iaMap.values.toList()

    fun getIAById(id: Long): IAEntity? = iaMap[id]

    fun saveIA(ia: IA): IAEntity {
        val userId = ia.id ?: calculateId(ia)
        val entity = IAEntity(userId, ia.copy(id = userId))

        iaMap[userId] = entity

        return entity
    }

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

    //Decks
    fun getAllDeck(): List<DeckEntity> = deckMap.values.toList()

    fun getAllDeckHistory(): List<DeckHistoryEntity> = deckHistoryMap.values.toList()

    fun getDeckById(id: Long): DeckEntity? = deckMap[id]

    fun getDeckHistoryById(id: Long): List<DeckHistoryEntity> = deckHistoryMap.values.filter { it.id == id }

    fun getDeckHistoryByVersion(version: Long): DeckHistoryEntity? =
        deckHistoryMap.values.firstOrNull { it.version == version }

    fun saveDeck(deck: Deck): DeckEntity {
        val deckId = deck.id ?: calculateId(deck)
        val deckVersion = deck.version ?: calculateVersion(deck)
        val entity = DeckEntity(deckId, deckVersion, deck)

        deckMap[deckId] = entity

        return entity
    }

    fun saveDeckHistory(deckHistory: DeckHistory): DeckHistoryEntity {
        val deckHistoryEntity = DeckHistoryEntity(deckHistory)

        if (!deckHistoryMap.containsKey(deckHistory.version)) {
            deckHistoryMap[deckHistory.version] = deckHistoryEntity
        }

        return deckHistoryEntity
    }

    fun deleteDeck(id: Long) = deckMap.remove(id)

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