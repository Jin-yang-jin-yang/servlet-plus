package patrick.servlet.plus.auto.node

import patrick.servlet.plus.auto.node.api.ApiList
import patrick.servlet.plus.auto.node.filter.FilterList
import java.lang.reflect.Method

/**
 * 节点管理, 管理所有Api, Filter, Init, Destroy节点
 */
data class NodeManager(
    val classInstanceMap: HashMap<Class<*>, Any>,
    val apiList: ApiList,
    val filterList: FilterList,
    val initMethodList: List<Method>,
    val destroyMethodList: List<Method>
)