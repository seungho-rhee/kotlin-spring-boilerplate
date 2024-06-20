package boilerplate.kotlin.spring.common.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    private inner class CacheOption(
        val name: String,
        val duration: Long = 1,
        val timeUnit: TimeUnit = TimeUnit.HOURS,
    )

    private val _caches = listOf(
        CacheOption(
            name = "USER",
            duration = 1,
            timeUnit = TimeUnit.HOURS, // 1 hour granted
        ),
        // ...
    )

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        val caches = caches(caches = _caches)

        cacheManager.setCaches(caches)

        return cacheManager
    }

    private fun caches(caches: List<CacheOption>): List<CaffeineCache> {
        return caches.map { cache ->
            CaffeineCache(
                cache.name,
                Caffeine.newBuilder()
                    .recordStats()
                    .expireAfterWrite(cache.duration, cache.timeUnit)
                    .build<Any, Any>()
            )
        }
    }
}