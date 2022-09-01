package patrick.servlet.plus.util

import java.lang.reflect.Method

fun doMethod(apiHolder: Any, method: Method, params :Array<Any?> = arrayOf()): Any?{
    return method.invoke(apiHolder, *params)
}