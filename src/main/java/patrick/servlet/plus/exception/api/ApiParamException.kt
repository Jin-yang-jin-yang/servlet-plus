package patrick.servlet.plus.exception.api

import java.lang.RuntimeException
import java.lang.reflect.Parameter

/**
 * Api节点参数异常
 */
class ApiParamException(msg: String): RuntimeException(msg) {
    companion object{
        //未知Api参数
        fun unknownApiParam(param: Parameter) = ApiParamException("无法为此参数(${param.type}:[${param.annotations}])配置信息")
    }
}