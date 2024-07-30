package boilerplate.kotlin.spring.api.user.dto

import boilerplate.kotlin.spring.api.user.entity.type.GenderType
import boilerplate.kotlin.spring.common.validation.MinZeroOrNull
import boilerplate.kotlin.spring.common.validation.NotBlankOrNull
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class RecordUserRequest(
    @NotBlank val name: String,
    @NotBlank val birth: String,
    @NotBlankOrNull val gender: String?,
    @MinZeroOrNull val genderCode: Int?,
    @NotBlankOrNull val phoneNumber: String?
)

data class UpdateUserRequest(
    @NotBlankOrNull val phoneNumber: String?
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