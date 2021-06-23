package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.deck.DeckHistory
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.deck.DeckHistoryEntity
import org.springframework.stereotype.Component

@Component
class Dao(
    private val userMap: HashMap<Long, UserEntity> = hashMapOf(),
    private val cardMap: HashMap<Long, CardEntity> = hashMapOf(),
    private val deckMap: HashMap<Long, DeckEntity> = hashMapOf(),
    private val playerMap: HashMap<Long, PlayerEntity> = hashMapOf(),
    private val matchMap: HashMap<Long, MatchEntity> = hashMapOf(),

    private val deckHistoryMap: HashMap<Long, DeckHistoryEntity> = hashMapOf(),
    private val playerHistoryMap: HashMap<Long, DeckHistoryEntity> = hashMapOf(),
    private val matchHistoryMap: HashMap<Long, DeckHistoryEntity> = hashMapOf()
) {

    fun <T> calculateId(entity: T): Long =
        when (entity) {
            is User -> userMap
            is Deck -> deckMap
            is Player -> playerMap
            is Match -> matchMap
            else -> cardMap
        }.size.toLong()

    fun <T> calculateVersion(entity: T): Long =
        when (entity) {
            is Deck -> deckHistoryMap
            is Player -> playerHistoryMap
            else -> matchHistoryMap
        }.size.toLong()

    //Users
    fun getAllUser(): List<UserEntity> = userMap.values.toList()

    fun getUserById(id: Long): UserEntity? = userMap[id]

    fun saveUser(user: User): UserEntity {
        val userId = user.id ?: calculateId(user)
        val entity = UserEntity(userId, user.copy(id = userId))

        userMap[userId] = entity

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
        val entity = DeckEntity(deckId, deckVersion, deck.copy(id = deckId, version = deckVersion))

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

    //Matches
    fun getMatchById(id: Long): MatchEntity? = matchMap[id]

    fun saveMatch(match: Match): MatchEntity {
        val matchId = match.id ?: calculateId(match)
        val entity = MatchEntity(matchId, match)

        matchMap[matchId] = entity

        return entity
    }

}