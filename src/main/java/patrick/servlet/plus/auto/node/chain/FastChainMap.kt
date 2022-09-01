package patrick.servlet.plus.auto.node.chain


import patrick.servlet.plus.auto.node.Node
import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.node.chain.struct.ChainNode
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.exception.api.ApiMatchException

class FastChainMap(private var node: Node) {
    private val deleteChainMap = HashMap<String, ChainNode>()
    private val getChainMap = HashMap<String, ChainNode>()
    private val headChainMap = HashMap<String, ChainNode>()
    private val optionsChainMap = HashMap<String, ChainNode>()
    private val patchChainMap = HashMap<String, ChainNode>()
    private val postChainMap = HashMap<String, ChainNode>()
    private val putChainMap = HashMap<String, ChainNode>()
    private val traceChainMap = HashMap<String, ChainNode>()

    init {
        apiLoop@ for (apiNode in node.apiList.getAll()) {
            for (path in apiNode.pathElementList) {
                if (path.startsWith("{") && path.endsWith("}")) continue@apiLoop
            }
            putApi(apiNode)
        }
    }

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


    private fun HashMap<String, ChainNode>.putChainNode(apiNode: ApiNode, httpMethod: HttpMethod) {
        if (this.contains(apiNode.path)) throw ApiMatchException.oneUrlMatchManyApi(apiNode.path, apiNode.httpMethods)
        this[apiNode.path] = ChainNode(
            node.filterList.getBeforeApiFilters(apiNode.path, httpMethod),
            apiNode,
            node.filterList.getAfterApiFilters(apiNode.path, httpMethod)
        )
    }

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
        } ?: ChainNode(
            node.filterList.getBeforeApiFilters(path, httpMethod),
            node.apiList.get(path, httpMethod),
            node.filterList.getAfterApiFilters(path, httpMethod)
        )
    }
}