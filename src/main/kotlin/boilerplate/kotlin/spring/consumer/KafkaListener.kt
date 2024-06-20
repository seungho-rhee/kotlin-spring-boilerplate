package boilerplate.kotlin.spring.consumer

import boilerplate.kotlin.spring.common.util.JSON
import boilerplate.kotlin.spring.consumer.dto.KafkaDTO
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service

@Service
class KafkaListener(
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        id = "\${app.kafka.topics.example.group}",
        topics = ["\${app.kafka.topics.example.create}"],
        concurrency = "3", // listener thread size and partition size less
        autoStartup = "true"
    )
    fun listener(
        message: String,
        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) timestamp: Long,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partition: String,
    ) {
        val data = JSON.parse(message, KafkaDTO::class.java)
        try {
            log.info("consumer data {}", message.toString())
        } catch (e: Exception) {
            log.error("ERROR: consumer data {}", message.toString())
        }
    }
}