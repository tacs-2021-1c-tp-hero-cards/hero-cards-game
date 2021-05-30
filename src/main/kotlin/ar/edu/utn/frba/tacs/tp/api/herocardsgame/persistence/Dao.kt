package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.accounts.User
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Deck
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Match
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Player
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.*
import org.springframework.stereotype.Component

@Component
class Dao(
    private val userMap: HashMap<Long, UserEntity> = hashMapOf(),
    private val cardMap: HashMap<Long, CardEntity> = hashMapOf(),
    private val deckMap: HashMap<Long, DeckEntity> = hashMapOf(),
    private val playerMap: HashMap<Long, PlayerEntity> = hashMapOf(),
    private val matchMap: HashMap<Long, MatchEntity> = hashMapOf(),
) {

    fun <T> calculateId(entity: T): Long =
        when (entity) {
            is User -> userMap
            is Deck -> deckMap
            is Player -> playerMap
            is Match -> matchMap
            else -> cardMap
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

    fun saveDeck(deck: Deck): DeckEntity {
        val deckId = deck.id ?: calculateId(deck)
        val entity = DeckEntity(deckId, deck.copy(id = deckId))

        deckMap[deckId] = entity

        return entity
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