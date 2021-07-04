package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.character.*
import org.springframework.stereotype.Component

@Component
class CharacterMapper {

    fun map(characterApi: CharacterApi) =
        Character(
            characterApi.id!!,
            characterApi.name!!,
            mapPowerstats(characterApi.powerstats!!),
            mapBiography(characterApi.biography!!),
            mapAppearance(characterApi.appearance!!),
            mapWork(characterApi.work!!),
            mapConnections(characterApi.connections!!)
        )

    private fun mapPowerstats(powerstatsApi: PowerstatsApi) =
        Powerstats(
            powerstatsApi.intelligence,
            powerstatsApi.strength,
            powerstatsApi.speed,
            powerstatsApi.durability,
            powerstatsApi.power,
            powerstatsApi.combat
        )

    private fun mapBiography(biographyApi: BiographyApi) =
        Biography(
            biographyApi.fullName,
            biographyApi.alterEgos,
            biographyApi.aliases,
            biographyApi.placeOfBirth,
            biographyApi.firstAppearance,
            biographyApi.publisher,
            biographyApi.alignment
        )

    private fun mapAppearance(appearanceApi: AppearanceApi) =
        Appearance(
            appearanceApi.gender,
            appearanceApi.race,
            appearanceApi.height,
            appearanceApi.weight,
            appearanceApi.eyeColor,
            appearanceApi.hairColor
        )

    private fun mapWork(workApi: WorkApi) = Work(workApi.occupation, workApi.base)

    private fun mapConnections(connectionsApi: ConnectionsApi) =
        Connections(
            connectionsApi.groupAffiliation,
            connectionsApi.relatives,
        )
}