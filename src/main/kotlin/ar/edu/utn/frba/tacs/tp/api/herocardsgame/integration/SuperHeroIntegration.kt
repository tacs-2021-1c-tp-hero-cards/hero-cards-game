package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.SuperHeroClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper.CardMapper
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import org.springframework.stereotype.Component

@Component
class SuperHeroIntegration(
    private val client: SuperHeroClient,
    private val mapper: CardMapper
) {

    var totalCard: Int = 731

    fun getCard(id: String): Card {
        val character = client.getCharacter(id)
        return mapper.map(character)
    }

    fun searchCardByName(characterName: String): List<Card> {
        val charactersByName = client.getCharacterByName(characterName)
        return mapper.mapCharactersSearch(charactersByName)
    }

    fun getRandomCards(numberCards: Int): List<Card> =
        (1..totalCard).shuffled().take(numberCards).map { getCard(it.toString()) }
}