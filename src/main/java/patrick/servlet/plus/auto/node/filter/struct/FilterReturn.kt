package patrick.servlet.plus.auto.node.filter.struct

/**
 * Filter返回类型
 */
sealed class FilterReturn{
    data class ForwardOrDirect(val path:String): FilterReturn()//进行请求转发或重定向, 需要重定向的路径前加"direct:", 需要请求转发的路径前加"forward:"或省略
    data class Body(val data: Any?): FilterReturn()// 直接向请求输出结果
    data class Next(val isNextChain: Boolean = true, val data: Any? = null): FilterReturn()//当isNextChain = true时进行下一个节点,否则终止请求链, data为向下一个节点传递的数据
}
