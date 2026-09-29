package com.gustate.mcga.xposed.helper

import java.lang.reflect.Method

@Suppress("unused")
object ClassHelper {

    /**
     * 安全加载类
     * @param name 具体类名
     * @param loader [ClassLoader] 实例
     * @return 加载的 Class 类
     */
    fun loadClass(
        name: String,
        loader: ClassLoader,
    ): Class<*> = runCatching {
        loader.loadClass(name)
    }.getOrNull()
        ?: throw NullPointerException("❌ 未找到 $name 类")

    /**
     * 取实例(具体对象)的字段
     * @param name 字段名称
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.getAnyField(name: String): T {
        var clazz: Class<*> = this?.javaClass
            ?: throw NullPointerException("❌ 获取 $name 字段失败, 所在类不存在")
        // 往父类找一找
        while (true) {
            // 从对象的类里找字段的位置
            val field = runCatching {
                clazz.getDeclaredField(name)
            }.getOrNull()
            if (field != null) {
                // 忽略 private 等安全检查
                field.isAccessible = true
                // 从对象中拿走字段
                return field.get(this) as T
            }
            // 往父类找, 父类不存在直接抛异常
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 获取 $name 字段失败, 字段不存在")
        }
    }

    /**
     * 取静态类的字段
     * @param fieldName 字段名称
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>?.getStaticField(fieldName: String): T {
        val clazz = this ?: throw NullPointerException("❌ 获取 $fieldName 字段失败, 所在类不存在")
        return runCatching {
            // 直接从这个类里找字段
            val field = clazz.getDeclaredField(fieldName)
            // 忽略 private 等安全检查
            field.isAccessible = true
            // 静态字段传 null 就能拿
            field.get(null) as T?
        }.getOrNull() ?: throw NullPointerException("❌ 获取 $fieldName 字段失败, 字段不存在")
    }

    /**
     * 取方法 (可取当前类及所有父类的私有/公开方法)
     * 需自行处理类加载
     * @param methodName 方法名
     * @param parameterTypes 参数类型
     */
    fun Any?.getAnyMethod(
        methodName: String,
        parameterTypes: Array<Class<*>?> = emptyArray()
    ): Method {
        var clazz: Class<*> = this as? Class<*>
            ?: (this?.javaClass
                ?: throw NullPointerException("❌ 获取 $methodName 方法失败, 所在类不存在"))
        while (true) {
            val method = runCatching {
                clazz.getDeclaredMethod(
                    methodName,
                    *parameterTypes
                )
            }.getOrNull()
            if (method != null) return method
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 获取 $methodName 方法失败, 方法不存在")
        }
    }

    /**
     * 取方法 (可取当前类及所有父类的私有/公开方法)
     * 自动处理类加载
     * @param name 方法名
     * @param classLoader [ClassLoader] 实例
     * @param paramTypes 参数类型
     */
    fun Any?.getAnyMethod(
        name: String,
        classLoader: ClassLoader,
        paramTypes: Array<Any?> = emptyArray()
    ): Method {
        var clazz: Class<*> = this as? Class<*>
            ?: (this?.javaClass ?: throw NullPointerException(
                "❌ 获取 $name 方法失败, 所在类不存在"
            ))
        val realParamTypes = paramTypes
            .toParamTypes(
                classLoader = classLoader
            )
        while (true) {
            val method = runCatching {
                clazz.getDeclaredMethod(
                    name,
                    *realParamTypes
                )
            }.getOrNull()
            if (method != null) return method
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 获取 $name 方法失败, 方法不存在")
        }
    }

    /**
     * 调用私有方法
     * @param methodName 方法名
     * @param args 参数
     */
    @Deprecated("喵呜")
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.callAnyMethod(methodName: String, vararg args: Any?): T {
        var clazz: Class<*> = this?.javaClass
            ?: throw NullPointerException("❌ 调用 $methodName 方法失败, 所在类不存在")
        while (true) {
            val method = runCatching {
                clazz.getDeclaredMethod(
                    methodName,
                    *args.map {
                        it?.javaClass
                    }.toTypedArray()
                )
            }.getOrNull()
            if (method != null) {
                method.isAccessible = true
                return method.invoke(this, *args) as T
            }
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 调用 $methodName 方法失败, 方法不存在")
        }
    }

    /**
     * 调用私有方法 (显式指定参数类型)
     * @param methodName 方法名
     * @param paramTypes 显式指定的方法参数签名 Class 数组（必须与源码完全一致）
     * @param args 实际传入的参数值
     */
    @Deprecated("喵呜")
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.callAnyMethod(
        methodName: String,
        paramTypes: Array<Class<out Any>?>,
        vararg args: Any?
    ): T {
        var clazz: Class<*> = this?.javaClass
            ?: throw NullPointerException("❌ 调用 $methodName 方法失败, 所在类不存在")
        while (true) {
            val method = runCatching {
                clazz.getDeclaredMethod(
                    methodName,
                    *paramTypes
                )
            }.getOrNull()
            if (method != null) {
                method.isAccessible = true
                return method.invoke(this, *args) as T
            }
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 调用 $methodName 方法失败, 方法不存在")
        }
    }

    /**
     * 调用静态方法 (可显式指定参数与返回类型)
     * @param name 方法名
     * @param params 实际传入的参数值
     * @param paramsType 显式指定的方法参数签名 Class 数组（必须与源码完全一致）
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.callAnyMethod(
        name: String,
        params: Array<Any?>? = null,
        paramsType: Array<Class<*>?>? = null
    ): T {
        var clazz: Class<*> = this?.javaClass
            ?: throw NullPointerException("❌ 调用 $name 方法失败, 所在类不存在")
        while (true) {
            val method = runCatching {
                if (paramsType == null) clazz.getDeclaredMethod(name)
                else clazz.getDeclaredMethod(name, *paramsType)
            }.getOrNull()
            if (method != null) {
                method.isAccessible = true
                val invoker =
                    if (params == null) method.invoke(this)
                    else method.invoke(this, *params)
                return invoker as T
            }
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 调用 $name 方法失败, 方法不存在")
        }
    }

    /**
     * 设置实例(具体对象)的字段值
     * @param fieldName 字段名称
     * @param value 要设置的值
     */
    fun Any?.setAnyField(fieldName: String, value: Any?) {
        var clazz: Class<*> = this?.javaClass
            ?: throw NullPointerException("❌ 设置 $fieldName 字段失败, 所在类不存在")
        while (true) {
            val field = runCatching {
                clazz.getDeclaredField(fieldName)
            }.getOrNull()
            if (field != null) {
                field.isAccessible = true
                field.set(this, value)
                return
            }
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 设置 $fieldName 字段失败, 字段不存在")
        }
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
     * @param name 方法名
     * @param params 参数
     * @param paramsType 参数类型
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>?.callStaticMethod(
        name: String,
        params: Array<Any?>? = null,
        paramsType: Array<Class<*>?>? = null
    ): T {
        var clazz = this
            ?: throw NullPointerException("❌ 调用 $name 静态方法失败, 类不存在")
        while (true) {
            val method = runCatching {
                if (paramsType == null) clazz.getDeclaredMethod(name)
                else clazz.getDeclaredMethod(name, *paramsType)
            }.getOrNull()
            if (method != null) {
                method.isAccessible = true
                val invoker =
                    if (params == null) method.invoke(null)
                    else method.invoke(null, *params)
                return invoker as T
            }
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 调用 $name 静态方法失败, 方法不存在")
        }
    }

    /**
     * 参数类型转换
     * @param classLoader [ClassLoader] 实例
     * 支持：
     * - Class<*>
     * - KClass<*>
     * - String（自动 loadClass()）
     * - null
     */
    private fun Array<Any?>?.toParamTypes(
        classLoader: ClassLoader
    ): Array<Class<*>?> {
        if (this == null) return emptyArray()

        return map { type ->
            when (type) {
                null -> null
                is Class<*> -> type
                is kotlin.reflect.KClass<*> -> type.java
                is String -> loadClass(name = type, loader = classLoader)
                else -> throw IllegalArgumentException(
                    "❌ 不支持的参数类型：${type::class.qualifiedName}"
                )
            }
        }.toTypedArray()
    }
}