package patrick.servlet.plus.util

/**
 * String扩展方法, 替换最后一个匹配的子字符串
 *
 * @param oldStr 被替换的子字符串
 * @param newStr 替换的内容
 *
 * @return 返回替换好的字符串
 */
fun String.replaceLast(oldStr: String, newStr: String): String {
    val oldStrIndex = this.lastIndexOf(oldStr)
    return this.replaceRange(oldStrIndex, oldStrIndex + oldStr.length, newStr)
}