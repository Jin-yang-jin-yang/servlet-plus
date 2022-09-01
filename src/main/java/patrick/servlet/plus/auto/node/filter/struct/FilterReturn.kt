package patrick.servlet.plus.auto.node.filter.struct

sealed class FilterReturn{
    data class Page(val path:String): FilterReturn()
    data class Body(val data: Any?): FilterReturn()
    data class Next(val isNextChain: Boolean = true, val data: Any? = null): FilterReturn()
}
