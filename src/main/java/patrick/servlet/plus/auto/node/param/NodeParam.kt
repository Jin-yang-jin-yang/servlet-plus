package patrick.servlet.plus.auto.node.param

import patrick.servlet.plus.constant.param.InType

/**
 * 节点方法参数
 */
data class NodeParam(
    val inType: InType, //参数InType
    val name: String, //参数名称
    val Type: Class<*>, //参数类型
    var apiPath: String //参数所在节点的请求路径
)
