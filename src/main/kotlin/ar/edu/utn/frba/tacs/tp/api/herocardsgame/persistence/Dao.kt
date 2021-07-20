package ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.CardEntity
import org.springframework.stereotype.Component

@Component
class Dao(
    private val cardMap: HashMap<Long, CardEntity> = hashMapOf()
) {

    fun <T> calculateId(entity: T): Long =
        cardMap.size.toLong()

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
}