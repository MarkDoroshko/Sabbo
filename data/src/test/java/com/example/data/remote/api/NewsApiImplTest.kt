package com.example.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class NewsApiImplTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun apiWithEngine(engine: MockEngine): NewsApiImpl {
        val client = HttpClient(engine) {
            defaultRequest { url("https://newsapi.org/v2/") }
            install(ContentNegotiation) { json(json) }
        }
        return NewsApiImpl(client)
    }

    private fun MockRequestHandleScope.jsonResponse(body: String) = respond(
        content = body,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    )

    @Test
    fun `getArticles sends request to everything endpoint with q and language query parameters`() =
        runTest {
            lateinit var requestUrl: Url
            val engine = MockEngine { request ->
                requestUrl = request.url
                jsonResponse("""{"articles":[]}""")
            }
            val api = apiWithEngine(engine)

            api.getArticles(topic = "Kotlin", language = "en")

            assertEquals("/v2/everything", requestUrl.encodedPath)
            assertEquals("Kotlin", requestUrl.parameters["q"])
            assertEquals("en", requestUrl.parameters["language"])
        }

    @Test
    fun `getArticles deserializes the response body into NewsResponseDto`() = runTest {
        val engine = MockEngine {
            jsonResponse(
                """
                {
                  "articles": [
                    {
                      "title": "Kotlin 2.4 released",
                      "description": "New release",
                      "publishedAt": "2024-01-01T12:00:00Z",
                      "url": "https://example.com/article",
                      "urlToImage": "https://example.com/image.png",
                      "source": { "name": "Example News" }
                    }
                  ]
                }
                """.trimIndent()
            )
        }
        val api = apiWithEngine(engine)

        val result = api.getArticles(topic = "Kotlin", language = "en")

        assertEquals(1, result.articles.size)
        assertEquals("Kotlin 2.4 released", result.articles[0].title)
        assertEquals("Example News", result.articles[0].source.name)
        assertEquals("https://example.com/image.png", result.articles[0].urlToImage)
    }
}
