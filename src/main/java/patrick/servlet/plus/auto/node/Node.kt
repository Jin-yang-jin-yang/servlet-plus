package patrick.servlet.plus.auto.node

import patrick.servlet.plus.auto.node.api.ApiList
import patrick.servlet.plus.auto.node.filter.FilterList
import java.lang.reflect.Method

data class Node(
    val classInstanceMap: HashMap<Class<*>, Any>,
    val apiList: ApiList,
    val filterList: FilterList,
    val initMethodList: List<Method>,
    val destroyMethodList: List<Method>
)