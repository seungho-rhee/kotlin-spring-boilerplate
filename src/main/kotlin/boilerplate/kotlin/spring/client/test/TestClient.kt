package boilerplate.kotlin.spring.client.test

import boilerplate.kotlin.spring.client.config.deleteWrapper
import boilerplate.kotlin.spring.client.config.getWrapper
import boilerplate.kotlin.spring.client.config.postWrapper
import boilerplate.kotlin.spring.client.config.putWrapper
import boilerplate.kotlin.spring.client.test.dto.RequestValue
import boilerplate.kotlin.spring.client.test.dto.ResponseValue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class TestClient(
    @Qualifier("testWebClient") private val webClient: WebClient,
) {

    // Any: Response Type으로 변경
    suspend fun getList(): ResponseValue {
        return webClient.getWrapper(uri = "/test/list")
    }

    suspend fun get(): ResponseValue {
        return webClient.getWrapper(uri = "/test/One")
    }

    suspend fun post(body: RequestValue): ResponseValue {
        return webClient
            .postWrapper(
                uri = "/test",
                body = body, // body: request body
            )
    }

    suspend fun put(body: RequestValue): ResponseValue {
        return webClient
            .putWrapper(
                uri = "/test",
                body = body, // body: request body
            )
    }

    suspend fun delete(): ResponseValue {
        return webClient.deleteWrapper(uri = "/test")
    }
}