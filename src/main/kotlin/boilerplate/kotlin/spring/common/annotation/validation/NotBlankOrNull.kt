package boilerplate.kotlin.spring.common.annotation.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotBlankOrNullValidator::class])
@MustBeDocumented
annotation class NotBlankOrNull(
    val message: String = "String? invalid, zero-string or whitespace characters",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

@Component
class NotBlankOrNullValidator : ConstraintValidator<NotBlankOrNull, String> {

    /**
     * nullable 변수에 할당되는 밸리드
     *
     * null이면 통과
     * 값이 있는데 NotBlank 이면 통과
     * zero-string이나 whitespace는 false
     */
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return value.isNotBlank()
    }
}