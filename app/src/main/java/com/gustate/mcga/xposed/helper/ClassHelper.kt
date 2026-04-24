package com.gustate.mcga.xposed.helper

object ClassHelper {

    /**
     * 安全加载类
     * @param className 具体类名
     * @param classLoader [ClassLoader] 实例
     */
    fun loadClass(
        className: String,
        classLoader: ClassLoader,
    ): Class<*> = runCatching {
        classLoader.loadClass(className)
    }.getOrNull()
        ?: throw NullPointerException("❌ 未找到 $className 类")

    /**
     * 取实例(具体对象)的字段
     * @param fieldName 字段名称
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.getAnyField(fieldName: String): T? {
        val target = this ?: return null
        return runCatching {
            var clazz: Class<*>? = target.javaClass
            // 往父类找一找
            while (clazz != null) {
                // 从对象的类里找字段的位置
                val field = runCatching {
                    clazz.getDeclaredField(fieldName)
                }.getOrNull()
                if (field != null) {
                    // 忽略 private 等安全检查
                    field.isAccessible = true
                    // 从对象中拿走字段
                    return@runCatching field.get(target) as T?
                }
                clazz = clazz.superclass  // 往父类走
            }
            null
        }.getOrNull()
    }

    /**
     * 取静态类的字段
     * @param fieldName 字段名称
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>?.getStaticField(fieldName: String): T? {
        val clazz = this ?: return null
        return runCatching {
            // 直接从这个类里找字段
            val field = clazz.getDeclaredField(fieldName)
            // 忽略 private 等安全检查
            field.isAccessible = true
            // 静态字段传 null 就能拿
            field.get(null) as T?
        }.getOrNull()
    }

    /**
     * 调用私有方法
     * @param methodName 方法名
     * @param args 参数
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.callAnyMethod(methodName: String, vararg args: Any?): T? {
        val target = this ?: return null
        return runCatching {
            // 这里我们简单处理，假设参数类型匹配，或者拿所有方法过滤
            val method = target.javaClass.declaredMethods.firstOrNull { it.name == methodName }
            method?.isAccessible = true
            method?.invoke(target, *args) as T?
        }.getOrNull()
    }

    /**
     * 设置实例(具体对象)的字段值
     * @param fieldName 字段名称
     * @param value 要设置的值
     */
    fun Any?.setAnyField(fieldName: String, value: Any?): Boolean {
        val target = this ?: return false
        return runCatching {
            val field = target.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(target, value)
            true
        }.getOrDefault(false)
    }

    /**
     * 设置静态类的字段值
     * @param fieldName 字段名称
     * @param value 要设置的值
     */
    fun Class<*>?.setStaticField(fieldName: String, value: Any?): Boolean {
        val clazz = this ?: return false
        return runCatching {
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(null, value)
            true
        }.getOrDefault(false)
    }

    /**
     * 调用静态方法
     * @param methodName 方法名
     * @param args 参数
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>?.callStaticMethod(methodName: String, vararg args: Any?): T? {
        val target = this ?: return null
        return runCatching {
            val method = target.getDeclaredMethod(methodName)
            method.isAccessible = true
            method.invoke(target, *args) as T?
        }.getOrNull()
    }

}