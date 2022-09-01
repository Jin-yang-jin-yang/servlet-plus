package patrick.servlet.plus.util

fun String.replaceLast(oldStr: String, newStr: String): String {
    val oldStrIndex = this.lastIndexOf(oldStr)
    return this.replaceRange(oldStrIndex, oldStrIndex + oldStr.length, newStr)
}