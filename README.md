# Servlet请求自动分发工具包

    可以自动分发请求到具体类

## 注解

### 类注解

    @Plus: 标记此类为被分发类, 只有此注解存在时, 工具包才会扫描其中的方法
    @Api(value= path): 注明此类中所有API的路径前缀

### Api注解

    @Api(value= path, httpMethod = {此API接受的HTTP方法}): 标记接口HTTP方法及注明路径
    @DeleteApi(value = path): 标记接口HTTP方法为DELETE, 及注明路径
    @GetApi(value = path): 标记接口HTTP方法为GET, 及注明路径
    @HeadApi(value = path): 标记接口HTTP方法为HEAD, 及注明路径
    @OptionsApi(value = path): 标记接口HTTP方法为OPTIONS, 及注明路径
    @PatchApi(value = path): 标记接口HTTP方法为PATCH, 及注明路径
    @PostApi(value = path): 标记接口HTTP方法为POST, 及注明路径
    @PutApi(value = path): 标记接口HTTP方法为PUT, 及注明路径
    @TraceApi(value = path): 标记接口HTTP方法为TRACE, 及注明路径
    @ForwardOrDirect: 标记为接口返回的路径转发或重定向(需要重定向的路径前加"direct:", 需要请求转发的路径前加"forward:"或省略)

### 过滤器注解

    @Filter(
        value = 要匹配的正则表达式, 默认为"[\\s\\S]*"
        order = 执行顺序, 绝对值越小约先执行. 正数表示前置过滤器, 负数表示后置过滤器, 0为最优先先前置过滤器
        httpMethods = 要过滤的HTTP方法, 默认为{HttpMethod.ALL}
    )

### 方法参数注解

    @CookieList: 表明此参数类型为List<Cookie>, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数泛型类型
    @FromBeforeNode: 表明此参数来自上一个过滤器, 类型为Object, 或者对应类型
    @FromBody(value = name): 表明此参数来自请求体的JSON, 工具包会自动封装为对象, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromContext(value = name): 表明此参数来自servletContext, 类型为基本数据类型及其包装类或String, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromCookie(value = name): 表明此参数来自Cookie, 类型为基本数据类型及其包装类或String, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromFile(value = name): 表明此参数来自文件, 类型为Part, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromHead(value = name): 表明此参数自来请求头, 类型为基本数据类型及其包装类或String, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromPath(value = name): 表明此参数来自请路径, 类型为基本数据类型及其包装类或String, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromRequest(value = name): 表明此参数来自form-data, 是缺省注解, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromSession(value = name): 表明此参数来自请Session, 类型为基本数据类型及其包装类或String, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数名称
    @FromList: 表明此参数类型为List<Part>, jdk1.8及以上value可缺省, 工具包将会直接检查方法参数泛型类型
    
    此外,若参数为Map<String, String[]>类型,则会装配为form-data的Map

### 生命周期注解
    @Destroy: 表明方法在服务器关闭时执行
    @Init: 表明方法在开始服务前执行

## Api方法
    Api方法应该为public

## 过滤器方法
    过滤器方法应该为public

## 生命周期方法
    生命周期方法应该为public, 无参数, 无返回值

## 配置如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">
    <servlet>
        <servlet-name>ServletPlus</servlet-name>
        <servlet-class>patrick.servlet.plus.auto.servlet.PlusServlet</servlet-class>
        <load-on-startup>0</load-on-startup>
        <init-param>
            <param-name>prefix</param-name>
            <param-value>项目请求路径前缀, 匹配Api时自动替换为""</param-value>
        </init-param>
        <init-param>
            <param-name>suffix</param-name>
            <param-value>项目请求路径后缀, 匹配Api时自动替换为""</param-value>
        </init-param>
        <init-param>
            <param-name>plusPackage</param-name>
            <param-value>包扫描路径</param-value>
        </init-param>
        <description>代理所有plus请求</description>
    </servlet>

    <servlet-mapping>
        <servlet-name>ServletPlus</servlet-name>
        <url-pattern>与suffix匹配(比如suffix为".do",这里就填*.do)</url-pattern>
    </servlet-mapping>
</web-app>
```