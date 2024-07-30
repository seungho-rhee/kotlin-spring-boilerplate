package boilerplate.kotlin.spring.api.user.controller

import boilerplate.kotlin.spring.api.user.converter.toResponse
import boilerplate.kotlin.spring.api.user.dto.RecordUserRequest
import boilerplate.kotlin.spring.api.user.dto.UpdateUserRequest
import boilerplate.kotlin.spring.api.user.dto.UserResponse
import boilerplate.kotlin.spring.api.user.entity.type.GenderType
import boilerplate.kotlin.spring.api.user.event.dto.UpdateUserEvent
import boilerplate.kotlin.spring.api.user.service.UserService
import boilerplate.kotlin.spring.common.dto.APIResponse
import boilerplate.kotlin.spring.common.dto.APISuccess
import boilerplate.kotlin.spring.common.exception.BadRequestException
import boilerplate.kotlin.spring.common.resolver.AtomicLock
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@Validated
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @GetMapping
    fun getUserList(): APISuccess<List<UserResponse>> {
        val users = userService.getUsers()

        return APISuccess(
            data = users.map { user -> user.toResponse() }
        )
    }

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable @Min(0) userId: Long,
    ): APISuccess<UserResponse> {
        val user = userService.getUser(userId = userId)

        return APISuccess(data = user.toResponse())
    }

    @PostMapping
    @Transactional
    fun recordUser(
        @RequestBody @Valid body: RecordUserRequest,
    ): APISuccess<UserResponse> {
        // value validation
        if (body.gender == null && body.genderCode == null)
            throw BadRequestException()

        val gender = when {
            body.gender != null -> GenderType.fromStr(body.gender)
            body.genderCode != null -> GenderType.fromCode(body.genderCode)
            else -> throw BadRequestException()
        }

        val birthPattern = """^\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$"""
        if (Regex(birthPattern).matches(body.birth).not()) {
            throw BadRequestException()
        }

        // TODO user exist check

        val user = userService.recordUser(
            name = body.name,
            birth = body.birth,
            gender = gender,
        )

        // phoneNumber update event
        body.phoneNumber?.let { phoneNumber ->
            eventPublisher.publishEvent(
                UpdateUserEvent(
                    userId = user.id,
                    phoneNumber = phoneNumber,
                )
            )
        }

        return APISuccess(data = user.toResponse())
    }

    @PutMapping("/{userId}")
    @AtomicLock(ttlSeconds = 60, key = "#userId", useURL = false)
    fun updateUser(
        @PathVariable @Min(0) userId: Long,
        @RequestBody @Valid body: UpdateUserRequest,
    ): APISuccess<UserResponse> {
        val user = userService.updateUser(
            userId = userId,
            phoneNumber = body.phoneNumber,
        )

        return APISuccess(data = user.toResponse())
    }

    @PatchMapping("/{userId}")
    @AtomicLock(ttlSeconds = 60, key = "#userId", useURL = false)
    fun leaveUser(
        @PathVariable @Min(0) userId: Long,
    ): APISuccess<UserResponse> {
        val user = userService.leaveUser(userId = userId)

        return APISuccess(data = user.toResponse())
    }

    @DeleteMapping("/{userId}")
    @AtomicLock(ttlSeconds = 60, key = "#userId", useURL = false)
    fun removeUser(
        @PathVariable @Min(0) userId: Long,
    ): APIResponse {
        userService.removeUser(userId = userId)

        return APIResponse()
    }
}