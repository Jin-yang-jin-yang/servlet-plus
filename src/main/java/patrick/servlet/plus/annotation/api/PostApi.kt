package patrick.servlet.plus.annotation.api

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PostApi(val value: String)
