package network.backend

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val httpClientJson = Json {
    isLenient = true
}

fun <T : HttpClientEngineConfig> HttpClientConfig<T>.installClientModules() {
    install(ContentNegotiation) {
        json(httpClientJson)
    }
}
