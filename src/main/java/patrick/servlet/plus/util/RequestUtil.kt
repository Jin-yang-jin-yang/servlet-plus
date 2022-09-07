package patrick.servlet.plus.util

import javax.servlet.http.HttpServletRequest
import patrick.servlet.plus.exception.api.CastException
import java.lang.reflect.ParameterizedType

fun HttpServletRequest.getBody(): String {
    val body = StringBuffer()
    val reader = this.reader
    var str: String?

    str = reader.readLine()
    while (null != str) {
        body.append(str)
        str = reader.readLine()
    }
    reader.close()
    return body.toString()
}

fun getPathData(path: String, data: String): Map<String, String> {
    val result = HashMap<String, String>()
    val pathList = apiSplit(path)
    val urlList = apiSplit(data)

    for ((i, pathElement) in pathList.withIndex()) {
        if (pathElement.startsWith("{") && pathElement.endsWith("}")) {
            result[
                        pathElement.replace("{", "")
                        .replaceRange(
                            pathElement.length - 2,
                            pathElement.length - 1,
                            ""
                        )
            ] = urlList[i]
        }
    }
    return result
}

fun stringArrayToBasicData(data: Array<String?>, type: Class<*>): Any? {
    if (data.isEmpty()) return null
    if (java.util.List::class.java == type) {
        if (null == data[0]) return null
        when ((type.genericSuperclass as ParameterizedType).actualTypeArguments[0]) {
            java.lang.Integer::class.java, java.lang.Integer.TYPE -> {
                val list = ArrayList<Int>()
                for (datum in data) {
                    list.add(java.lang.Integer.parseInt(datum))
                }
                return list
            }

            java.lang.Short::class.java, java.lang.Short.TYPE -> {
                val list = ArrayList<Short>()
                for (datum in data) {
                    list.add(java.lang.Short.parseShort(datum))
                }
                return list
            }

            java.lang.Byte::class.java, java.lang.Byte.TYPE -> {
                val list = ArrayList<Byte>()
                for (datum in data) {
                    list.add(java.lang.Byte.parseByte(datum))
                }
                return list
            }

            java.lang.Long::class.java, java.lang.Long.TYPE -> {
                val list = ArrayList<Long>()
                for (datum in data) {
                    list.add(java.lang.Long.parseLong(datum))
                }
                return list
            }

            java.lang.Character::class.java, java.lang.Character.TYPE -> {
                val list = ArrayList<Char?>()
                for (datum in data) {
                    list.add(datum?.toCharArray()?.get(0))
                }
                return list
            }

            java.lang.Double::class.java, java.lang.Double.TYPE -> {
                val list = ArrayList<Double>()
                for (datum in data) {
                    list.add(java.lang.Double.parseDouble(datum))
                }
                return list
            }

            java.lang.Float::class.java, java.lang.Float.TYPE -> {
                val list = ArrayList<Float>()
                for (datum in data) {
                    list.add(java.lang.Float.parseFloat(datum))
                }
                return list
            }

            java.lang.Boolean::class.java, java.lang.Boolean.TYPE -> {
                val list = ArrayList<Boolean>()
                for (datum in data) {
                    list.add(java.lang.Boolean.parseBoolean(datum))
                }
                return list
            }

            String::class.java -> return data.toList()
            else -> throw CastException.castToBasicDataFailed(data, type)
        }

    } else {
        return when (type) {
            java.lang.Integer::class.java, java.lang.Integer.TYPE -> data[0]?.toInt()
            java.lang.Byte::class.java, java.lang.Byte.TYPE -> data[0]?.toByte()
            java.lang.Short::class.java, java.lang.Short.TYPE -> data[0]?.toShort()
            java.lang.Long::class.java, java.lang.Long.TYPE -> data[0]?.toLong()
            java.lang.Character::class.java, java.lang.Character.TYPE -> data[0]?.toCharArray()?.get(0)
            java.lang.Double::class.java, java.lang.Double.TYPE -> data[0]?.toDouble()
            java.lang.Float::class.java, java.lang.Float.TYPE -> data[0]?.toFloat()
            java.lang.Boolean::class.java, java.lang.Boolean.TYPE -> data[0]?.toBoolean()
            String::class.java -> data[0]
            else -> throw CastException.castToBasicDataFailed(data, type)
        }
    }
}

fun stringToBasicData(data: String?, type: Class<*>): Any? = stringArrayToBasicData(arrayOf(data), type)
