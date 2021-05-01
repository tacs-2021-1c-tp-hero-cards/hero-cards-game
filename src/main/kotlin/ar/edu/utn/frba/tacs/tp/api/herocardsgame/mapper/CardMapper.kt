package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api.CharacterApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.api.CharactersSearchApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import org.springframework.stereotype.Component

@Component
class CardMapper(val powerstatsMapper: PowerstatsMapper) {

    fun mapCard(characterApi: CharacterApi): Card =
        Card(
            id = characterApi.id.toLong(),
            name = characterApi.name,
            powerstats = powerstatsMapper.map(characterApi.powerstats, characterApi.appearance)
        )

    fun mapCharactersSearch(charactersSearchApi: CharactersSearchApi): List<Card> =
        charactersSearchApi.results.map { mapCard(it) }
}