package boilerplate.kotlin.spring.client.config

import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.function.Consumer

@Configuration
class WebClientConfig(
    @Value("\${app.client.testClient.url}") private val testClientURL: String,
    @Value("\${app.client.testClient.apiKey}") private val testClientApiKey: String,
) {
    @Bean("testWebClient")
    fun testWebClient(): WebClient {
        return newClientBuilder()
            .clientConnector(
                client(
                    baseUrl = testClientURL,
                    headers = mapOf(
                        "API-KEY" to testClientApiKey,
                    ),
                    poolName = "test-client",
                )
            )
            .build()
    }

    private fun client(
        baseUrl: String,
        headers: Map<String, String> = emptyMap(),
        poolName: String,
        maxCon: Int = 30,
        connectionTimeout: Int = 10_000,
        keepAlive: Boolean = true,
    ): ReactorClientHttpConnector {
        return ReactorClientHttpConnector(
            HttpClient
                .create(connectionProvider(poolName, maxCon))
                .baseUrl(baseUrl)
                .headers { headers.forEach { (k, v) -> it.add(k, v) } }
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .option(ChannelOption.SO_KEEPALIVE, keepAlive)
                .responseTimeout(Duration.ofMillis(30_000))
        )
    }

    private fun connectionProvider(poolName: String, maxCon: Int = 30): ConnectionProvider {
        return ConnectionProvider.builder(poolName)
            .maxConnections(maxCon)
            .maxIdleTime(Duration.ofMillis(58_000))
            .maxLifeTime(Duration.ofMillis(58_000))
            .pendingAcquireTimeout(Duration.ofMillis(10_000))
            .pendingAcquireMaxCount(-1)
            .evictInBackground(Duration.ofMillis(30_000))
            .lifo()
            .metrics(true)
            .build()
    }

    private fun newClientBuilder(): WebClient.Builder {
        return WebClient.builder().codecs(defaultCodec)
    }

    private val defaultCodec = Consumer<ClientCodecConfigurer> { configurer ->
        val codecs: ClientCodecConfigurer.ClientDefaultCodecs = configurer.defaultCodecs()
        codecs.maxInMemorySize(-1) // UNLIMIT
    }
}
