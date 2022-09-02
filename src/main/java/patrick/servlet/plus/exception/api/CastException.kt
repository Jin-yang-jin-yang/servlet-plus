package patrick.servlet.plus.exception.api

import java.lang.RuntimeException

/**
 * 数据转换异常
 */
class CastException(msg: String): RuntimeException(msg) {
    companion object{
        //无法转换
        fun castToBasicDataFailed(data: Array<String?>, type: Class<*>) = CastException("$data 无法转换为 $type")
    }
}