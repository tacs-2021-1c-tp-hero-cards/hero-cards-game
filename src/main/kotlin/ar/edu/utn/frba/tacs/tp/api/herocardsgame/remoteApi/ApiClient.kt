package ar.edu.utn.frba.tacs.tp.api.herocardsgame.remoteApi

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.utils.FileConstructorUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.jvm.Throws

abstract class ApiClient {

    private var client = OkHttpClient()
    private var access_token = System.getenv("ACCESS_TOKEN")

    @Throws(IOException::class)
    fun <T>run(url: String, classReturn: Class<T>): T? {
        val request: Request = Request.Builder().url(url).build()
        this.client.newCall(request).execute().use {
            response -> val content = response.body!!.string()
                        return FileConstructorUtils.createFromContent(content, classReturn)
        }
    }

    fun url(): String {
        return BASE_URL.replace("access_token", access_token)
    }

    companion object {
        const val BASE_URL = "https://superheroapi.com/api/access_token"
    }
}