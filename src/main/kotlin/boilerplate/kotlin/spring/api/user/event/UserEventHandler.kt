package boilerplate.kotlin.spring.api.user.event

import boilerplate.kotlin.spring.api.user.event.dto.UpdateUserEvent
import boilerplate.kotlin.spring.api.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
@EnableAsync
class UserEventHandler(
    private val userService: UserService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    // TODO BEFORE_COMMIT

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun userAfterCommitEventListener(event: UpdateUserEvent) {
        log.info("Commit Event")
        val user = userService.getUser(userId = event.userId)

        userService.updateUser(
            userId = user.id,
            phoneNumber = event.phoneNumber,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun userAfterRollbackEventListener(event: UpdateUserEvent) {
        log.error("Rollback Event")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    fun userAfterCompletionEventListener(event: UpdateUserEvent) {
        log.info("Completion Event")
    }
}
