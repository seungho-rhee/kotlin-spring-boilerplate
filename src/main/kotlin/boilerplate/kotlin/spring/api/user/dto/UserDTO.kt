package boilerplate.kotlin.spring.api.user.dto

import boilerplate.kotlin.spring.api.user.entity.type.GenderType
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class RecordUserRequest(
    @NotBlank val name: String,
    @NotBlank val birth: String,
    val gender: String?,
    val genderCode: Int?,
    val phoneNumber: String?
)

data class UpdateUserRequest(
    val phoneNumber: String?
)


data class User(
    val id: Long,
    val name: String,
    val gender: GenderType,
    var phoneNumber: String?,
    val joinedAt: LocalDateTime,
    var leftAt: LocalDateTime?,
)

data class UserResponse(
    val id: Long,
    val name: String,
    val gender: String,
    var phoneNumber: String?,
    val joinedAt: Long,
    var leftAt: Long?,
)