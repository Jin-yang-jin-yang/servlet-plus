package patrick.servlet.plus.annotation.api

import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Api(val value: String, val httpMethods: Array<HttpMethod> = [HttpMethod.ALL])
