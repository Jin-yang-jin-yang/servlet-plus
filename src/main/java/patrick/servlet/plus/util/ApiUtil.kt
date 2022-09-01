package patrick.servlet.plus.util

import patrick.servlet.plus.annotation.api.*
import patrick.servlet.plus.annotation.api.HeadApi
import patrick.servlet.plus.annotation.api.OptionsApi
import patrick.servlet.plus.annotation.api.TraceApi
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod
import java.lang.reflect.Method

fun apiSplit(path: String): List<String> {
    return path.split(Regex("/"))
}

fun getApiAnnotation(method: Method): Api? {
    for (annotation in method.annotations) {
        return when (annotation) {
            is Api -> annotation
            is DeleteApi -> Api(annotation.value, arrayOf(HttpMethod.DELETE))
            is GetApi -> Api(annotation.value, arrayOf(HttpMethod.GET))
            is HeadApi -> Api(annotation.value, arrayOf(HttpMethod.HEAD))
            is OptionsApi -> Api(annotation.value, arrayOf(HttpMethod.OPTIONS))
            is PatchApi -> Api(annotation.value, arrayOf(HttpMethod.PATCH))
            is PostApi -> Api(annotation.value, arrayOf(HttpMethod.POST))
            is PutApi -> Api(annotation.value, arrayOf(HttpMethod.PUT))
            is TraceApi -> Api(annotation.value, arrayOf(HttpMethod.TRACE))
            else -> continue
        }
    }
    return null
}

