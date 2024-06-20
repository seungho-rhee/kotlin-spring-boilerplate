package boilerplate.kotlin.spring.client.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisCacheClient(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    fun <T> get(key: String, typeClass: Class<T>?): T? {
        return try {
            if (redisTemplate.hasKey(key)) {
                val result = redisTemplate.opsForValue().get(key)
                objectMapper.convertValue(result, typeClass)
            } else {
                null
            }
        } catch (e: Exception) {
            log.warn("[Redis] 캐쉬 읽기 실패. message = {}", e.message)
            e.printStackTrace()
            null
        }
    }

    fun set(
        key: String,
        value: Any,
        expiredAtMillis: Long,
    ) {
        try {
            redisTemplate.opsForValue().set(
                key, value,
                expiredAtMillis, TimeUnit.MILLISECONDS,
            )
        } catch (e: Exception) {
            log.warn("[Redis] 캐쉬 쓰기 실패. message = {}", e.message)
            e.printStackTrace()
        }
    }

    /**
     * redis 로 setnx 명령어를 사용하여 db에 key가 없을 때만 저장 후 true | false 반환
     */
    fun setNotExists(
        key: String,
        value: Any = key,
        expiredAtMillis: Long,
    ): Boolean {
        return try {
            redisTemplate
                .opsForValue()
                .setIfAbsent(
                    key, value,
                    expiredAtMillis, TimeUnit.MILLISECONDS,
                ) ?: false
        } catch (ignored: Exception) {
            false
        }
    }

    /**
     * redis del 명령어를 사용하여 전달된 key 를 삭제 한 후 true | false 반환
     */
    fun delete(key: String): Boolean {
        return try {
            redisTemplate.delete(key)
        } catch (ignored: Exception) {
            false
        }
    }
}