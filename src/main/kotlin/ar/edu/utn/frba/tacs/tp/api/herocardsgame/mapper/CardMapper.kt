package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.CharacterApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.CharactersSearchApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import org.springframework.stereotype.Component

@Component
class CardMapper(val powerstatsMapper: PowerstatsMapper, val imageMapper: ImageMapper) {

    fun map(characterApi: CharacterApi): Card {
        val card = Card(
            id = characterApi.id!!.toLong(),
            name = characterApi.name!!,
            powerstats = powerstatsMapper.map(characterApi.powerstats!!, characterApi.appearance!!),
            imageUrl = imageMapper.map(characterApi.image!!)
        )

        card.isInvalidPowerstats()

        return card
    }

    fun mapCharactersSearch(charactersSearchApi: CharactersSearchApi): List<Card> =
        charactersSearchApi.results.map { map(it) }
}