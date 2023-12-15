package network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual val httpClient: HttpClient = HttpClient(OkHttp) {
    installClientModules()
}
