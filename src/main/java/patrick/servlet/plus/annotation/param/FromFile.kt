package patrick.servlet.plus.annotation.param

/**
 * 表明此参数来自文件, 类型为Part, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class FromFile(val value: String = "")
