package boilerplate.kotlin.spring.client.config

import io.lettuce.core.ClientOptions
import io.lettuce.core.ReadFrom
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions
import io.lettuce.core.resource.DefaultClientResources
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig(private val redisProperties: RedisProperties) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory =
        LettuceConnectionFactory(
            createStandaloneConfiguration(),
            createConnectionPoolConfiguration()
        ).apply {
            afterPropertiesSet()
        }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>().apply {
            setConnectionFactory(redisConnectionFactory!!)
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
            afterPropertiesSet()
        }

    private fun createConnectionPoolConfiguration(): LettucePoolingClientConfiguration {
        val poolConfig = GenericObjectPoolConfig()
            .apply {
                val pool = redisProperties.lettuce.pool
                /*
                * pool 의 최대 min / max connection 개수를 정의
                */
                maxTotal = pool.maxActive
                minIdle = pool.minIdle
                maxIdle = pool.maxIdle

                /*
                 * pool 내에 connection 이 모두 조진되었을 경우 block 시키지 않고 maxWait 시간만큼 대기
                 * true 인 경우 maxWait 만큼 대기
                 */
                blockWhenExhausted = true
                maxWaitMillis = pool.maxWait.toMillis()

                /*
                 * pool 내에 idle connection 제거 설정
                 * testWhileIdle - idle connection 제거 thread 실행 여부
                 * timeBetweenEvictionRuns - idle connection 제거 thread 실행간 휴면시간 ( = 실행 주기 )
                 */
                testWhileIdle = true
                timeBetweenEvictionRunsMillis = pool.timeBetweenEvictionRuns.toMillis()

                /*
                 * pool로 부터 connection 얻기 / 반환시 유효성 검사 설정
                 * default value 는 모두 false
                 */
                testOnCreate = true
                testOnBorrow = false
                testOnReturn = false
            }

        return LettucePoolingClientConfiguration.builder()
            .clientResources(DefaultClientResources.create())
            .poolConfig(poolConfig)
            .readFrom(ReadFrom.MASTER)
            .commandTimeout(Duration.ofMillis(1L * 1000))
            .clientOptions(createClientOptions())
            .shutdownTimeout(redisProperties.lettuce.shutdownTimeout)
            .build()
    }

    private fun createStandaloneConfiguration(): RedisStandaloneConfiguration =
        RedisStandaloneConfiguration(
            redisProperties.host,
            redisProperties.port
        ).apply {
            setPassword(redisProperties.password)
        }

    private fun createClientOptions(): ClientOptions =
        ClusterClientOptions.builder()
            .autoReconnect(true)
            .topologyRefreshOptions(
                ClusterTopologyRefreshOptions.builder()
                    .enablePeriodicRefresh(Duration.ofMillis(2L * 1000))
                    .enableAllAdaptiveRefreshTriggers()
                    .closeStaleConnections(true)
                    .build()
            )
            .build()
}