package ar.edu.utn.frba.tacs.tp.api.herocardsgame.remoteApi

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.jvm.Throws

abstract class ApiClient {

    private var client = OkHttpClient()
    private var access_token = System.getenv("ACCESS_TOKEN")

    @Throws(IOException::class)
    fun run(url: String): String? {
        val request: Request = Request.Builder().url(url).build()
        this.client.newCall(request).execute().use { response -> return response.body!!.string()}
    }

    fun url(): String {
        return BASE_URL.replace("access_token", access_token)
    }

    companion object {
        const val BASE_URL = "https://superheroapi.com/api/access_token"
    }
}