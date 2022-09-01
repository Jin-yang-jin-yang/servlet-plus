package patrick.servlet.plus.auto.node.api.struct

import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.constant.ReturnType
import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.util.apiSplit

data class ApiNode(
    val path: String,
    val nodeParamList: List<NodeParam>,
    val httpMethods: Array<HttpMethod>,
    val api: java.lang.reflect.Method,
    val apiHolder: Any,
    val returnType: ReturnType
){
    val pathElementList: List<String> = apiSplit(path)
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