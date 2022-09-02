package patrick.servlet.plus.util

import patrick.servlet.plus.annotation.api.*
import patrick.servlet.plus.annotation.api.HeadApi
import patrick.servlet.plus.annotation.api.OptionsApi
import patrick.servlet.plus.annotation.api.TraceApi
import patrick.servlet.plus.constant.http.HttpMethod
import java.lang.reflect.Method

/**
 * 切割Api路径
 *
 * @param path Api路径
 *
 * @return 切割结果
 */
fun apiSplit(path: String): List<String> {
    return path.split(Regex("/"))
}

/**
 * 获得@Api注解
 *
 * @param method 检测方法
 *
 * @return 当不存在Api注解时返回null
 */
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

