package patrick.servlet.plus.annotation.life.clazz

/**
 * 表明方法在开始服务前执行, 被标记方法方法应该为public, 无参数, 无返回值
 */
@Retention(AnnotationRetention.RUNTIME)
@Target( AnnotationTarget.FUNCTION)
annotation class Init
