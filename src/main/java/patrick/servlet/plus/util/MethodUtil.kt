package patrick.servlet.plus.util

import java.lang.reflect.Method

/**
 * 执行方法
 *
 * @param instance 方法所在的类的实例
 * @param method 被执行方法
 * @param params 参数数组, 默认为空数组
 */
fun doMethod(instance: Any, method: Method, params :Array<Any?> = arrayOf()): Any?{
    return method.invoke(instance, *params)
}