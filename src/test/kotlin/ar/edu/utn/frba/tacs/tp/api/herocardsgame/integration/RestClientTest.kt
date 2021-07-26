package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.RestClient
import ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration.client.api.ImageApi
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.File

internal class RestClientTest {

    private val clientMock = mock(OkHttpClient::class.java)
    private val instance = ClientMock(clientMock)

    private val responseMock = mock(Response::class.java)

    private class ClientMock(client: OkHttpClient) :
        RestClient(
            client = client,
            host = "test.com"
        ){
        override fun getAccessToken(): String = "ACCESS_TOKEN"
    }

    @Test
    fun buildUrlWithUriParams() {
        val buildUrl = instance.buildUrl("/{}/powerstats", "id")
        assertEquals("https://test.com/api/ACCESS_TOKEN/id/powerstats", buildUrl)
    }

    @Test
    fun buildUrlWithOutUriParams() {
        val buildUrl = instance.buildUrl("/search/name")
        assertEquals("https://test.com/api/ACCESS_TOKEN/search/name", buildUrl)
    }

    @Test
    fun buildResponse() {
        val json =
            File("src/test/resources/json/api/image.json")
                .bufferedReader()
                .use { it.readText() }

        `when`(responseMock.body).thenReturn(json.toResponseBody())

        val response = instance.buildResponse<ImageApi>(responseMock)

        assertEquals("success", response.response)
        assertEquals("https://www.superherodb.com/pictures2/portraits/10/100/639.jpg", response.url)
        assertNull(response.error)
    }

    @Test
    fun buildResponse_error() {
        val json =
            File("src/test/resources/json/api/error.json")
                .bufferedReader()
                .use { it.readText() }

        `when`(responseMock.body).thenReturn(json.toResponseBody())

        val response = instance.buildResponse<ImageApi>(responseMock)

        assertEquals("error", response.response)
        assertEquals("invalid id", response.error)
        assertNull(response.url)
    }
}