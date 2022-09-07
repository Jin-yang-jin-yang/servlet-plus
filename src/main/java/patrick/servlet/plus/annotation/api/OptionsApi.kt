package patrick.servlet.plus.annotation.api

/**
 * 标记接口HTTP方法为OPTIONS, 及注明路径, 是特殊的@Api类型
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OptionsApi(val value: String = "")
