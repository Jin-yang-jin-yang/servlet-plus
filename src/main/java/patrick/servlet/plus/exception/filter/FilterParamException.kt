package patrick.servlet.plus.exception.filter

import java.lang.RuntimeException

class FilterParamException(msg: String) : RuntimeException(msg) {
    companion object {
        fun notFilterReturnType(clazz: Class<*>) = FilterParamException("$clazz 不是标准的过滤器返回类型")
    }
}