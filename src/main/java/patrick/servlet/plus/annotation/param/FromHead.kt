package patrick.servlet.plus.annotation.param

/**
 * 表明此参数自来请求头, 类型为基本数据类型及其包装类或String, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FromHead(val value: String = "")
