package patrick.servlet.plus.auto.servlet

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import patrick.servlet.plus.auto.config.ServletConfig.prefix
import patrick.servlet.plus.auto.node.Node
import patrick.servlet.plus.auto.assembling.initNode
import patrick.servlet.plus.auto.config.ServletConfig.suffix
import patrick.servlet.plus.auto.node.chain.FastChainMap
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.getPage404
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.getPage405
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.getPage500
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.getPageXXX
import patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant.http.HttpMethod
import patrick.servlet.plus.exception.api.ApiMatchException
import patrick.servlet.plus.exception.`package`.IllegalParamException
import patrick.servlet.plus.util.doChain
import patrick.servlet.plus.util.doMethod
import patrick.servlet.plus.util.replaceLast
import java.lang.reflect.Method
import java.util.*

class PlusServlet : HttpServlet() {

    private val log: Logger = LoggerFactory.getLogger(PlusServlet::class.java)
    private var node: Node? = null
    private var fastChainMap: FastChainMap? = null
    private var errorPathMap = HashMap<String, String>()

    override fun init() {
        log.info("plus servlet 开始初始化")
        prefix = getInitParameter("prefix") ?: ""
        suffix = getInitParameter("suffix") ?: ""

        val plusPackage = getInitParameter("plusPackage")
        if (plusPackage.isNullOrBlank()) throw IllegalParamException.PLUS_PACKAGE_IS_NULL
        node = initNode(plusPackage)
        fastChainMap = FastChainMap(node!!)

        doInitOrDestroyMethod(node!!.classInstanceMap, node!!.initMethodList)
    }

    private fun doInitOrDestroyMethod(plusClassSet: Map<Class<*>, Any>, initMethodList: List<Method>){
        for (method in initMethodList) {
            doMethod(plusClassSet[method.declaringClass]!!, method)
        }
    }

    private fun doAction(req: HttpServletRequest?, resp: HttpServletResponse?, httpMethod: HttpMethod) {
        req?.characterEncoding = "UTF-8"
        resp?.characterEncoding = "UTF-8"
        val url = req!!.requestURI.replace(prefix,"").replaceLast(suffix, "")
//        if (errorPathMap.contains(url)) {
//            resp?.writer?.print(errorPathMap[url])
//            return
//        }
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
        doAction(req, resp, HttpMethod.GET)
    }

    override fun doHead(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doAction(req, resp, HttpMethod.HEAD)
    }

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doAction(req, resp, HttpMethod.POST)
    }

    override fun doPut(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doAction(req, resp, HttpMethod.PUT)
    }

    override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doAction(req, resp, HttpMethod.DELETE)
    }

    override fun doOptions(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doAction(req, resp, HttpMethod.OPTIONS)
    }

    override fun doTrace(req: HttpServletRequest?, resp: HttpServletResponse?) {
        doAction(req, resp, HttpMethod.TRACE)
    }

    override fun destroy() {
        doInitOrDestroyMethod(node!!.classInstanceMap, node!!.destroyMethodList)
    }

}