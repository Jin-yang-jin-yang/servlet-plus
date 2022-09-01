package patrick.servlet.plus.annotation.api

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class DeleteApi(val value: String)
