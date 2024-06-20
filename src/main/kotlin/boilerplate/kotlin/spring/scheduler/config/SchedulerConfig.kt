package boilerplate.kotlin.spring.scheduler.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar


@EnableScheduling
@Configuration
class SchedulerConfig : SchedulingConfigurer {
    @Value("\${thread.pool.size}")
    private val POOL_SIZE = 0

    override fun configureTasks(
        taskRegistrar: ScheduledTaskRegistrar,
    ) = taskRegistrar.setTaskScheduler(
        ThreadPoolTaskScheduler().apply {
            poolSize = POOL_SIZE
            setThreadNamePrefix("Scheduled-")
            initialize()
        }
    )
}