package patrick.servlet.plus.annotation.param

/**
 * 表明此参数来自上一个过滤器, 类型为Object, 或者对应类型
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FromBeforeNode
