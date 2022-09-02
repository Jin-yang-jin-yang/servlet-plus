package patrick.servlet.plus.exception.api

import patrick.servlet.plus.constant.http.HttpMethod
import java.lang.RuntimeException

/**
 * Api匹配异常
 *
 * @param msg 异常描述
 * @param code 错误码
 */
class ApiMatchException(msg: String, val code: Int) : RuntimeException(msg) {
    companion object {
        //多个Api节点匹配
        fun tooManyApiFit(path: String, httpMethod: HttpMethod) =
            ApiMatchException("${httpMethod.name} $path 可以匹配到多个Api", 500)

        //无Api节点匹配
        fun noApiFit(path: String, httpMethod: HttpMethod) =
            ApiMatchException("${httpMethod.name} $path 没有可以匹配的Api", 404)

        //路径冲突
        fun oneUrlMatchManyApi(path: String, httpMethods: Array<HttpMethod>) =
            ApiMatchException("${httpMethods.contentDeepToString()} $path 与其他Api路径冲突", 500)

        //405
        fun noThatMethod(path: String, httpMethods: HttpMethod) =
            ApiMatchException("${httpMethods.name} $path 不支持当前HTTP方法", 405)
    }
}