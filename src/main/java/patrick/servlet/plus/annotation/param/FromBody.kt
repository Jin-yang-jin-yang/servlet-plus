package patrick.servlet.plus.annotation.param

/**
 * 表明此参数来自请求体的JSON, 工具包会自动封装为对象, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FromBody
