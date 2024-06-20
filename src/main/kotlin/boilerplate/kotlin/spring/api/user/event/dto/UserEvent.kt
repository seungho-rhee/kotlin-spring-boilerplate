package boilerplate.kotlin.spring.api.user.event.dto

data class UpdateUserEvent(
    val userId: Long,
    val phoneNumber: String?,
)