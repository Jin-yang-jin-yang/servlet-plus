package patrick.servlet.plus.util

import jakarta.servlet.http.Cookie

fun Array<Cookie>.getAttribute(k: String): Cookie?{
    for (cookie in this) {
        if(cookie.name.equals(k)) return cookie
    }
    return null
}