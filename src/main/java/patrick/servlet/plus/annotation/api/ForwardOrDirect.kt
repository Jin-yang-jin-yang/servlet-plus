package patrick.servlet.plus.annotation.api

/**
 * 标记为接口返回的路径转发或重定向(需要重定向的路径前加"direct:", 需要请求转发的路径前加"forward:"或省略)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ForwardOrDirect
