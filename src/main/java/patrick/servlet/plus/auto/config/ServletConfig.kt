package patrick.servlet.plus.auto.config

object ServletConfig {
    var prefix: String = ""
        set(value) {
            if("" == prefix) field = value
        }

    var suffix: String = ""
        set(value) {
            if("" == suffix) field = value
        }
}