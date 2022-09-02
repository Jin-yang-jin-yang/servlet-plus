package patrick.servlet.plus.auto.node.filter.struct

import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.constant.http.HttpMethod

/**
 * Filter节点
 */
data class FilterNode(
    val pathRegex: Regex,//Filter过滤的路径正则表达式, 请求路径与之全匹配时Filter生效
    val nodeParamList: List<NodeParam>,////节点方法参数列表
    val httpMethods: Array<HttpMethod> = arrayOf(HttpMethod.ALL),//接受的HTTP请求方法
    val filter: java.lang.reflect.Method,//Filter方法
    val filterHolder: Any,//Filter方法所在类的实例
    val order: Int,//执行顺序, 绝对值越小约先执行. 正数表示前置过滤器, 负数表示后置过滤器, 0为最优先先前置过滤器
    val returnType: Class<out FilterReturn> //Filter返回类型
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
