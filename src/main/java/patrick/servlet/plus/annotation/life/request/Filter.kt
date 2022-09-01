package patrick.servlet.plus.annotation.life.request

import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod

@Retention(AnnotationRetention.RUNTIME)
@Target( AnnotationTarget.FUNCTION)
annotation class Filter(
    val value: String = "[\\s\\S]*",
    val order: Int, // 绝对值越小约先执行. 正数表示前置过滤器, 负数表示后置过滤器, 0为最优先先前置过滤器.
    val httpMethods: Array<HttpMethod> = [HttpMethod.ALL],
)
