package boilerplate.kotlin.spring.common.annotation.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [MinZeroOrNullValidator::class])
@MustBeDocumented
annotation class MinZeroOrNull(
    val message: String = "Int? invalid, empty Or min 0",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

@Component
class MinZeroOrNullValidator : ConstraintValidator<MinZeroOrNull, Int> {

    /**
     * nullable 변수에 할당되는 밸리드
     *
     * null이면 통과
     * 값이 있고 0이상일때 통과
     */
    override fun isValid(value: Int?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return value >= 0
    }
}