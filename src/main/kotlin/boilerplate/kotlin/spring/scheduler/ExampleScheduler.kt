package boilerplate.kotlin.spring.scheduler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

@Configuration
@EnableScheduling
class ExampleScheduler(
    // ...
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    // 한국시간 기준 월-금 매일 오전 6시
    @Scheduled(cron = "0 0 6 * * MON-FRI", zone = "Asia/Seoul")
    fun unpaidPaymentScheduler() {
        log.info("Scheduler start: {}", LocalDateTime.now())
    }
}