package patrick.servlet.plus.util

import patrick.servlet.plus.exception.`package`.IllegalParamException
import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.util.*
import java.util.jar.JarEntry

private fun dirScanner(currentDir: File, currentPackageName: String, action: (String) -> Unit){
    //currentDir默认存在且为文件夹
    val files = currentDir.listFiles()
    for (file in files!!) {
        if(file.isFile && file.name.endsWith(".class")){
            val className = "${currentPackageName}.${file.name.replace(".class", "")}"
            action(className)
        } else if(file.isDirectory){
            dirScanner(file, "${currentPackageName}.${file.name}",action)
        }
    }
}

private fun jarScanner(url: URL, action: (String) -> Unit) {
    /**
     * 由API查得：
     * 该方法返回一个URLConnection实例，表示与URL引用的远程对象的URL
     * 如果对于URL的协议（如HTTP或JAR）,则存在一个属于以下软件包或其子包之一的公共专用URLConnection子类：
     * java.long, java.io, java.util, java.net,返回的连接将是该子类
     */
    val jarURLConnection = url.openConnection() as JarURLConnection
    /**
     * 由API查得：
     * 返回此连接的JAR文件
     * 如果连接是与JAR文件的条目的连接，则返回JAR文件对象
     */
    val jarFile = jarURLConnection.jarFile
    /**
     * 由API查得：
     * 返回zip文件条目的枚举
     */
    val jarEntries: Enumeration<JarEntry> = jarFile.entries()
    for (jarEntry in jarEntries) {
        val jarName = jarEntry.name
        if (!jarEntry.isDirectory && jarName.endsWith(".class")) {
            val className = jarName.replace(".class", "").replace(Regex("/"), ".")
            action(className)
        }
    }
}

fun scanClass(packageName: String, action: (String) -> Unit){
    val packagePath = packageName.replace(Regex("\\."), "/")
    val classLoader = Thread.currentThread().contextClassLoader

    val resources = classLoader.getResources(packagePath)
    var isResourcesEmpty = true
    for (url in resources) {
        isResourcesEmpty = false
        if(url.protocol.equals("jar")){
            jarScanner(url, action)
        } else {
            val file = File(url.toURI())
            if(file.exists()){
                dirScanner(file, packageName, action)
            }
        }
    }
    if(isResourcesEmpty) throw IllegalParamException.PACKAGE_NOT_EXIST

}