package patrick.servlet.plus.exception.api

import java.lang.RuntimeException
import java.lang.reflect.Parameter

class ApiParamException(msg: String): RuntimeException(msg) {
    companion object{
        fun unknownApiParam(param: Parameter) = ApiParamException("无法为此参数(${param.type}:[${param.annotations}])配置信息")
    }
}