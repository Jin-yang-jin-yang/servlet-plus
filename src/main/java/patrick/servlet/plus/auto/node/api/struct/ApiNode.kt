package patrick.servlet.plus.auto.node.api.struct

import patrick.servlet.plus.constant.param.ReturnType
import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.util.apiSplit

/**
 * Api节点
 */
data class ApiNode(
    val path: String,//Api路径
    val nodeParamList: List<NodeParam>,//节点方法参数列表
    val httpMethods: Array<HttpMethod>,//接受的HTTP请求方法
    val api: java.lang.reflect.Method,//Api方法
    val apiHolder: Any,//Api方法所在类的实例
    val returnType: ReturnType//返回类型
){
    val pathElementList: List<String> = apiSplit(path)//Api路径数组
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApiNode

        if (path != other.path) return false
        if (nodeParamList != other.nodeParamList) return false
        if (!httpMethods.contentEquals(other.httpMethods)) return false
        if (api != other.api) return false
        if (apiHolder != other.apiHolder) return false
        if (returnType != other.returnType) return false
        if (pathElementList != other.pathElementList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + nodeParamList.hashCode()
        result = 31 * result + httpMethods.contentHashCode()
        result = 31 * result + api.hashCode()
        result = 31 * result + apiHolder.hashCode()
        result = 31 * result + returnType.hashCode()
        result = 31 * result + pathElementList.hashCode()
        return result
    }
}