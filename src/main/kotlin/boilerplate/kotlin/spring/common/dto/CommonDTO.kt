package boilerplate.kotlin.spring.common.dto

data class APISuccess<T>(
    override val code: String = APICode.OK.name,
    override val debugMessage: String? = null, // for debug
    val context: T? = null,
) : APIResponse(code = code, debugMessage = debugMessage)

open class APIResponse(
    open val code: String = APICode.OK.name,
    open val debugMessage: String? = null, // for debug
)

enum class APICode {
    OK,
    USER_NOT_FOUND,
    USER_ALREADY,
    ;
}
