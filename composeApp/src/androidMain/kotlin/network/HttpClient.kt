package network

import KoverIgnore
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import network.backend.installClientModules

@KoverIgnore
actual val httpClient: HttpClient = HttpClient(Android) {
    installClientModules()
}
