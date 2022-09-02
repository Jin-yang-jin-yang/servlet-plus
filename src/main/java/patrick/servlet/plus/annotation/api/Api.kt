package patrick.servlet.plus.annotation.api

import patrick.servlet.plus.constant.http.HttpMethod

/**
 * 标记在类上时, 注明此类中所有API的路径前缀; 标记在方法上时, 标记接口HTTP方法及注明路径
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Api(val value: String, val httpMethods: Array<HttpMethod> = [HttpMethod.ALL])
