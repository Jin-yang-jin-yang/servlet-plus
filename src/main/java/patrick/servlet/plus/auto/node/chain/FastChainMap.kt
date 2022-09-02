package patrick.servlet.plus.auto.node.chain


import patrick.servlet.plus.auto.node.NodeManager
import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.node.chain.struct.ChainNode
import patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.exception.api.ApiMatchException

/**
 * 请求执行链查询Map, 维护着所有请求的执行链
 */
class FastChainMap(private var nodeManager: NodeManager) {
    //缓存所有不带路径参数的执行链
    private val deleteChainMap = HashMap<String, ChainNode>()
    private val getChainMap = HashMap<String, ChainNode>()
    private val headChainMap = HashMap<String, ChainNode>()
    private val optionsChainMap = HashMap<String, ChainNode>()
    private val patchChainMap = HashMap<String, ChainNode>()
    private val postChainMap = HashMap<String, ChainNode>()
    private val putChainMap = HashMap<String, ChainNode>()
    private val traceChainMap = HashMap<String, ChainNode>()

    init {
        //将所有不带路径参数的执行链按照HTTP方法缓存到对应Map<请求路径, 请求执行链>
        apiLoop@ for (apiNode in nodeManager.apiList.getAll()) {
            //检查请求路径是否有参数
            for (path in apiNode.pathElementList) {
                if (path.startsWith("{") && path.endsWith("}")) continue@apiLoop
            }
            putApi(apiNode)
        }
    }

    /**
     * 放置请求执行链到缓存
     *
     * @param apiNode Api节点
     */
    private fun putApi(apiNode: ApiNode) {
        for (httpMethod in apiNode.httpMethods) {
            when (httpMethod) {
                HttpMethod.ALL -> {
                    deleteChainMap.putChainNode(apiNode, HttpMethod.DELETE)
                    getChainMap.putChainNode(apiNode, HttpMethod.GET)
                    headChainMap.putChainNode(apiNode, HttpMethod.HEAD)
                    optionsChainMap.putChainNode(apiNode, HttpMethod.OPTIONS)
                    patchChainMap.putChainNode(apiNode, HttpMethod.PATCH)
                    postChainMap.putChainNode(apiNode, HttpMethod.POST)
                    putChainMap.putChainNode(apiNode, HttpMethod.PUT)
                    traceChainMap.putChainNode(apiNode, HttpMethod.TRACE)
                }

                HttpMethod.DELETE -> deleteChainMap.putChainNode(apiNode, httpMethod)
                HttpMethod.GET -> getChainMap.putChainNode(apiNode, httpMethod)
                HttpMethod.HEAD -> headChainMap.putChainNode(apiNode, httpMethod)
                HttpMethod.OPTIONS -> optionsChainMap.putChainNode(apiNode, httpMethod)
                HttpMethod.PATCH -> patchChainMap.putChainNode(apiNode, httpMethod)
                HttpMethod.POST -> postChainMap.putChainNode(apiNode, httpMethod)
                HttpMethod.PUT -> putChainMap.putChainNode(apiNode, httpMethod)
                HttpMethod.TRACE -> traceChainMap.putChainNode(apiNode, httpMethod)
            }
        }
    }

    /**
     * HashMap<String, ChainNode>的扩展方法, 根据ApiNode和HTTP方法生成请求执行链并放入Map中
     *
     * @param apiNode Api节点
     * @param httpMethod Api节点接受的HTTP方法
     *
     * @throws ApiMatchException Api节点重复放置时抛出
     */
    private fun HashMap<String, ChainNode>.putChainNode(apiNode: ApiNode, httpMethod: HttpMethod) {
        if (this.contains(apiNode.path)) throw ApiMatchException.oneUrlMatchManyApi(apiNode.path, apiNode.httpMethods)
        this[apiNode.path] = ChainNode(
            nodeManager.filterList.getBeforeApiFilters(apiNode.path, httpMethod),
            apiNode,
            nodeManager.filterList.getAfterApiFilters(apiNode.path, httpMethod)
        )
    }

    /**
     * 获得请求执行链
     */
    fun getChainNode(path: String, httpMethod: HttpMethod): ChainNode {
        return when (httpMethod) {
            HttpMethod.DELETE -> deleteChainMap[path]
            HttpMethod.GET -> getChainMap[path]
            HttpMethod.HEAD -> headChainMap[path]
            HttpMethod.PATCH -> patchChainMap[path]
            HttpMethod.POST -> postChainMap[path]
            HttpMethod.PUT -> putChainMap[path]
            HttpMethod.TRACE -> traceChainMap[path]
            else -> null
        } ?: ChainNode(//当缓存不存在此Api节点时去Node里查询
            nodeManager.filterList.getBeforeApiFilters(path, httpMethod),
            nodeManager.apiList.get(path, httpMethod),
            nodeManager.filterList.getAfterApiFilters(path, httpMethod)
        )
    }
}