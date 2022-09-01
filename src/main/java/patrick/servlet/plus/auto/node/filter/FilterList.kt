package patrick.servlet.plus.auto.node.filter

import patrick.servlet.plus.auto.node.filter.struct.FilterNode
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod
import java.util.TreeSet

class FilterList {
    private val beforeApiFilterNodeSet: MutableSet<FilterNode> = TreeSet { f1, f2 ->
        if (f1.equals(f2))
            0
        else {
            val order = f1.order - f2.order
            if (0 == order) 1 else order
        }
    }
    private val afterApiFilterNodeSet: MutableSet<FilterNode> = TreeSet { f1, f2 ->
        if (f1.equals(f2))
            0
        else {
            val order = f2.order - f1.order
            if (0 == order) 1 else order
        }
    }

    fun add(filterNode: FilterNode) =
        if (0 <= filterNode.order) beforeApiFilterNodeSet.add(filterNode) else afterApiFilterNodeSet.add(filterNode)

    fun getBeforeApiFilters(path: String, httpMethod: HttpMethod): List<FilterNode> {
        val resultList = ArrayList<FilterNode>()
        beforeApiFilterNodeSet.forEach {
            if (it.pathRegex.matches(path) && (it.httpMethods.contains(HttpMethod.ALL) || it.httpMethods.contains(httpMethod))) resultList.add(it)
        }
        return resultList
    }

    fun getAfterApiFilters(path: String, httpMethod: HttpMethod): List<FilterNode> {
        val resultList = ArrayList<FilterNode>()
        afterApiFilterNodeSet.forEach {
            if (it.pathRegex.matches(path)&& (it.httpMethods.contains(HttpMethod.ALL) || it.httpMethods.contains(httpMethod))) resultList.add(it)
        }
        return resultList
    }
}