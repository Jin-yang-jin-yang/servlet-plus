package patrick.servlet.plus.annotation.param

/**
 * 表明此参数来自Cookie, 类型为基本数据类型及其包装类或String, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FromCookie(val value: String = "")
