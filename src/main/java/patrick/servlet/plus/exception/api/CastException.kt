package patrick.servlet.plus.auto.servlet.patrick.servlet.plus.exception.api

import java.lang.RuntimeException

class CastException(msg: String): RuntimeException(msg) {
    companion object{
        fun castToBasicDataFailed(data: Array<String?>, type: Class<*>) = CastException("$data 无法转换为 $type")
    }
}