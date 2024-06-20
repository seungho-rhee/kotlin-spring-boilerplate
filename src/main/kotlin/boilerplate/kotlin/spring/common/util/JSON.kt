package boilerplate.kotlin.spring.common.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * kotlin object mapper
 * jackson-module-kotlin required
 */
object JSON {
    val mapper: ObjectMapper
        get() = jacksonObjectMapper()
            .apply {
                registerModule(JavaTimeModule())
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }

    fun stringify(item: Any): String {
        return mapper.writeValueAsString(item)
    }

    inline fun <reified T> parse(json: String): T {
        return mapper.readValue(json, T::class.java)
    }

    fun <T> parse(json: String, clazz: Class<T>): T {
        return mapper.readValue(json, clazz)
    }


    fun Any?.toJson(): String? {
        return this?.let { stringify(it) }
    }
}