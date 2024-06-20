package boilerplate.kotlin.spring.common.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.hibernate.TypeMismatchException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.security.InvalidParameterException

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(APIException::class)
    fun handleAPIException(
        ex: Throwable,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<APIErrorBody> {
        return APIErrorResponse(ex as APIException)
    }

    @ExceptionHandler(
        value = [
            BadRequestException::class,
            BindException::class,
            HttpMessageNotReadableException::class,
            InvalidParameterException::class,
            MissingServletRequestParameterException::class,
            TypeMismatchException::class,
            ConstraintViolationException::class,
            ServletRequestBindingException::class,
            MethodArgumentNotValidException::class,
            MethodArgumentTypeMismatchException::class,
            IllegalStateException::class,
            IllegalArgumentException::class,
            DataIntegrityViolationException::class, // duplicated write
            HttpRequestMethodNotSupportedException::class // http request method not support
        ],
    )
    fun handleInvalidParamException(e: Exception?): ResponseEntity<ErrorBody> {
        log.warn("[GlobalExceptionHandler] - bad request")
        return ErrorResponse(BadRequestException())
    }

    @ExceptionHandler(Throwable::class)
    fun handleException(
        ex: Throwable,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<ErrorBody> {
        log.error("[GlobalExceptionHandler] - unknown", ex)
        return ErrorResponse(InternalServerException())
    }


}

class APIErrorResponse(ex: APIException) : ResponseEntity<APIErrorBody>(
    APIErrorBody(ex),
    HttpStatus.OK,
)

data class APIErrorBody(
    val code: String,
    val debugMessage: String?,
    val context: Any?,
) {
    constructor(e: APIException) : this(
        code = e.code,
        debugMessage = e.debugMessage,
        context = e.context,
    )
}


class ErrorResponse(ex: BaseException) : ResponseEntity<ErrorBody>(
    ErrorBody(ex),
    ex.status,
)

data class ErrorBody(
    val status: Int,
    val message: String,
) {
    constructor(ex: BaseException) : this(
        status = ex.status.value(),
        message = ex.message,
    )
}
