package ar.edu.utn.frba.tacs.tp.api.herocardsgame.integration

import okhttp3.OkHttpClient
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

internal class RestClientTest {

    private val clientMock = mock(OkHttpClient::class.java)
    private val instance = ClientMock(clientMock)

    private class ClientMock(client :OkHttpClient) :
        RestClient(
            client = client,
            host = "test.com",
            accessToken = "ACCESS_TOKEN"
        )

    @Test
    fun buildUrlWithUriParams() {
        val buildUrl = instance.buildUrl("/{}/powerstats", "id")
        assertEquals("https//test.com/api/ACCESS_TOKEN/id/powerstats", buildUrl)
    }

    @Test
    fun buildUrlWithOutUriParams() {
        val buildUrl = instance.buildUrl("/search/name")
        assertEquals("https//test.com/api/ACCESS_TOKEN/search/name", buildUrl)
    }
}