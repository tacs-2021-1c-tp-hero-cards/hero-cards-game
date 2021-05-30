package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.Dao
import org.springframework.stereotype.Component

@Component
class CardIntegration(private val dao: Dao, private val superHeroIntegration: SuperHeroIntegration) {

    var limitCard: Int = 20

    fun saveCard(card: Card): Card {
        val allCard = dao.getAllCard()

        return when {
            allCard.any { card.id == it.id } -> dao.saveCard(card)
            allCard.size < limitCard -> dao.saveCard(card)
            else -> {
                dao.removeLastUse()
                dao.saveCard(card)
            }
        }.toModel()
    }

    fun getCardById(id: String): Card =
        dao.getAllCard().find { it.id == id.toLong() }?.toModel() ?: superHeroIntegration.getCard(id)

    fun searchCardByName(characterName: String): List<Card> = superHeroIntegration.searchCardByName(characterName)

    fun getSavedCards(): List<Card> =
        dao.getAllCard()
            .map { it.toModel() }
            .ifEmpty { superHeroIntegration.getRandomCards(limitCard) }

}
