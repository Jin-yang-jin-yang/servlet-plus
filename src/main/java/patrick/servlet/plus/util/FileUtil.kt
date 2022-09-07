package patrick.servlet.plus.util

import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.util.Streams
import patrick.servlet.plus.auto.config.ServletConfig
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.servlet.http.HttpServletRequest

/**
 * 上传数据及保存文件
 */
fun getFile(request: HttpServletRequest): List<FileItem> {
    val result = ArrayList<FileItem>()

    // 检测是否为多媒体上传, 如果不是则停止
    if (!ServletFileUpload.isMultipartContent(request)) return result

    // 配置上传参数
    val factory = DiskFileItemFactory()
    // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
    factory.sizeThreshold = ServletConfig.fileCacheSize!!
    // 设置临时存储目录
    factory.repository = File(System.getProperty("java.io.tmpdir"))
    val upload = ServletFileUpload(factory)

    // 设置最大文件上传值
    upload.fileSizeMax = ServletConfig.maxFileSize!!

    // 设置最大请求值 (包含文件和表单数据)
    upload.sizeMax = ServletConfig.maxFileRequestSize!!

    // 中文处理
    upload.headerEncoding = "UTF-8"


    // 解析请求的内容提取文件数据
    val formItems = upload.parseRequest(request)
    if (formItems.isNotEmpty()) {
        // 迭代表单数据
        for (item in formItems) {
            // 处理不在表单中的字段
            if (!item.isFormField) {
                result.add(item)
            }
        }
    }

    return result
}

fun FileItem.toFile(path: String){
    Streams.copy(this.inputStream, BufferedOutputStream(FileOutputStream(path)), true)
}
