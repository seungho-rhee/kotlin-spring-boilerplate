package boilerplate.kotlin.spring.api.user.converter

import boilerplate.kotlin.spring.api.user.dto.User
import boilerplate.kotlin.spring.api.user.dto.UserResponse
import boilerplate.kotlin.spring.api.user.entity.UserEntity
import boilerplate.kotlin.spring.common.util.toTimestamp


fun UserEntity.toService() = User(
    id = this.id,
    name = this.name,
    gender = this.gender,
    phoneNumber = this.phoneNumber,
    joinedAt = this.joinedAt,
    leftAt = this.leftAt,
)

fun User.toResponse() = UserResponse(
    id = this.id,
    name = this.name,
    gender = this.gender.name,
    phoneNumber = this.phoneNumber,
    joinedAt = this.joinedAt.toTimestamp().epochMilli,
    leftAt = this.leftAt?.toTimestamp()?.epochMilli,
)