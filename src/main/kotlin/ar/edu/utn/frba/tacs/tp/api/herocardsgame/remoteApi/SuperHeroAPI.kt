package ar.edu.utn.frba.tacs.tp.api.herocardsgame.remoteApi

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Character
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Image

class SuperHeroAPI: ApiClient() {

    fun getCharacter(id: String): Character? {
        val content = run("${url()}/$id")
        return content?.let {FileConstructorUtils.createFromContent(it, Character::class.java)}
    }

    fun getImage(id: String): Image? {
        val content = run("${url()}/$id/image")
        return content?.let {FileConstructorUtils.createFromContent(it, Image::class.java)}
    }

}