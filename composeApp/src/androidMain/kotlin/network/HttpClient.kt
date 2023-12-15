package network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual val httpClient: HttpClient = HttpClient(Android)
