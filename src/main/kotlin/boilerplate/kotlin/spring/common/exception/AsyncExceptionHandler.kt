package boilerplate.kotlin.spring.common.exception

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import java.lang.reflect.Method


class AsyncExceptionHandler : AsyncUncaughtExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any) {
        when (ex) {
            is APIException -> return // do nothing
            else -> log.error("exception throw in async method: $method, ${ex.message}")
        }
    }
}

