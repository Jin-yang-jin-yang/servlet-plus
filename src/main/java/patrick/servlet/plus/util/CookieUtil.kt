package patrick.servlet.plus.util

import jakarta.servlet.http.Cookie

/**
 * Array<Cookie>扩展方法, 根据名称获取cookie值
 */
fun  Array<Cookie>.getAttribute(k: String): Cookie?{
    for (cookie in this) {
        if(cookie.name.equals(k)) return cookie
    }
    return null
}