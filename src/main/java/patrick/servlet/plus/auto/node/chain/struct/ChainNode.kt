package patrick.servlet.plus.auto.node.chain.struct

import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.node.filter.struct.FilterNode

/**
 * 请求执行链
 */
data class ChainNode(val beforeFilters: List<FilterNode>, val api: ApiNode, val afterFilters: List<FilterNode>)