package patrick.servlet.plus.annotation.param

/**
 * 表明此参数类型为List<Part>, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数泛型类型
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class PartList
