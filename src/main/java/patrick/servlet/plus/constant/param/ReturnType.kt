package patrick.servlet.plus.constant.param

/**
 * 返回类型
 */
enum class ReturnType {
    BODY, //直接向响应体输出
    FORWARD_OR_DIRECT, //请求转发或重定向
    NO_RETURN //不作处理
}