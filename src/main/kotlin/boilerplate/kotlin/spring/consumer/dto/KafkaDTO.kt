package boilerplate.kotlin.spring.consumer.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KafkaDTO (
    @JsonProperty("id") val id: Long,
    @JsonProperty("name") val name: String,
)