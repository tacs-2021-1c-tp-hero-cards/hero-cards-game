package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

abstract class RestClient(
    private var client: OkHttpClient = OkHttpClient(),
    private var protocol: String = "https://",
    private var host: String = "superheroapi.com",
    private var baseUrl: String = "/api/",
    //private var accessToken: String = System.getenv("ACCESS_TOKEN")
    private var accessToken: String = "10225693555586194"
) {

    val log: Logger = LoggerFactory.getLogger(RestClient::class.java)

    inline fun <reified T> doGet(serviceUrl: String, vararg uriParams: String): T {
        val fullUrl = buildUrl(serviceUrl, *uriParams)
        val response = run(fullUrl)
        val buildResponse: T = buildResponse(response)
        log.info("Get $fullUrl code: ${response.code} responseBody: [$buildResponse]")

        return buildResponse
    }

    fun buildUrl(serviceUrl: String, vararg uriParams: String): String {
        val serviceUrlWithParams = uriParams.fold(serviceUrl) { url, uriParam ->
            url.replaceFirst("{}", uriParam)
        }
        return protocol + host + baseUrl + accessToken + serviceUrlWithParams
    }

    fun run(url: String): Response {
        val request: Request = Request.Builder().url(url).build()
        return client.newCall(request).execute()
    }

    inline fun <reified T> buildResponse(response: Response): T {
        try {
            return Gson().fromJson(response.body!!.string(), T::class.java)
        } catch (e: IOException) {
            throw RuntimeException(e);
        }
    }
}