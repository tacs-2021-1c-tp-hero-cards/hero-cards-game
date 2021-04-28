package ar.edu.utn.frba.tacs.tp.api.herocardsgame.remoteApi

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Character
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Image

class SuperHeroAPI: ApiClient() {

    fun getCharacter(id: String): Character? {
        return run("${url()}/$id", Character::class.java)
    }

    fun getImage(id: String): Image? {
        return run("${url()}/$id/image", Image::class.java)
    }

}