package patrick.servlet.plus.auto.node.filter

import patrick.servlet.plus.auto.node.filter.struct.FilterNode
import patrick.servlet.plus.constant.http.HttpMethod
import java.util.TreeSet

/**
 * Filter节点列表, 维护所有Filter节点
 */
class FilterList {
    //前置Filter节点
    private val beforeApiFilterNodeSet: MutableSet<FilterNode> = TreeSet { f1, f2 ->
        if (f1.equals(f2))
            0
        else {
            val order = f1.order - f2.order
            if (0 == order) 1 else order
        }
    }

    //后置Filter节点
    private val afterApiFilterNodeSet: MutableSet<FilterNode> = TreeSet { f1, f2 ->
        if (f1.equals(f2))
            0
        else {
            val order = f2.order - f1.order
            if (0 == order) 1 else order
        }
    }

    /**
     * 添加Filter节点
     *
     * @param filterNode Filter节点
     */
    fun add(filterNode: FilterNode) =
        if (0 <= filterNode.order) beforeApiFilterNodeSet.add(filterNode) else afterApiFilterNodeSet.add(filterNode)

    /**
     * 根据请求路径和HTTP方法, 获得前置Filter节点
     *
     * @param path 请求路径
     * @param httpMethod 请求的HTTP方法
     *
     * @return 前置过滤节点有序列表
     */
    fun getBeforeApiFilters(path: String, httpMethod: HttpMethod): List<FilterNode> {
        val resultList = ArrayList<FilterNode>()
        beforeApiFilterNodeSet.forEach {
            if (it.pathRegex.matches(path) && (it.httpMethods.contains(HttpMethod.ALL) || it.httpMethods.contains(httpMethod))) resultList.add(it)
        }
        return resultList
    }

    /**
     * 根据请求路径和HTTP方法, 获得后置Filter节点
     *
     * @param path 请求路径
     * @param httpMethod 请求的HTTP方法
     *
     * @return 后置过滤节点有序列表
     */
    fun getAfterApiFilters(path: String, httpMethod: HttpMethod): List<FilterNode> {
        val resultList = ArrayList<FilterNode>()
        afterApiFilterNodeSet.forEach {
            if (it.pathRegex.matches(path)&& (it.httpMethods.contains(HttpMethod.ALL) || it.httpMethods.contains(httpMethod))) resultList.add(it)
        }
        return resultList
    }
}