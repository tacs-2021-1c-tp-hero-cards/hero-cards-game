package ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils;

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.models.game.Card

class BuilderContextUtils {
    companion object {
        fun buildBatman() = FileConstructorUtils.createFromFile(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Batman.json",
            Card::class.java
        )

        fun buildFlash() = FileConstructorUtils.createFromFile(
            "src/test/kotlin/ar/edu/utn/frba/tacs/tp/api/herocardsgame/json/card/Flash.json",
            Card::class.java
        )
    }
}
