package boilerplate.kotlin.spring.common.dto

data class Page<T, S>(
    override val items: List<T>,
    val checkPoint: S?,
) : Items<T>(items = items)

open class Items<T>(open val items: List<T>)
fun <T> List<T>.toItems(): Items<T> = Items(items = this)

data class APISuccess<T>(
    override val code: String = APICode.OK.name, // default not
    override val debugMessage: String? = null, // for debug
    val data: T? = null,
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
