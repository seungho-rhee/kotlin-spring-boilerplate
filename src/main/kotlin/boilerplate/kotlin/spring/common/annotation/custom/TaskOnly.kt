package boilerplate.kotlin.spring.common.annotation.custom

@Target(AnnotationTarget.FUNCTION) // 함수에 적용 가능
@Retention(AnnotationRetention.RUNTIME) // 런타임까지 유지
annotation class TaskOnly