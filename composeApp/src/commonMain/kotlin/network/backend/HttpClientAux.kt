package network.backend

import KoverIgnore
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@KoverIgnore
val httpClientJson = Json {
    isLenient = true
}

@KoverIgnore
fun <T : HttpClientEngineConfig> HttpClientConfig<T>.installClientModules() {
    install(ContentNegotiation) {
        json(httpClientJson)
    }
    install(Auth)
}
