package patrick.servlet.plus.util

import cn.hutool.json.JSONUtil
import org.apache.commons.fileupload.FileItem
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import patrick.servlet.plus.auto.config.ServletConfig.prefix
import patrick.servlet.plus.auto.config.ServletConfig.suffix
import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.node.chain.struct.ChainNode
import patrick.servlet.plus.auto.node.filter.struct.FilterNode
import patrick.servlet.plus.auto.node.filter.struct.FilterReturn
import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.constant.param.ReturnType
import patrick.servlet.plus.constant.param.InType
import patrick.servlet.plus.exception.filter.FilterParamException

/**
 * 从请求中获得数据
 * 
 * @param req HttpServletRequest
 * @param resp HttpServletResponse
 * @param nodeParamList 参数类型列表
 * @param beforeNodeData 来自上一个节点的数据
 * 
 * @return 数据列表
 */
private fun getParam(
    req: HttpServletRequest?,
    resp: HttpServletResponse?,
    nodeParamList: List<NodeParam>,
    beforeNodeData: Any?,
): List<Any?> {
    val result = ArrayList<Any?>()
    
    //路径参数数据, 懒加载
    var pathDataMap: Map<String, String>? = null

    /**
     * 加载pathDataMap
     * 
     * @param path 请求路径
     */
    fun getDataFromPath(path: String) {
        pathDataMap = getPathData(path, req!!.requestURI.replace(prefix, "").replaceLast(suffix, ""))
    }

    var fileItemList: List<FileItem>? = null //懒加载文件列表
    nodeParamList.forEach {

        when (it.inType) {
            InType.SERVLET_CONTEXT -> result.add(req?.session?.servletContext)
            InType.SESSION -> result.add(req?.session)
            InType.REQUEST -> result.add(req)
            InType.RESPONSE -> result.add(resp)
            InType.OUT -> result.add(resp?.writer)
            InType.COOKIE -> req?.cookies?.getAttribute(it.name)
            InType.COOKIE_LIST -> result.add(req?.cookies?.toList())

            InType.SERVLET_CONTEXT_DATA -> result.add(req?.session?.servletContext?.getAttribute(it.name))
            InType.SESSION_DATA -> result.add(req?.session?.getAttribute(it.name))
            InType.REQUEST_DATA -> result.add(req?.getAttribute(it.name))
            InType.COOKIE_DATA ->  req?.cookies?.getAttribute(it.name)?.value
            InType.OBJECT_FROM_FORM_DATA -> result.add(JSONUtil.toBean((req!!.parameterMap as Map<String,Array<String>>).toJson(), it.Type))
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
                if(null == fileItemList) fileItemList = getFile(req!!)
                result.add(fileItemList!!.find { file -> it.name == file.fieldName })
            }
            InType.FILE_LIST ->{
                if(null == fileItemList) fileItemList = getFile(req!!)
                result.add(fileItemList)
            }

            InType.BEFORE_NODE_DATA -> result.add(beforeNodeData)
        }
    }
    return result
}

/**
 * 执行请求转发或重定向
 * 
 * @param req HttpServletRequest
 * @param resp HttpServletResponse
 * @param path 请求转发或重定向的路径
 */
private fun forwardOrDirect(req: HttpServletRequest?, resp: HttpServletResponse?, path: String) {
    if (path.startsWith("redirect:")) {//重定向
        resp?.sendRedirect(path.replace("redirect:", ""))
    } else {//请求转发
        val tempPath = if (path.startsWith("forward:")) path.replace("forward:", "") else path
        req?.getRequestDispatcher(tempPath)?.forward(req, resp)
    }
}

/**
 * 执行Filter节点
 *
 * @param req HttpServletRequest
 * @param resp HttpServletResponse
 * @param filterNode Filter节点
 * @param preData 来自上一个节点的数据
 * 
 * @return 元组, first为是否执行下个节点, second为这个节点产生的数据
 * 
 */
private fun doFilter(
    req: HttpServletRequest?,
    resp: HttpServletResponse?,
    filterNode: FilterNode,
    preData: Any?,
): Pair<Boolean, Any?> {
    //执行Filter方法
    val returnObj =
        doMethod(
            filterNode.filterHolder,
            filterNode.filter,
            getParam(req, resp, filterNode.nodeParamList, preData).toTypedArray()
        )
    
    //判断返回类型
    return when (filterNode.returnType) {
        FilterReturn.ForwardOrDirect::class.java -> {//请求转发或重定向, 不执行下个节点
            forwardOrDirect(req, resp, (returnObj as FilterReturn.ForwardOrDirect).path)
            Pair(false, null)
        }

        FilterReturn.Body::class.java -> {//向响应体输出, 不执行下个节点
            resp?.writer?.print(JSONUtil.toJsonStr((returnObj as FilterReturn.Body).data))
            Pair(false, null)
        }

        //不做响应处理, isNextChain为true是执行下一个节点
        FilterReturn.Next::class.java -> Pair((returnObj as FilterReturn.Next).isNextChain, returnObj.data)
        else -> throw FilterParamException.notFilterReturnType(filterNode.returnType)
    }
}

/**
 * 执行Api节点
 *
 * @param req HttpServletRequest
 * @param resp HttpServletResponse
 * @param apiNode Api节点
 * @param preData 来自上一个节点的数据
 *
 * @return 执行结果
 */
private fun doApi(req: HttpServletRequest?, resp: HttpServletResponse?, apiNode: ApiNode, preData: Any?) =
    doMethod(apiNode.apiHolder, apiNode.api, getParam(req, resp, apiNode.nodeParamList, preData).toTypedArray())

/**
 * ChainNode扩展方法, 启动执行链
 *
 * @param req HttpServletRequest
 * @param resp HttpServletResponse
 */
fun ChainNode.doChain(req: HttpServletRequest?, resp: HttpServletResponse?) {
    var preData: Any? = null

    //按序前置Filter节点
    for (beforeFilter in this.beforeFilters) {
        val filterResult = doFilter(req, resp, beforeFilter, preData)
        if (!filterResult.first) return
        else {
            preData = filterResult.second
        }
    }

    //执行Api节点, 但不做请求响应
    val apiData = doApi(req, resp, this.api, preData)
    preData = apiData

    //按序后置Filter节点
    for (afterFilter in this.afterFilters) {
        val filterResult = doFilter(req, resp, afterFilter, preData)
        if (!filterResult.first) return
        else {
            preData = filterResult.second
        }
    }

    //响应请求
    when (this.api.returnType) {
        ReturnType.BODY -> resp?.writer?.print(JSONUtil.toJsonStr(apiData))
        ReturnType.FORWARD_OR_DIRECT -> forwardOrDirect(req, resp, apiData.toString())
        ReturnType.NO_RETURN -> return
    }
}