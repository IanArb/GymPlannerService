import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.time.LocalDateTime
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [FutureDateValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class FutureDate(
    val message: String = "Date must be in the future",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class FutureDateValidator : ConstraintValidator<FutureDate, LocalDateTime> {
    override fun isValid(
        value: LocalDateTime?,
        context: ConstraintValidatorContext,
    ): Boolean = value?.isAfter(LocalDateTime.now()) ?: false
}
