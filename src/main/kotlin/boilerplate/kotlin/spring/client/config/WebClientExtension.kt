package boilerplate.kotlin.spring.client.config

import boilerplate.kotlin.spring.common.exception.APIException
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.awaitExchangeOrNull

// TODO Add Function [HEAD, PATCH]
suspend inline fun <reified T> WebClient.getWrapper(uri: String): T =
    get()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .awaitExchangeOrNull(::exchangeResponse)!!

suspend inline fun <reified T> WebClient.postWrapper(
    uri: String,
    body: Any = Any::class, // or Map::class.java
): T =
    post()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .awaitExchangeOrNull(::exchangeResponse)!!

suspend inline fun <reified T> WebClient.putWrapper(
    uri: String,
    body: Any = Any::class, // or Map::class.java
): T =
    put()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .awaitExchangeOrNull(::exchangeResponse)!!

suspend inline fun <reified T> WebClient.deleteWrapper(uri: String): T =
    delete()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .awaitExchangeOrNull(::exchangeResponse)!!

suspend inline fun <reified T : Any> exchangeResponse(res: ClientResponse): T? {
    if (res.statusCode().is2xxSuccessful.not()) { // != 200 -> Error
        throw res.awaitBodyOrNull<APIException>() ?: Throwable("Internal Server Error")
    }

    if (T::class == Unit::class) return null
    return res.awaitBodyOrNull<T>()
}