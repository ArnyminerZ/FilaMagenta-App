package network.backend.model

import error.ServerResponseException
import filamagenta.BuildKonfig
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.appendEncodedPathSegments
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import network.backend.httpClientJson
import network.httpClient
import response.FailureResponse
import server.EndpointDef

abstract class BackendConnector {
    val server: String = BuildKonfig.SERVER

    /**
     * Runs an HTTP request with the desired method to the given endpoint.
     *
     * The target url is generated using [EndpointDef.url], replacing all parameters with the ones in [parameters].
     *
     * @param method Should be a call to [httpClient], selecting the desired method to use.
     * @param endpoint The endpoint to fetch.
     * @param parameters If any, pairs of parameters to replace in the url.
     * @param body Can be null, if not null, the body to send in the request.
     * @param token If any, the authentication token to use for the request.
     *
     * @throws ServerResponseException
     */
    suspend inline fun <reified DataType> httpRequest(
        method: (url: String, config: HttpRequestBuilder.() -> Unit) -> HttpResponse,
        endpoint: EndpointDef,
        vararg parameters: Pair<String, Any>,
        body: Any? = null,
        token: String? = null
    ): DataType {
        val url = URLBuilder(server)
            .appendEncodedPathSegments(endpoint.url(*parameters))
            .buildString()

        val response = method(url) {
            token?.let { bearerAuth(it) }
            body?.let {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        }
        val responseBodyStr = response.bodyAsText()
        val bodyResponse = httpClientJson.decodeFromString<JsonElement>(responseBodyStr).jsonObject

        val success = bodyResponse.getValue("success").jsonPrimitive.boolean
        if (success) {
            return bodyResponse["data"]?.let { httpClientJson.decodeFromJsonElement<DataType>(it) } as DataType
        } else {
            val failure = httpClientJson.decodeFromString<FailureResponse>(responseBodyStr)
            val exception: ServerResponseException = failure.error.toException()
            throw exception
        }
    }

    /**
     * Runs an HTTP POST request to the given endpoint.
     *
     * The target url is generated using [EndpointDef.url], replacing all parameters with the ones in [parameters].
     *
     * @param endpoint The endpoint to fetch.
     * @param parameters If any, pairs of parameters to replace in the url.
     * @param body Can be null, if not null, the body to send in the request.
     *
     * @throws ServerResponseException
     */
    suspend inline fun <reified DataType> post(
        endpoint: EndpointDef,
        vararg parameters: Pair<String, Any>,
        body: Any? = null,
        token: String? = null
    ): DataType {
        return httpRequest({ u, c -> httpClient.post(u, c) }, endpoint, *parameters, body = body, token = token)
    }

    /**
     * Runs an HTTP GET request to the given endpoint.
     *
     * The target url is generated using [EndpointDef.url], replacing all parameters with the ones in [parameters].
     *
     * @param endpoint The endpoint to fetch.
     * @param parameters If any, pairs of parameters to replace in the url.
     *
     * @throws ServerResponseException
     */
    suspend inline fun <reified DataType> get(
        endpoint: EndpointDef,
        vararg parameters: Pair<String, Any>,
        token: String? = null
    ): DataType {
        return httpRequest({ u, c -> httpClient.get(u, c) }, endpoint, *parameters, body = null, token = token)
    }
}
