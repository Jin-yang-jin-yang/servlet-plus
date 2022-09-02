package patrick.servlet.plus.constant.param

/**
 * 节点参数类型
 */
enum class InType {
    SERVLET_CONTEXT, //ServletContext
    SESSION, //HttpSession
    REQUEST, //HttpServletRequest
    RESPONSE, //HttpServletResponse
    OUT, //PrintWriter
    COOKIE, //Cookie
    COOKIE_LIST,//List<Cookie>

    SERVLET_CONTEXT_DATA, //基本数据类型及其包装类或String, 且参数来自ServletContext
    SESSION_DATA, //基本数据类型及其包装类或String, 且参数来自HttpSession
    REQUEST_DATA, //基本数据类型及其包装类或String, 且参数来自HttpServletRequest
    COOKIE_DATA, //基本数据类型及其包装类或String, 且参数来自Cookie

    OBJECT_FROM_FORM_DATA, //非String类的对象, 数据来自form-data
    OBJECT_FROM_BODY, //非String类的对象, 数据来自请求体
    BASIC_FORM_DATA, //基本数据类型及其包装类或String, 且参数来自form-data
    HEAD, //基本数据类型及其包装类或String, 且参数来自请求头
    PATH, //基本数据类型及其包装类或String, 且参数来自路径
    MAP, //所有form-data数据
    FILE, //Part文件

    LIST_FILE, //Part文件列表

    BEFORE_NODE_DATA //任意类型数据, 数据来自上一个节点
}