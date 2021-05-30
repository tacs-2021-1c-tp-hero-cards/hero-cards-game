package ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.*
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card
import com.google.gson.Gson
import java.io.File
import java.io.IOException

class BuilderContextUtils {
    companion object {
        private inline fun <reified T> createFromFile(file: String): T {
            try {
                val bufferedReader = File(file).bufferedReader()
                return Gson().fromJson(bufferedReader, T::class.java)
            } catch (e: IOException) {
                throw RuntimeException(e);
            }
        }
        
        fun buildBatman() = createFromFile<Card>(
            "src/test/resources/json/card/Batman.json"
        )

        fun buildBatmanII() = createFromFile<Card>(
            "src/test/resources/json/card/BatmanII.json"
        )

        fun buildBatmanIII() = createFromFile<Card>(
            "src/test/resources/json/card/BatmanIII.json"
        )

        fun buildFlash() = createFromFile<Card>(
            "src/test/resources/json/card/Flash.json"
        )

        fun buildAppearanceApi() = createFromFile<AppearanceApi>(
            "src/test/resources/json/api/appearance.json"
        )

        fun buildPowerstatsApi() = createFromFile<PowerstatsApi>(
            "src/test/resources/json/api/powerstats.json"
        )

        fun buildCharacterApi() = createFromFile<CharacterApi>(
            "src/test/resources/json/api/character.json"
        )

        fun buildCharactersSearchApi() = createFromFile<CharactersSearchApi>(
            "src/test/resources/json/api/charactersSearch.json"
        )

        fun buildImageApi() = createFromFile<ImageApi>(
            "src/test/resources/json/api/image.json"
        )

        fun buildErrorApi() = createFromFile<CharacterApi>(
            "src/test/resources/json/api/error.json"
        )
    }
}
