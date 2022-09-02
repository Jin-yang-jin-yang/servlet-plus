package patrick.servlet.plus.util

/**
 * Map<String,Array<String>>扩展方法, 将Map<String,Array<String>>转为JSON
 */
fun Map<String,Array<String>>.toJson(): String {
    val result = StringBuffer("{")
    this.forEach{
        result.append("\"${it.key}\":")
        if (1 == it.value.size)
            result.append("\"${it.value[0]}\"").append(",")
        else{
            result.append("[")
            it.value.forEach { value ->
                result.append("\"${value}\",")
            }
            result.delete(result.length-1, result.length).append("],")
        }
    }
    return result.delete(result.length-1, result.length).append("}").toString()
}