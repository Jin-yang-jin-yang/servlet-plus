package patrick.servlet.plus.util

import cn.hutool.json.JSONUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import patrick.servlet.plus.auto.config.ServletConfig.prefix
import patrick.servlet.plus.auto.config.ServletConfig.suffix
import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.node.chain.struct.ChainNode
import patrick.servlet.plus.auto.node.filter.struct.FilterNode
import patrick.servlet.plus.auto.node.filter.struct.FilterReturn
import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.constant.ReturnType
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.param.InType
import patrick.servlet.plus.exception.filter.FilterParamException

private fun getParam(
    req: HttpServletRequest?,
    resp: HttpServletResponse?,
    nodeParamList: List<NodeParam>,
    beforeNodeData: Any?,
): List<Any?> {
    val result = ArrayList<Any?>()
    var pathDataMap: Map<String, String>? = null
    fun getDataFromPath(path: String) {
        pathDataMap = getPathData(path, req!!.requestURI.replace(prefix, "").replaceLast(suffix, ""))
    }

    nodeParamList.forEach {
        when (it.inType) {
            InType.SERVLET_CONTEXT -> result.add(req?.servletContext)
            InType.SESSION -> result.add(req?.session)
            InType.REQUEST -> result.add(req)
            InType.RESPONSE -> result.add(resp)
            InType.OUT -> result.add(resp?.writer)
            InType.COOKIE -> {
                var needAddNull = true
                req?.cookies?.forEach cookieLoop@{ cookie ->
                    if (cookie.name.equals(it.name))
                        needAddNull = false
                    result.add(cookie)
                }
                if (needAddNull) result.add(null)
            }

            InType.COOKIE_LIST -> result.add(req?.cookies?.toList())

            InType.SERVLET_CONTEXT_DATA -> result.add(req?.servletContext?.getAttribute(it.name))
            InType.SESSION_DATA -> result.add(req?.session?.getAttribute(it.name))
            InType.REQUEST_DATA -> result.add(req?.getAttribute(it.name))
            InType.COOKIE_DATA -> {
                var needAddNull = true
                req?.cookies?.forEach cookieLoop@{ cookie ->
                    if (cookie.name.equals(it.name)) {
                        needAddNull = false
                        result.add(cookie.value)
                    }
                }
                if (needAddNull) result.add(null)
            }

            InType.OBJECT_FROM_FORM_DATA -> result.add(JSONUtil.toBean(req!!.parameterMap.toJson(), it.Type))
            InType.OBJECT_FROM_BODY -> result.add(JSONUtil.toBean(req?.getBody(), it.Type))
            InType.BASIC_FORM_DATA -> result.add(
                req?.getParameterValues(it.name)?.let { stringArray -> stringArrayToBasicData(stringArray, it.Type) }
            )

            InType.HEAD -> result.add(stringToBasicData(req?.getHeader(it.name), it.Type))
            InType.PATH -> {
                if (null == pathDataMap) getDataFromPath(it.apiPath)
                result.add(stringToBasicData(pathDataMap?.get(it.name), it.Type))
            }

            InType.MAP -> result.add(HashMap(req?.parameterMap))

            InType.FILE -> {
                try {
                    result.add(req?.getPart(it.name))
                } catch (e: java.lang.IllegalStateException) {
                    result.add(null)
                }
            }


            InType.LIST_FILE -> {
                try {
                    result.add(req?.parts)
                } catch (e: java.lang.IllegalStateException) {
                    result.add(null)
                }
            }

            InType.BEFORE_NODE_DATA -> result.add(beforeNodeData)
        }
    }
    return result
}

private fun toPage(req: HttpServletRequest?, resp: HttpServletResponse?, path: String) {
    if (path.startsWith("redirect:")) {
        resp?.sendRedirect(path.replace("redirect:", ""))
    } else {
        val tempPath = if (path.startsWith("forward:")) path.replace("forward:", "") else path
        req?.getRequestDispatcher(tempPath)?.forward(req, resp)
    }
}

private fun doFilter(
    req: HttpServletRequest?,
    resp: HttpServletResponse?,
    filterNode: FilterNode,
    preData: Any?,
): Pair<Boolean, Any?> {
    val returnObj =
        doMethod(
            filterNode.filterHolder,
            filterNode.filter,
            getParam(req, resp, filterNode.nodeParamList, preData).toTypedArray()
        )
    return when (filterNode.returnType) {
        FilterReturn.Page::class.java -> {
            toPage(req, resp, (returnObj as FilterReturn.Page).path)
            Pair(false, null)
        }

        FilterReturn.Body::class.java -> {
            resp?.writer?.print(JSONUtil.toJsonStr((returnObj as FilterReturn.Body).data))
            Pair(false, null)
        }

        FilterReturn.Next::class.java -> Pair((returnObj as FilterReturn.Next).isNextChain, returnObj.data)
        else -> throw FilterParamException.notFilterReturnType(filterNode.returnType)
    }
}

private fun doApi(req: HttpServletRequest?, resp: HttpServletResponse?, api: ApiNode, preData: Any?) =
    doMethod(api.apiHolder, api.api, getParam(req, resp, api.nodeParamList, preData).toTypedArray())

fun ChainNode.doChain(req: HttpServletRequest?, resp: HttpServletResponse?) {
    var preData: Any? = null

    for (beforeFilter in this.beforeFilters) {
        val filterResult = doFilter(req, resp, beforeFilter, preData)
        if (!filterResult.first) return
        else {
            preData = filterResult.second
        }
    }

    preData = doApi(req, resp, this.api, preData)
    val apiData: Any? = preData

    for (afterFilter in this.afterFilters) {
        val filterResult = doFilter(req, resp, afterFilter, preData)
        if (!filterResult.first) return
        else {
            preData = filterResult.second
        }
    }

    when (this.api.returnType) {
        ReturnType.OBJECT -> resp?.writer?.print(JSONUtil.toJsonStr(apiData))
        ReturnType.PAGE -> toPage(req, resp, apiData.toString())
        ReturnType.NO_RETURN -> return
    }
}