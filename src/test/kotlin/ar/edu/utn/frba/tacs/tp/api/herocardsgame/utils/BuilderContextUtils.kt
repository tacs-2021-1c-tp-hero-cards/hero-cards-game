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
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Batman.json"
        )

        fun buildBatmanII() = createFromFile<Card>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/BatmanII.json"
        )

        fun buildBatmanIII() = createFromFile<Card>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/BatmanIII.json"
        )

        fun buildFlash() = createFromFile<Card>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Flash.json"
        )

        fun buildAppearanceApi() = createFromFile<AppearanceApi>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/appearance.json"
        )

        fun buildPowerstatsApi() = createFromFile<PowerstatsApi>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/powerstats.json"
        )

        fun buildCharacterApi() = createFromFile<CharacterApi>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/character.json"
        )

        fun buildCharactersSearchApi() = createFromFile<CharactersSearchApi>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/charactersSearch.json"
        )

        fun buildImageApi() = createFromFile<ImageApi>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/image.json"
        )

        fun buildErrorApi() = createFromFile<CharacterApi>(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/api/error.json"
        )
    }
}
