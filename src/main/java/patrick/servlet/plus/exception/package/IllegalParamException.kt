package patrick.servlet.plus.exception.`package`

import java.lang.RuntimeException

class IllegalParamException(override val message: String): RuntimeException() {
    companion object{
        val PLUS_PACKAGE_IS_NULL = IllegalParamException("plusPackage 参数为null")
        val PACKAGE_NOT_EXIST = IllegalParamException("plusPackage 所指的包不存在")
        val REFLECT_PARAM_CANNOT_CHECK_ACTUALLY_TYPE = IllegalParamException("泛型参数无法读取泛型")
    }
}