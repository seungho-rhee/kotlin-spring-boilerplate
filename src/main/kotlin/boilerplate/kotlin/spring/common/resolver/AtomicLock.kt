package boilerplate.kotlin.spring.common.resolver

import boilerplate.kotlin.spring.common.exception.APIException
import boilerplate.kotlin.spring.common.exception.BadRequestException
import boilerplate.kotlin.spring.common.exception.BaseException
import boilerplate.kotlin.spring.common.util.toEpochMilli
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.expression.EvaluationException
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Duration
import java.time.LocalDateTime
import kotlin.reflect.KClass

/**
 * redis cache db를 이용한 lock
 * key 가 없는 경우 "REDIS-LOCK-$requestURL",
 * key 가 있는 경우 "REDIS-LOCK-$requestURL-$key" 입니다
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AtomicLock(
    val ttlSeconds: Long = 10,
    val key: String,
    val useURL: Boolean = true,
    val _throw: KClass<out BaseException> = BadRequestException::class,
)

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
class AtomicLockAspect(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val expressionParser: ExpressionParser = SpelExpressionParser()

    @Around("@annotation(AtomicLock)")
    fun atomicLock(joinPoint: ProceedingJoinPoint, blockDuplicateRequest: AtomicLock): Any? {
        val requestURL = getRequestUrl() ?: return joinPoint.proceed()

        val keyEXP = blockDuplicateRequest.key

        val key = getDynamicValue(
            parameterNames = (joinPoint.signature as MethodSignature).parameterNames,
            args = joinPoint.args,
            key = keyEXP,
        ) ?: return joinPoint.proceed()

        val redisKey = if (blockDuplicateRequest.useURL) {
            "REDIS-LOCK-$requestURL-$key"
        } else {
            "REDIS-LOCK-$key"
        }

        val ttl = Duration.ofSeconds(blockDuplicateRequest.ttlSeconds)

        val result = redisTemplate
            .boundValueOps(redisKey)
            .setIfAbsent("1", ttl)

        // 이미 진행중이라면
        if (result != true) {
            val throwable = blockDuplicateRequest._throw
                .java
                .getDeclaredConstructor()
                .newInstance()
                ?: BadRequestException()

            logger.info("중복 요청, key: $redisKey")
            throw throwable
        }

        // API 시작
        try {
            return joinPoint.proceed()
        } finally {
            // lock free
            val logStartTime = LocalDateTime.now().toEpochMilli()
            redisTemplate.delete(redisKey)
        }
    }

    private fun getRequestUrl(): String? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request

        return request.requestURI
    }

    // SpEL 구문을 파싱하여 내부값을 매핑해옵니다
    private fun getDynamicValue(
        parameterNames: Array<String>,
        args: Array<Any>,
        key: String,
    ): String? {
        val context = StandardEvaluationContext()
        for (i in parameterNames.indices) {
            context.setVariable(parameterNames[i], args[i])
        }

        return try {
            expressionParser.parseExpression(key).getValue(
                context,
                String::class.java
            )
        } catch (e: EvaluationException) {
            key
        } catch (e: Exception) {
            null
        }
    }
}