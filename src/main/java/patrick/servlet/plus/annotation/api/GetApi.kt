package patrick.servlet.plus.annotation.api

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class GetApi(val value: String)
