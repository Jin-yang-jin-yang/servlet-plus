package patrick.servlet.plus.annotation.param

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FromPath(val value: String = "")