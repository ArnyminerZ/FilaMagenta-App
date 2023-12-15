package network

import KoverIgnore
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import network.backend.installClientModules

@KoverIgnore
actual val httpClient: HttpClient = HttpClient(Darwin) {
    installClientModules()
}
