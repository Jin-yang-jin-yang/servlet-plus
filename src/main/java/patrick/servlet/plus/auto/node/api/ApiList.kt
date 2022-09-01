package patrick.servlet.plus.auto.node.api

import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.exception.api.ApiMatchException
import patrick.servlet.plus.util.apiSplit
import java.util.LinkedList

class ApiList {
    private val apiNodeMap: MutableMap<Int, MutableList<ApiNode>> = HashMap()

    fun add(apiNode: ApiNode) {
        val pathElementList = apiSplit(apiNode.path)
        val apiList = apiNodeMap.getOrElse(pathElementList.size) {
            val newPathList = ArrayList<ApiNode>()
            apiNodeMap[pathElementList.size] = newPathList
            newPathList
        }
        apiList.add(apiNode)
    }

    fun get(path: String, httpMethod: HttpMethod): ApiNode {
        var result: ApiNode? = null
        var is405 = false

        val pathList = apiSplit(path)
        if (!apiNodeMap.keys.contains(pathList.size)) throw ApiMatchException.noApiFit(path, httpMethod)

        val apiList = apiNodeMap[pathList.size]
        if (apiList != null) apiLoop@ for (it in apiList) {
            val apiPathElementList = it.pathElementList
            for ((i, element) in apiPathElementList.withIndex()) {
                if (element.startsWith("{") && element.endsWith("}")) continue
                if (element != pathList[i]) continue@apiLoop
            }
            if (it.httpMethods.contains(HttpMethod.ALL) || it.httpMethods.contains(httpMethod)) {
                if (null != result) throw ApiMatchException.tooManyApiFit(path, httpMethod) else {
                    result = it
                    is405 = false
                }
            } else is405 = true
        }
        if (null == result) {
            if(is405) throw ApiMatchException.noThatMethod(path, httpMethod)
            throw ApiMatchException.noApiFit(path, httpMethod)
        } else return result
    }

    fun getAll(): List<ApiNode>{
        val result = LinkedList<ApiNode>()
        for (apiNodes in apiNodeMap.values) {
            for (apiNode in apiNodes) {
                result.add(apiNode)
            }
        }
        return result
    }
}