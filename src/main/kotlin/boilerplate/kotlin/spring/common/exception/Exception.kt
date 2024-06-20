package boilerplate.kotlin.spring.common.exception

import org.springframework.http.HttpStatus

open class BaseException(
    val status: HttpStatus,
    override val message: String,
) : RuntimeException()

class BadRequestException : BaseException(
    status = HttpStatus.BAD_REQUEST,
    message = "잘못된 요청",
)

class InternalServerException : BaseException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = "서버 내부 오류",
)


class APIException(
    val code: String,
    val debugMessage: String? = null,
    val context: Any? = null,
) : RuntimeException()