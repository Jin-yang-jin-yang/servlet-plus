package patrick.servlet.plus.auto.config

/**
 * servlet配置类
 */
object ServletConfig {
    /**
     * 项目路径前缀
     */
    var prefix: String = ""
        set(value) {
            if("" == prefix) field = value
        }

    /**
     * 项目路径后缀
     */
    var suffix: String = ""
        set(value) {
            if("" == suffix) field = value
        }
}