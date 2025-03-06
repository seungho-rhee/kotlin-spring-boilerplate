package boilerplate.kotlin.spring.common.annotation.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EpochTimeValidator::class])
@MustBeDocumented
annotation class EpochTime(
    val message: String = "Timestamp(ms) Invalid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

@Component
class EpochTimeValidator : ConstraintValidator<EpochTime, Long> {

    override fun isValid(value: Long?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return false
        }

        // 1970년 1월 1일 00시 00분 00초 < value < 2038년 1월 19일 00시 00분 00초
        return value in 0..2147472000000
    }
}