package patrick.servlet.plus.auto.node.filter.struct

import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod

data class FilterNode(
    val pathRegex: Regex,
    val nodeParamList: List<NodeParam>,
    val httpMethods: Array<HttpMethod> = arrayOf(HttpMethod.ALL),
    val filter: java.lang.reflect.Method,
    val filterHolder: Any,
    val order: Int,
    val returnType: Class<out FilterReturn>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilterNode

        if (pathRegex != other.pathRegex) return false
        if (nodeParamList != other.nodeParamList) return false
        if (!httpMethods.contentEquals(other.httpMethods)) return false
        if (filter != other.filter) return false
        if (filterHolder != other.filterHolder) return false
        if (order != other.order) return false
        if (returnType != other.returnType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pathRegex.hashCode()
        result = 31 * result + nodeParamList.hashCode()
        result = 31 * result + httpMethods.contentHashCode()
        result = 31 * result + filter.hashCode()
        result = 31 * result + filterHolder.hashCode()
        result = 31 * result + order
        result = 31 * result + returnType.hashCode()
        return result
    }

}
