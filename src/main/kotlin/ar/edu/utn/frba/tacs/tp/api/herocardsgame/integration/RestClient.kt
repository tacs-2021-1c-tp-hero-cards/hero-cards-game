package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

abstract class RestClient(
    private var client: OkHttpClient = OkHttpClient(),
    private var protocol: String = "https://",
    private var host: String = "superheroapi.com",
    private var baseUrl: String = "/api/",
    //private var accessToken: String = System.getenv("ACCESS_TOKEN")
    private var accessToken: String = "10225693555586194"
) {

    fun <T> doGet(serviceUrl: String, classReturn: Class<T>, vararg uriParams: String): T {
        val fullUrl = buildUrl(serviceUrl, *uriParams)
        run(fullUrl).use {
            return buildResponse(it, classReturn)
        }
    }

    fun buildUrl(serviceUrl: String, vararg uriParams: String): String {
        val serviceUrlWithParams = uriParams.fold(serviceUrl) { url, uriParam ->
            url.replaceFirst("{}", uriParam)
        }
        return protocol + host + baseUrl + accessToken + serviceUrlWithParams
    }

    private fun run(url: String): Response {
        val request: Request = Request.Builder().url(url).build()
        return client.newCall(request).execute()
    }

    private fun <T> buildResponse(response: Response, classReturn: Class<T>): T {
        try {
            return Gson().fromJson(response.body!!.string(), classReturn)
        } catch (e: IOException) {
            throw RuntimeException(e);
        }
    }
}