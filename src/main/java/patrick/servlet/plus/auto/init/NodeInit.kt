package patrick.servlet.plus.auto.init

/**
 * 用于构建节点(Api, Filter, Init, Destroy)列表
 */

import org.apache.commons.fileupload.FileItem
import javax.servlet.ServletContext
import javax.servlet.http.*
import patrick.servlet.plus.annotation.Plus
import patrick.servlet.plus.annotation.api.Api
import patrick.servlet.plus.annotation.api.ForwardOrDirect
import patrick.servlet.plus.annotation.life.clazz.Destroy
import patrick.servlet.plus.annotation.life.clazz.Init
import patrick.servlet.plus.annotation.filter.Filter
import patrick.servlet.plus.annotation.param.*
import patrick.servlet.plus.auto.node.NodeManager
import patrick.servlet.plus.auto.node.api.ApiList
import patrick.servlet.plus.auto.node.api.struct.ApiNode
import patrick.servlet.plus.auto.node.filter.FilterList
import patrick.servlet.plus.auto.node.filter.struct.FilterNode
import patrick.servlet.plus.auto.node.filter.struct.FilterReturn
import patrick.servlet.plus.auto.node.param.NodeParam
import patrick.servlet.plus.constant.param.ReturnType
import patrick.servlet.plus.constant.param.InType
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

/**
 * 判断nodeHolder所代表的类是否是@Plus类, 若是,就将其和它的实例放入plusClassMap中
 *
 * @param nodeHolder 被判断类
 * @param plusClassMap 类型-实例映射表
 * @return 判断nodeHolder所代表是否是@Plus类
 */
private fun putNodeHolderInstance(nodeHolder: Class<*>, plusClassMap: MutableMap<Class<*>, Any>): Boolean {
    if (null == nodeHolder.getAnnotation(Plus::class.java)) return false

    plusClassMap[nodeHolder] = nodeHolder.getConstructor().newInstance()
    return true
}

/**
 * 获得指定方法List参数的InType和名称
 *
 * @param param 方法参数, 应为List<T>类型
 * @param method param所属方法
 *
 * @return 返回元组, 元组的first为param的InType, second为参数名称
 *
 * @throws ApiParamException param没有@CookieList或@FileList注解, 且List<T>的T类型不为Cookie或FileItem时抛出
 * @throws IllegalParamException param没有@CookieList或@FileList注解, 且无法检测泛型类型时抛出
 */
private fun getListParamTypeAndName(param: Parameter, method: Method): Pair<InType, String> {

    //先判断是否有注解
    if (null != param.getAnnotation(CookieList::class.java)) return Pair(InType.COOKIE_LIST, param.name)
    if (null != param.getAnnotation(FileList::class.java)) return Pair(InType.FILE_LIST, param.name)

    //获得参数类型
    val genericParameterTypes: Array<Type> = method.genericParameterTypes
    val index = method.parameters.indexOf(param)
    if (genericParameterTypes[index] is ParameterizedType) {
        //获得泛型类型,并判断是不是Cookie或FileItem类型
        return when ((genericParameterTypes[index] as ParameterizedType).actualTypeArguments[0].typeName) {
            Cookie::class.java.name -> Pair(InType.COOKIE_LIST, param.name)
            FileItem::class.java.name -> Pair(InType.FILE_LIST, param.name)
            else -> throw ApiParamException.unknownApiParam(param)
        }
    }
    throw IllegalParamException.REFLECT_PARAM_CANNOT_CHECK_ACTUALLY_TYPE
}

/**
 * 获得基本数据类型及其包装类和String参数的InType和名称
 *
 * @param param 本数据类型或其包装类或String类型的Parameter
 *
 * @return 返回元组, 元组的first为param的InType类型, second为参数的名称, 若用户没有指定名称, 则通查找字节码中的方法参数名称
 */
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

/**
 * 返回除包装类和String类以外的对象的InType和参数名称
 *
 * @param param 除包装类和String类以外的对象
 *
 * @return 返回元组, 元组的first为param的InType类型, second为参数的名称
 */
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

/**
 * 获得方法参数的InType和名称
 *
 * @param param 方法参数
 *
 * @return 返回元组, 元组的first为param的InType类型, second为参数的名称, 若用户没有指定名称, 则通查找字节码中的方法参数名称
 */
private fun getParamTypeAndName(param: Parameter): Pair<InType, String> {
    if (null != param.getAnnotation(FromBeforeNode::class.java)) return Pair(InType.BEFORE_NODE_DATA, param.name)
    return when (param.type) {
        //特殊对象
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
        FileItem::class.java -> {
            val fileAnnotation = param.getAnnotation(File::class.java)
            if (null == fileAnnotation) Pair(InType.FILE, param.name) else {
                val fileName = fileAnnotation.value
                Pair(InType.FILE, fileName.ifBlank { param.name })
            }
        }

        //基本数据类型及其包装类和String
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

        //其他对象
        else -> getObjectInTypeAndName(param)
    }
}

/**
 * 获得不含apiPath数据的NodeParam列表
 *
 * @param method 被@Api或@Filter标记的方法
 *
 * @return 不含apiPath数据的NodeParam列表
 */
private fun getNoApiPathParamList(method: Method): List<NodeParam> {
    val paramList = ArrayList<NodeParam>()
    for (param in method.parameters) {
        //判断方法参数是否为List<T>类型
        val inTypeAndName = if (java.util.List::class.java == param.type) {
            getListParamTypeAndName(param, method)
        } else {
            getParamTypeAndName(param)
        }
        paramList.add(NodeParam(inTypeAndName.first, inTypeAndName.second, param.type, ""))
    }
    return paramList
}

/**
 * 处理被@Plus标记的类的方法的api或filter方法
 *
 * @param nodeHolder 被@Plus标记的类的实例
 * @param apiList Api节点列表
 *
 * @param filterList Filter节点列表
 *
 * @throws FilterParamException Filter的返回类型不是FilterReturn类型
 *
 */
private fun apiAndFilterHandler(nodeHolder: Any, apiList: ApiList, filterList: FilterList) {
    //获得此类的URL前缀
    var topPath = ""
    val topApi = nodeHolder::class.java.getAnnotation(Api::class.java)
    if (null != topApi) topPath = topApi.value

    //检查是否全部Api为转发或重定向
    val forwardOrDirectApi = nodeHolder::class.java.getAnnotation(ForwardOrDirect::class.java)
    val isForwardOrDirectApi = null != forwardOrDirectApi

    //扫描方法
    for (method in nodeHolder::class.java.methods) {
        val apiAnnotation = getApiAnnotation(method)
        val filterAnnotation = method.getAnnotation(Filter::class.java)

        //方法被@Api标记时
        if (null != apiAnnotation) {
            //判断返回类型是否是请求转发或重定向
            val isForwardOrDirect = (null != method.getAnnotation(ForwardOrDirect::class.java))

            val paramList = getNoApiPathParamList(method).map { it.apiPath = topPath + apiAnnotation.value; it }
            apiList.add(
                ApiNode(
                    topPath + apiAnnotation.value,
                    paramList,
                    apiAnnotation.httpMethods,
                    method,
                    nodeHolder,
                    if (isForwardOrDirectApi) ReturnType.FORWARD_OR_DIRECT else
                        (if (isForwardOrDirect) ReturnType.FORWARD_OR_DIRECT else
                                (if (Void.TYPE == method.returnType) ReturnType.NO_RETURN else
                                    ReturnType.BODY))
                )
            )
        }

        //方法被@Filter标记时
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

/**
 * 初始化Init和Destroy方法
 *
 * @param plusClass 被@Plus标记的类
 * @param initMethodList Init方法节点列表
 * @param destroyMethodList Destroy方法节点列表
 */
private fun initAndDestroyMethodHandler(
    plusClass: Class<*>,
    initMethodList: MutableList<Method>,
    destroyMethodList: MutableList<Method>
) {
    for (method in plusClass.methods) {
        if (null != method.getAnnotation(Init::class.java)) initMethodList.add(method)
        if (null != method.getAnnotation(Destroy::class.java)) destroyMethodList.add(method)
    }
}

/**
 * 初始化节点
 *
 * @param packagePath 需要扫描的包路径
 */
fun initNode(packagePath: String): NodeManager {
    val plusClassMap = HashMap<Class<*>, Any>()
    val apiList = ApiList()
    val filterList = FilterList()
    val initMethodList = ArrayList<Method>()
    val destroyMethodList = ArrayList<Method>()

    //包扫描
    scanClass(packagePath) {
        //初始化Class类
        val nodeHolderClass = Class.forName(it)
        //判断是否被@Plus标记
        if (!putNodeHolderInstance(nodeHolderClass, plusClassMap)) return@scanClass
        //获得Api和Filter节点列表
        apiAndFilterHandler(plusClassMap[nodeHolderClass]!!, apiList, filterList)
        //获得Init和Destroy方法列表
        initAndDestroyMethodHandler(nodeHolderClass, initMethodList, destroyMethodList)
    }
    return NodeManager(plusClassMap, apiList, filterList, initMethodList, destroyMethodList)
}
