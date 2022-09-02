package patrick.servlet.plus.auto.servlet

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import patrick.servlet.plus.auto.config.ServletConfig.prefix
import patrick.servlet.plus.auto.node.NodeManager
import patrick.servlet.plus.auto.init.initNode
import patrick.servlet.plus.auto.config.ServletConfig.suffix
import patrick.servlet.plus.auto.node.chain.FastChainMap
import patrick.servlet.plus.constant.page.getPage404
import patrick.servlet.plus.constant.page.getPage405
import patrick.servlet.plus.constant.page.getPage500
import patrick.servlet.plus.constant.page.getPageXXX
import patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.exception.api.ApiMatchException
import patrick.servlet.plus.exception.`package`.IllegalParamException
import patrick.servlet.plus.util.doChain
import patrick.servlet.plus.util.doMethod
import patrick.servlet.plus.util.replaceLast
import java.lang.reflect.Method
import java.util.*

/**
 * servlet代理类
 */
class PlusServlet : HttpServlet() {

    private val log: Logger = LoggerFactory.getLogger(PlusServlet::class.java)
    private var nodeManager: NodeManager? = null
    private var fastChainMap: FastChainMap? = null
    private var errorPathMap = HashMap<String, String>()

    /**
     * 初始化所有节点
     *
     * @throws IllegalParamException 被扫描包为空包
     */
    override fun init() {
        log.info("plus servlet 开始初始化")

        prefix = getInitParameter("prefix") ?: ""
        log.info("代理请求的前缀为$prefix")

        suffix = getInitParameter("suffix") ?: ""
        log.info("代理请求的后缀为$suffix")

        log.info("开始进行包扫描")
        val plusPackage = getInitParameter("plusPackage")
        if (plusPackage.isNullOrBlank()) throw IllegalParamException.PLUS_PACKAGE_IS_NULL

        log.info("开始生成节点")
        nodeManager = initNode(plusPackage)

        log.info("开始生成过滤链")
        fastChainMap = FastChainMap(nodeManager!!)

        log.info("开始执行Init节点方法")
        doInitOrDestroyMethod(nodeManager!!.classInstanceMap, nodeManager!!.initMethodList)
    }

    /**
     * 执行Init或Destroy节点方法
     */
    private fun doInitOrDestroyMethod(plusClassSet: Map<Class<*>, Any>, methodList: List<Method>){
        for (method in methodList) {
            doMethod(plusClassSet[method.declaringClass]!!, method)
        }
    }

    /**
     * 执行请求
     *
     * @param httpMethod 请求的HTTP方法
     */
    private fun doAction(req: HttpServletRequest?, resp: HttpServletResponse?, httpMethod: HttpMethod) {
        req?.characterEncoding = "UTF-8"
        resp?.characterEncoding = "UTF-8"

        val url = req!!.requestURI.replace(prefix,"").replaceLast(suffix, "")
        //缓存发生异常地执行链, 方便下次快速响应
        if (errorPathMap.contains(url)) {
            resp?.writer?.print(errorPathMap[url])
            return
        }

        try {
            fastChainMap?.getChainNode(url, httpMethod)?.doChain(req, resp)
        } catch (pathException: ApiMatchException) {
            val msgPage = when (pathException.code) {
                500 -> {
                    resp?.status = 500
                    getPage500(Arrays.toString(pathException.stackTrace))
                }

                404 -> {
                    resp?.status = 404
                    getPage404()
                }

                405 -> {
                    resp?.status = 500
                    getPage405()
                }

                else -> getPageXXX()
            }
            errorPathMap[url] = msgPage
            resp?.writer?.print(msgPage)
        } catch (error: Throwable) {
            resp?.status = 500
            val msgPage = getPage500(Arrays.toString(error.stackTrace))
            errorPathMap[url] = msgPage
            resp?.writer?.print(msgPage)
        }
    }

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(log.isDebugEnabled){
            log.debug("接收到GET请求: ${req?.requestURI}")
        }
        doAction(req, resp, HttpMethod.GET)
    }

    override fun doHead(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(log.isDebugEnabled){
            log.debug("接收到HEAD请求: ${req?.requestURI}")
        }
        doAction(req, resp, HttpMethod.HEAD)
    }

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(log.isDebugEnabled){
            log.debug("接收到POST请求: ${req?.requestURI}")
        }
        doAction(req, resp, HttpMethod.POST)
    }

    override fun doPut(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(log.isDebugEnabled){
            log.debug("接收到PUT请求: ${req?.requestURI}")
        }
        doAction(req, resp, HttpMethod.PUT)
    }

    override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(log.isDebugEnabled){
            log.debug("接收到DELETE请求: ${req?.requestURI}")
        }
        doAction(req, resp, HttpMethod.DELETE)
    }

    override fun doOptions(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(log.isDebugEnabled){
            log.debug("接收到OPTIONS请求: ${req?.requestURI}")
        }
        doAction(req, resp, HttpMethod.OPTIONS)
    }

    override fun doTrace(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if(log.isDebugEnabled){
            log.debug("接收到TRACE请求: ${req?.requestURI}")
        }
        doAction(req, resp, HttpMethod.TRACE)
    }

    override fun destroy() {
        log.info("开始执行Destroy节点方法")
        doInitOrDestroyMethod(nodeManager!!.classInstanceMap, nodeManager!!.destroyMethodList)
    }

}