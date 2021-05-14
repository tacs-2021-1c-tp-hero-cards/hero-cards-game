package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.CharacterApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.CharactersSearchApi
import org.springframework.stereotype.Component

@Component
class SuperHeroClient : RestClient() {

    private val pathGetCharacter = "/{}"
    private val pathGetSearchName = "/search/{}"

    fun getCharacter(id: String): CharacterApi {
        val response = doGet<CharacterApi>(pathGetCharacter, id)
        if (response.error != null) {
            throw ElementNotFoundException("character", id)
        }
        return response
    }

    fun getCharacterByName(characterName: String): CharactersSearchApi {
        val response = doGet<CharactersSearchApi>(pathGetSearchName, characterName)
        if (response.error != null) {
            throw ElementNotFoundException("character", characterName)
        }
        return response
    }
}