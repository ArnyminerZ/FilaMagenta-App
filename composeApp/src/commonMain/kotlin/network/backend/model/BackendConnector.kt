package network.backend.model

import error.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
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
        body: Any? = null
    ): DataType {
        val url = endpoint.url(*parameters)
        val response = httpClient.post(url) {
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
}
