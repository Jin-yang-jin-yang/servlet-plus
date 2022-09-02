package patrick.servlet.plus.exception.filter

import java.lang.RuntimeException

/**
 * Filter节点异常
 */
class FilterParamException(msg: String) : RuntimeException(msg) {
    companion object {
        //Filter返回类型不标准
        fun notFilterReturnType(clazz: Class<*>) = FilterParamException("$clazz 不是标准的过滤器返回类型")
    }
}