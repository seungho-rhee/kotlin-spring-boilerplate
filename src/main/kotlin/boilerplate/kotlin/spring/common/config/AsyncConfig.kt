package boilerplate.kotlin.spring.common.config

import boilerplate.kotlin.spring.common.exception.AsyncExceptionHandler
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync


@EnableAsync
@Configuration
class AsyncConfig : AsyncConfigurer {
    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return AsyncExceptionHandler()
    }
}