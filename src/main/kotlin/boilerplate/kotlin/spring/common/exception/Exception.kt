package boilerplate.kotlin.spring.common.exception

import org.springframework.http.HttpStatus


open class BaseException(
    val status: HttpStatus,
    override val message: String,
) : RuntimeException()

class BadRequestException(
    message: String? = null,
) : BaseException(
    status = HttpStatus.BAD_REQUEST,
    message = message ?: "잘못된 요청",
)

class NotFoundException : BaseException(
    status = HttpStatus.NOT_FOUND,
    message = "리소스 찾을 수 없음",
)

class InternalServerException(
    message: String? = null,
) : BaseException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = message ?: "서버 내부 오류",
)

class APIException(
    val code: String,
    val debugMessage: String? = null,
    val data: Any? = null,
) : RuntimeException()
