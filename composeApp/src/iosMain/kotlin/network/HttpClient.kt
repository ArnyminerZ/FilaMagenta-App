package network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import network.backend.installClientModules

actual val httpClient: HttpClient = HttpClient(Darwin) {
    installClientModules()
}
