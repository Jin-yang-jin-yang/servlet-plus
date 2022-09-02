package patrick.servlet.plus.annotation

/**
 * 标记此类为被分发类, 只有此注解存在时, 工具包才会扫描其中的方法
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Plus
