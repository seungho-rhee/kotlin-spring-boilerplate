package boilerplate.kotlin.spring.client.kafka

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

interface MessageClient {
    fun send(to: String, message: String)
}

@Component
@Profile("prod", "stage", "dev")
class KafkaClient(
    private val kafkaTemplate: KafkaTemplate<Nothing, String>,
) : MessageClient {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(to: String, message: String) {
        kafkaTemplate.send(to, message)
    }
}

@Component
@Profile("local", "test")
class KafkaMock : MessageClient {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(to: String, message: String) {
        // do nothing
    }
}
