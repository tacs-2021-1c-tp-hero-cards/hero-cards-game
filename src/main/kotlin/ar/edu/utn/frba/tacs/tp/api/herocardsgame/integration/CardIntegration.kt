package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.entity.CardEntity
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.persistence.repository.CardRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class CardIntegration(
    private val superHeroIntegration: SuperHeroIntegration,
    private val repository: CardRepository
) {

    var limitCard: Int = 20

    fun getCardById(id: String): Card =
        (repository.findByIdOrNull(id) ?: repository.save(CardEntity(superHeroIntegration.getCard(id)))).toModel()

    fun searchCardByName(characterName: String): List<Card> {
        val cards = superHeroIntegration.searchCardByName(characterName)
        return saveAll(cards)
    }

    fun getSavedCards(): List<Card> =
        repository.findAll()
            .map { it.toModel() }
            .ifEmpty {
                val cards = superHeroIntegration.getRandomCards(limitCard)
                return saveAll(cards)
            }

    private fun saveAll(cards: List<Card>) =
        cards.map { repository.save(CardEntity(it)).toModel() }

}
