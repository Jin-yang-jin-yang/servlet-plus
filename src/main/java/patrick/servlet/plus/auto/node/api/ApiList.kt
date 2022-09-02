package patrick.servlet.plus.auto.node.api

import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.exception.api.ApiMatchException
import patrick.servlet.plus.util.apiSplit
import java.util.LinkedList

/**
 * Api节点列表, 维护所有Api节点
 */
class ApiList {
    private val apiNodeMap: MutableMap<Int, MutableList<ApiNode>> = HashMap()//Map<Api路径数组长度, Api节点列表>

    /**
     * 添加Api节点
     *
     * @param apiNode Api节点
     */
    fun add(apiNode: ApiNode) {
        val pathElementList = apiNode.pathElementList

        val apiList = apiNodeMap.getOrElse(pathElementList.size) {
            val newPathList = ArrayList<ApiNode>()
            apiNodeMap[pathElementList.size] = newPathList
            newPathList
        }
        apiList.add(apiNode)
    }

    /**
     * 取得Api节点
     *
     * @param path 请求路径
     * @param httpMethod 请求的HTTP方法
     *
     * @return Api节点
     *
     * @throws ApiMatchException 匹配不到或匹配到大于2个Api节点时抛出
     */
    fun get(path: String, httpMethod: HttpMethod): ApiNode {
        var result: ApiNode? = null
        var is405 = false//查询不到Api节点是是否返回405状态码

        val pathList = apiSplit(path)
        if (!apiNodeMap.keys.contains(pathList.size)) throw ApiMatchException.noApiFit(path, httpMethod)

        val apiList = apiNodeMap[pathList.size]
        if (apiList != null) apiLoop@ for (it in apiList) {
            val apiPathElementList = it.pathElementList
            for ((i, element) in apiPathElementList.withIndex()) {
                //检查是否为路径参数. 若是, 则直接比对下一个路径
                if (element.startsWith("{") && element.endsWith("}")) continue
                //路径不匹配
                if (element != pathList[i]) continue@apiLoop
            }
            if (it.httpMethods.contains(HttpMethod.ALL) || it.httpMethods.contains(httpMethod)) {
                if (null != result) throw ApiMatchException.tooManyApiFit(path, httpMethod)//匹配到多个路径
                else {
                    result = it
                    is405 = false
                }
            } else is405 = true // 根据路径匹配到Api节点, 但HTTP方法不匹配
        }
        if (null == result) {
            if (is405) throw ApiMatchException.noThatMethod(path, httpMethod)
            throw ApiMatchException.noApiFit(path, httpMethod)
        } else return result
    }

    /**
     * 获得所有Api节点
     *
     * @return 所有Api节点
     */
    fun getAll(): List<ApiNode> {
        val result = LinkedList<ApiNode>()
        for (apiNodes in apiNodeMap.values) {
            for (apiNode in apiNodes) {
                result.add(apiNode)
            }
        }
        return result
    }
}