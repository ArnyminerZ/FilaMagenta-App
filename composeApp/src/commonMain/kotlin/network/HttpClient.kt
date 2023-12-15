package network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect val httpClient: HttpClient

val httpClientJson = Json {
    isLenient = true
}

fun <T : HttpClientEngineConfig> HttpClientConfig<T>.installClientModules() {
    install(ContentNegotiation) {
        json(httpClientJson)
    }
}
