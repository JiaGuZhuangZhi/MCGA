package com.gustate.mcga.xposed.helper

import java.lang.reflect.Method

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
    fun <T> Any?.getAnyField(fieldName: String): T {
        var clazz: Class<*> = this?.javaClass
            ?: throw NullPointerException("❌ 获取 $fieldName 字段失败, 所在类不存在")
        // 往父类找一找
        while (true) {
            // 从对象的类里找字段的位置
            val field = runCatching {
                clazz.getDeclaredField(fieldName)
            }.getOrNull()
            if (field != null) {
                // 忽略 private 等安全检查
                field.isAccessible = true
                // 从对象中拿走字段
                return field.get(this) as T
            }
            // 往父类找, 父类不存在直接抛异常
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 获取 $fieldName 字段失败, 字段不存在")
        }
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
     * 取方法 (可取当前类及所有父类的私有/公开方法)
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
     * 调用私有方法
     * @param methodName 方法名
     * @param args 参数
     */
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