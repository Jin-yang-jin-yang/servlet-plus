package patrick.servlet.plus.auto.assembling

import jakarta.servlet.ServletContext
import jakarta.servlet.http.*
import patrick.servlet.plus.annotation.Plus
import patrick.servlet.plus.annotation.api.Api
import patrick.servlet.plus.annotation.api.ForwardOrDirect
import patrick.servlet.plus.annotation.life.clazz.Destroy
import patrick.servlet.plus.annotation.life.clazz.Init
import patrick.servlet.plus.annotation.life.request.Filter
import patrick.servlet.plus.annotation.param.*
import patrick.servlet.plus.auto.node.Node
import patrick.servlet.plus.auto.node.api.ApiList
import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.node.filter.FilterList
import patrick.servlet.plus.auto.node.filter.struct.FilterNode
import patrick.servlet.plus.auto.node.filter.struct.FilterReturn
import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.constant.ReturnType
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.param.InType
import patrick.servlet.plus.exception.api.ApiParamException
import patrick.servlet.plus.exception.filter.FilterParamException
import patrick.servlet.plus.exception.`package`.IllegalParamException
import patrick.servlet.plus.util.getApiAnnotation
import patrick.servlet.plus.util.scanClass
import java.io.PrintWriter
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

private fun putNodeHolderInstance(nodeHolder: Class<*>, plusClassMap: MutableMap<Class<*>, Any>): Boolean {
    if (null == nodeHolder.getAnnotation(Plus::class.java)) return false
    plusClassMap[nodeHolder] = nodeHolder.getConstructor().newInstance()
    return true
}

private fun getListParamType(param: Parameter, method: Method): Pair<InType, String> {

    val genericParameterTypes: Array<Type> = method.genericParameterTypes
    val index = method.parameters.indexOf(param)
    if (genericParameterTypes[index] is ParameterizedType) {
        return when ((genericParameterTypes[index] as ParameterizedType).actualTypeArguments[0].typeName) {
            Cookie::class.java.name -> Pair(InType.COOKIE_LIST, param.name)
            Part::class.java.name -> Pair(InType.LIST_FILE, param.name)
            else -> {
                if (null != param.getAnnotation(CookieList::class.java)) return Pair(InType.COOKIE_LIST, param.name)
                if (null != param.getAnnotation(PartList::class.java)) return Pair(InType.LIST_FILE, param.name)
                throw ApiParamException.unknownApiParam(param)
            }
        }

    }
    throw IllegalParamException.REFLECT_PARAM_CANNOT_CHECK_ACTUALLY_TYPE
}

private fun getBasicDataInTypeAndName(param: Parameter): Pair<InType, String> {
    var tempInTypeAndName = Pair(InType.BASIC_FORM_DATA, param.name)
    loop@ for (annotation in param.annotations) {
        when (annotation) {
            is FromContext -> {
                tempInTypeAndName = Pair(InType.SERVLET_CONTEXT_DATA, annotation.value.ifBlank { param.name })
                break@loop
            }

            is FromSession -> {
                tempInTypeAndName = Pair(InType.SESSION_DATA, annotation.value.ifBlank { param.name })
                break@loop
            }

            is FromCookie -> {
                tempInTypeAndName = Pair(InType.COOKIE_DATA, annotation.value.ifBlank { param.name })
                break@loop
            }

            is FromHead -> {
                tempInTypeAndName = Pair(InType.HEAD, annotation.value.ifBlank { param.name })
                break@loop
            }

            is FromPath -> {
                tempInTypeAndName = Pair(InType.PATH, annotation.value.ifBlank { param.name })
                break@loop
            }

            is FromRequest -> {
                tempInTypeAndName = Pair(InType.BASIC_FORM_DATA, annotation.value.ifBlank { param.name })
                break@loop
            }
        }
    }
    return tempInTypeAndName
}

private fun getObjectInTypeAndName(param: Parameter): Pair<InType, String> {
    var tempInTypeAndName = Pair(InType.OBJECT_FROM_FORM_DATA, param.name)
    loop@ for (annotation in param.annotations) {
        tempInTypeAndName = when (annotation) {
            is FromBody -> {
                Pair(InType.OBJECT_FROM_BODY, param.name)
                break@loop
            }

            is FromBeforeNode -> {
                Pair(InType.BEFORE_NODE_DATA, param.name)
                break@loop
            }

            else -> tempInTypeAndName
        }
    }
    return tempInTypeAndName
}

private fun getParamTypeAndName(param: Parameter): Pair<InType, String> {
    if(null != param.getAnnotation(FromBeforeNode::class.java)) return Pair(InType.BEFORE_NODE_DATA, param.name)
     return when (param.type) {
        ServletContext::class.java -> Pair(InType.SERVLET_CONTEXT, param.name)
        HttpSession::class.java -> Pair(InType.SESSION, param.name)
        HttpServletRequest::class.java -> Pair(InType.REQUEST, param.name)
        HttpServletResponse::class.java -> Pair(InType.RESPONSE, param.name)
        PrintWriter::class.java -> Pair(InType.OUT, param.name)
        Cookie::class.java -> {
            val fromCookie = param.getAnnotation(FromCookie::class.java)
            val fileName = fromCookie.value
            Pair(InType.COOKIE, fileName.ifBlank { param.name })
        }
        Map::class.java -> Pair(InType.MAP, param.name)
        Part::class.java -> {
            val fromFile = param.getAnnotation(FromFile::class.java)
            val fileName = fromFile.value
            Pair(InType.FILE, fileName.ifBlank { param.name })
        }
        java.lang.Integer::class.java,
        java.lang.Integer.TYPE,
        java.lang.Byte::class.java,
        java.lang.Byte.TYPE,
        java.lang.Short::class.java,
        java.lang.Short.TYPE,
        java.lang.Long::class.java,
        java.lang.Long.TYPE,
        java.lang.Character::class.java,
        java.lang.Character.TYPE,
        java.lang.Double::class.java,
        java.lang.Double.TYPE,
        java.lang.Float::class.java,
        java.lang.Float.TYPE,
        java.lang.Boolean::class.java,
        java.lang.Boolean.TYPE,
        String::class.java -> getBasicDataInTypeAndName(param)

        else -> getObjectInTypeAndName(param)
    }
}

private fun getNoApiPathParamList(method: Method): List<NodeParam> {
    val paramList = ArrayList<NodeParam>()
    for (param in method.parameters) {
        val inTypeAndName = if (java.util.List::class.java == param.type) {
            getListParamType(param, method)
        } else {
            getParamTypeAndName(param)
        }
        paramList.add(NodeParam(inTypeAndName.first, inTypeAndName.second, param.type, ""))
    }
    return paramList
}


private fun apiAndFilterHandler(nodeHolder: Any, apiList: ApiList, filterList: FilterList) {
    var topPath = ""
    val topApi = nodeHolder::class.java.getAnnotation(Api::class.java)
    if (null != topApi) topPath = topApi.value

    for (method in nodeHolder::class.java.methods) {
        val apiAnnotation = getApiAnnotation(method)
        val filterAnnotation = method.getAnnotation(Filter::class.java)

        if (null != apiAnnotation) {
            val isForwardOrDirect = (null != method.getAnnotation(ForwardOrDirect::class.java))

            val paramList = getNoApiPathParamList(method).map { it.apiPath = topPath + apiAnnotation.value; it }
            apiList.add(
                ApiNode(
                    topPath + apiAnnotation.value,
                    paramList,
                    apiAnnotation.httpMethods,
                    method,
                    nodeHolder,
                    if (isForwardOrDirect) ReturnType.PAGE else (if (Void.TYPE == method.returnType) ReturnType.NO_RETURN else ReturnType.OBJECT)
                )
            )
        }

        if (null != filterAnnotation) {
            val paramList = getNoApiPathParamList(method).map { it.apiPath = topPath + filterAnnotation.value; it }
            if (FilterReturn::class.java.isAssignableFrom(method.returnType)) {
                filterList.add(
                    FilterNode(
                        Regex(filterAnnotation.value),
                        paramList,
                        filterAnnotation.httpMethods,
                        method,
                        nodeHolder,
                        filterAnnotation.order,
                        method.returnType as Class<out FilterReturn>
                    )
                )
            } else
                throw FilterParamException.notFilterReturnType(method.returnType)
        }
    }
}

private fun initAndDestroyMethodHandler(
    apiClass: Class<*>,
    initMethodList: MutableList<Method>,
    destroyMethodList: MutableList<Method>
) {
    for (method in apiClass.methods) {
        if (null != method.getAnnotation(Init::class.java)) initMethodList.add(method)
        if (null != method.getAnnotation(Destroy::class.java)) destroyMethodList.add(method)
    }
}


fun initNode(packagePath: String): Node {
    val plusClassMap = HashMap<Class<*>, Any>()
    val apiList = ApiList()
    val filterList = FilterList()
    val initMethodList = ArrayList<Method>()
    val destroyMethodList = ArrayList<Method>()

    scanClass(packagePath) {
        val nodeHolder = Class.forName(it)
        if (!putNodeHolderInstance(nodeHolder, plusClassMap)) return@scanClass
        apiAndFilterHandler(plusClassMap[nodeHolder]!!, apiList, filterList)
        initAndDestroyMethodHandler(nodeHolder, initMethodList, destroyMethodList)
    }
    return Node(plusClassMap, apiList, filterList, initMethodList, destroyMethodList)
}
