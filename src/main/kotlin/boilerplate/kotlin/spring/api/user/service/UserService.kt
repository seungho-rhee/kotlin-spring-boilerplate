package boilerplate.kotlin.spring.api.user.service

import boilerplate.kotlin.spring.api.user.converter.toService
import boilerplate.kotlin.spring.api.user.dto.User
import boilerplate.kotlin.spring.api.user.entity.UserEntity
import boilerplate.kotlin.spring.api.user.entity.type.GenderType
import boilerplate.kotlin.spring.api.user.repository.UserRepository
import boilerplate.kotlin.spring.common.dto.APICode
import boilerplate.kotlin.spring.common.exception.APIException
import org.springframework.cache.annotation.Cacheable
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class UserService(
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun getUsers(): List<User> {
        // left user 제외
        val users = userRepository.getUsersWithoutLeft()

        if (users.isEmpty()) {
            throw APIException(code = APICode.USER_NOT_FOUND.name)
        }

        return users.map { user -> user.toService() }
    }

    @Cacheable(cacheNames = ["USER"], key = "#userId")
    @Transactional(readOnly = true)
    fun getUser(
        userId: Long,
    ): User {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw APIException(code = APICode.USER_NOT_FOUND.name)

        return user.toService()
    }

    @Transactional
    fun recordUser(
        name: String,
        birth: String,
        gender: GenderType,
        // ...
    ): User {

        val user = UserEntity(
            name = name,
            birth = birth,
            gender = gender,
        )

        userRepository.save(user)

        return user.toService()
    }

    @Transactional
    fun updateUser(
        userId: Long,
        phoneNumber: String?,
        // ...
    ): User {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw APIException(code = APICode.USER_NOT_FOUND.name)

        user.updatePhoneNumber(phoneNumber = phoneNumber)

        userRepository.save(user)

        return user.toService()
    }

    @Transactional
    fun leaveUser(
        userId: Long,
    ): User {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw APIException(code = APICode.USER_NOT_FOUND.name)

        user.updateLeftAt(leftAt = LocalDateTime.now())

        return user.toService()
    }

    @Transactional
    fun removeUser(
        userId: Long,
    ) {
        val user = userRepository.findByIdOrNull(userId)
            ?: return

        userRepository.delete(user)

        // or
        // userRepository.deleteById(userId)
    }
}