package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.exception.ElementNotFoundException
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.CharacterApi
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.CharactersSearchApi
import org.springframework.stereotype.Component

@Component
class SuperHeroClient : RestClient() {

    private val pathGetCharacter = "/{}"
    private val pathGetSearchName = "/search/{}"
    private val pathGetPowerstats = "/{}/powerstats"
    private val pathGetBiography = "/{}/biography"
    private val pathGetAppearance = "/{}/appearance"
    private val pathGetWork = "/{}/work"
    private val pathGetConnections = "/{}/connections"
    private val pathGetImage = "/{}/image"

    fun getCharacter(id: String): CharacterApi {
        val response = doGet(pathGetCharacter, CharacterApi::class.java, id)
        if (response.error != null) {
            throw ElementNotFoundException("character", id)
        }
        return response
    }

    fun getCharacterByName(characterName : String): CharactersSearchApi {
        val response = doGet(pathGetSearchName, CharactersSearchApi::class.java, characterName)
        if (response.error != null) {
            throw ElementNotFoundException("character", characterName)
        }
        return response
    }
}