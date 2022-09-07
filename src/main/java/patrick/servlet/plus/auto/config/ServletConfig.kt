package patrick.servlet.plus.auto.config

import patrick.servlet.plus.exception.`package`.IllegalParamException
import javax.servlet.ServletConfig

/**
 * servlet配置类
 */
object ServletConfig {
    /**
     * 项目路径前缀
     */
    var plusPackage: String = ""
        set(value) {
            if ("" == plusPackage) field = value
        }

    /**
     * 项目路径前缀
     */
    var prefix: String = ""
        set(value) {
            if ("" == prefix) field = value
        }

    /**
     * 项目路径后缀
     */
    var suffix: String = ""
        set(value) {
            if ("" == suffix) field = value
        }

    /**
     * 文件缓存上限
     */
    var fileCacheSize: Int? = null
        set(value) {
            if (null == fileCacheSize) field = value
        }

    /**
     * 可接受文件大小上限
     */
    var maxFileSize: Long? = null
        set(value) {
            if (null == maxFileSize) field = value
        }

    /**
     * 携带文件的请求大小上限
     */
    var maxFileRequestSize: Long? = null
        set(value) {
            if (null == maxFileRequestSize) field = value
        }


    /**
     * 初始化配置类
     *
     * @param config ServletConfig
     */
    fun initConfig(config: ServletConfig) {
        //初始化被扫描的包名称
        val plusPackageConfig = config.getInitParameter("plusPackage")
        plusPackage =
            if (null == plusPackageConfig || plusPackageConfig.isBlank()) throw IllegalParamException.PLUS_PACKAGE_IS_NULL else plusPackageConfig

        //初始化请求路径前缀
        val prefixConfig = config.getInitParameter("prefix")
        prefix = if (null == prefixConfig || prefixConfig.isBlank()) "" else prefixConfig

        //初始化请求路径后缀
        val suffixConfig = config.getInitParameter("suffix")
        suffix = if (null == suffixConfig || suffixConfig.isBlank()) "" else suffixConfig

        //初始化文件缓存大小
        val fileCacheSizeConfig = config.getInitParameter("fileCacheSize")
        fileCacheSize =
            if (null == fileCacheSizeConfig || fileCacheSizeConfig.isBlank()) 1024 * 1024 * 3 else Integer.parseInt(
                fileCacheSizeConfig
            )

        //初始化最大上传文件大小
        val maxFileSizeConfig = config.getInitParameter("maxFileSize")
        maxFileSize =
            if (null == maxFileSizeConfig || maxFileSizeConfig.isBlank()) 1024 * 1024 * 40L else maxFileSizeConfig.toLong()

        //初始化最大带有文件的请求大小
        val maxFileRequestSizeConfig = config.getInitParameter("maxFileRequestSize")
        maxFileRequestSize =
            if (null == maxFileRequestSizeConfig || maxFileRequestSizeConfig.isBlank()) 1024 * 1024 * 40L else maxFileRequestSizeConfig.toLong()

    }
}