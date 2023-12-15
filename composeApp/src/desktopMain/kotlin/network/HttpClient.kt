package network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import network.backend.installClientModules

actual val httpClient: HttpClient = HttpClient(OkHttp) {
    installClientModules()
}
