package patrick.servlet.plus.auto.node.param

import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.param.InType

data class NodeParam(val inType: InType, val name: String, val Type: Class<*>, var apiPath: String)
