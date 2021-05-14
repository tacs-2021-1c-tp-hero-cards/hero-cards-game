package ar.edu.utn.frba.tacs.tp.api.herocardsgame.mapper

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.ImageApi
import org.springframework.stereotype.Component

@Component
class ImageMapper {
    fun map(imageApi: ImageApi): String = imageApi.url
}
