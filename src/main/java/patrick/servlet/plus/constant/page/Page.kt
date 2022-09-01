package patrick.servlet.plus.auto.servlet.patrick.servlet.plus.constant

fun getPage500(msg: String) =
    """
        <!doctype html>
        <html lang="zh">
        <head><title>HTTP状态 500 - 你服务器炸了</title>
        <meta http-equiv='content-type' content='text/html;charset=UTF-8'>
            <style type="text/css">body {
                font-family: Tahoma, Arial, sans-serif;
            }
        
            h1, h2, h3, b {
                color: white;
                background-color: #525D76;
            }
        
            h1 {
                font-size: 22px;
            }
        
            h2 {
                font-size: 16px;
            }
        
            h3 {
                font-size: 14px;
            }
        
            p {
                font-size: 12px;
            }
        
            a {
                color: black;
            }
        
            .line {
                height: 1px;
                background-color: #525D76;
                border: none;
            }</style>
        </head>
        <body><h1>HTTP状态 500 - 你服务器炸了</h1>
        <hr class="line"/>
        <p><b>类型</b> 状态报告</p>
        <p><b>描述</b> ${msg}</p>
        <hr class="line"/>
        <h3>Apache Tomcat/10.0.23</h3></body>
        </html>
    """.trimIndent()

fun getPage404() =
    """
        <!doctype html>
        <html lang="zh">
        <head><title>HTTP状态 404 - 未找到</title>
        <meta http-equiv='content-type' content='text/html;charset=UTF-8'>
            <style type="text/css">body {
                font-family: Tahoma, Arial, sans-serif;
            }

            h1, h2, h3, b {
                color: white;
                background-color: #525D76;
            }

            h1 {
                font-size: 22px;
            }

            h2 {
                font-size: 16px;
            }

            h3 {
                font-size: 14px;
            }

            p {
                font-size: 12px;
            }

            a {
                color: black;
            }

            .line {
                height: 1px;
                background-color: #525D76;
                border: none;
            }</style>
        </head>
        <body><h1>HTTP状态 404 - 未找到</h1>
        <hr class="line"/>
        <p><b>类型</b> 状态报告</p>
        <p><b>描述</b> 源服务器未能找到目标资源的表示或者是不愿公开一个已经存在的资源表示。</p>
        <hr class="line"/>
        <h3>Apache Tomcat/10.0.23</h3></body>
        </html>
    """.trimIndent()

fun getPage405() =
    """
        <!doctype html>
        <html lang="zh">
        <head><title>HTTP状态 405 - 此方法不受支持</title>
        <meta http-equiv='content-type' content='text/html;charset=UTF-8'>
            <style type="text/css">body {
                font-family: Tahoma, Arial, sans-serif;
            }

            h1, h2, h3, b {
                color: white;
                background-color: #525D76;
            }

            h1 {
                font-size: 22px;
            }

            h2 {
                font-size: 16px;
            }

            h3 {
                font-size: 14px;
            }

            p {
                font-size: 12px;
            }

            a {
                color: black;
            }

            .line {
                height: 1px;
                background-color: #525D76;
                border: none;
            }</style>
        </head>
        <body><h1>HTTP状态 405 - 此方法不受支持</h1>
        <hr class="line"/>
        <p><b>类型</b> 状态报告</p>
        <p><b>描述</b> 此方法不受支持</p>
        <hr class="line"/>
        <h3>Apache Tomcat/10.0.23</h3></body>
        </html>
    """.trimIndent()

fun getPageXXX() =
    """
        <!doctype html>
        <html lang="zh">
        <head><title>HTTP状态 xxx - 反正就是出错了,懒得查HTTP状态码了</title>
        <meta http-equiv='content-type' content='text/html;charset=UTF-8'>
            <style type="text/css">body {
                font-family: Tahoma, Arial, sans-serif;
            }

            h1, h2, h3, b {
                color: white;
                background-color: #525D76;
            }

            h1 {
                font-size: 22px;
            }

            h2 {
                font-size: 16px;
            }

            h3 {
                font-size: 14px;
            }

            p {
                font-size: 12px;
            }

            a {
                color: black;
            }

            .line {
                height: 1px;
                background-color: #525D76;
                border: none;
            }</style>
        </head>
        <body><h1>HTTP状态 xxx - 反正就是出错了,懒得查HTTP状态码了</h1>
        <hr class="line"/>
        <p><b>类型</b> 状态报告</p>
        <p><b>描述</b> 出错了 </p>
        <hr class="line"/>
        <h3>Apache Tomcat/10.0.23</h3></body>
        </html>
    """.trimIndent()