/**
 * MCGA (Make Color Great Again) - A Free and Open-Source Xposed Module for ColorOS Users
 *
 * Copyright (C) 2026 Zhuangzhi Meng (Gustate XiaoMeng)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gustate.mcga.xposed.helper

import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

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
     * @param name 字段名称
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>?.getStaticField(name: String): T {
        val clazz = this ?: throw NullPointerException("❌ 获取 $name 字段失败, 所在类不存在")
        return runCatching {
            // 直接从这个类里找字段
            val field = clazz.getDeclaredField(name)
            // 忽略 private 等安全检查
            field.isAccessible = true
            // 静态字段传 null 就能拿
            field.get(null) as T?
        }.getOrNull() ?: throw NullPointerException("❌ 获取 $name 字段失败, 字段不存在")
    }

    /**
     * 取方法 (可取当前类及其所有父类的私有/公开方法)
     * 自动处理类加载
     * @param name 方法名
     * @param paramTypes 参数类型
     * @param classLoader [ClassLoader] 实例
     * @param searchSuper 是否向父类寻找
     * @return Method 方法
     */
    fun Any?.getAnyMethod(
        name: String,
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader,
        searchSuper: Boolean = true
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
            // 不允许查找父类, 当前类找不到就报错
            if (!searchSuper)
                throw NoSuchMethodException("❌ 获取 $name 方法失败, 当前类不存在该方法")
            // 父类找不到该方法报错
            clazz = clazz.superclass
                ?: throw NoSuchMethodException("❌ 获取 $name 方法失败, 方法不存在")
        }
    }

    /**
     * 取方法 (可取当前类及其所有父类的私有/公开方法)
     * 自动处理类加载
     * @param name 方法名
     * @param classLoader 类加载器
     * @param paramTypes 参数类型
     * @param searchSuper 是否向父类查找
     * @return Method 方法
     */
    fun getAnyMethod(
        name: String,
        className: String,
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader,
        searchSuper: Boolean = true,
    ): Method {
        return loadClass(
            name = className,
            loader = classLoader
        ).getAnyMethod(
            name = name,
            paramTypes = paramTypes,
            classLoader = classLoader,
            searchSuper = searchSuper,
        )
    }

    /**
     * 取方法并创建 Hook
     * 可 Hook 当前类及所有父类的私有/公开方法
     * @param module Xposed 模块实例
     * @param name 方法名
     * @param classLoader 类加载器
     * @param paramTypes 参数类型
     * @param searchSuper 是否向父类查找
     * @return [XposedInterface.HookBuilder]
     */
    fun Any?.getAndHookMethod(
        module: XposedInterface,
        name: String,
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader,
        searchSuper: Boolean = true,
    ): XposedInterface.HookBuilder {
        return module.hook(
            this.getAnyMethod(
                name = name,
                paramTypes = paramTypes,
                classLoader = classLoader,
                searchSuper = searchSuper,
            )
        )
    }

    /**
     * 取方法并创建 Hook (含类名参数)
     * 可 Hook 当前类及所有父类的私有/公开方法
     * @param module Xposed 模块实例
     * @param name 方法名
     * @param classLoader 类加载器
     * @param paramTypes 参数类型
     * @param searchSuper 是否向父类查找
     * @return [XposedInterface.HookBuilder]
     */
    fun getAndHookMethod(
        module: XposedInterface,
        name: String,
        className: String,
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader,
        searchSuper: Boolean = true,
    ): XposedInterface.HookBuilder {
        return module.hook(
            loadClass(
                name = className,
                loader = classLoader
            ).getAnyMethod(
                name = name,
                paramTypes = paramTypes,
                classLoader = classLoader,
                searchSuper = searchSuper,
            )
        )
    }

    /**
     * 调用实例方法 (可显式指定参数与返回类型)
     * @param name 方法名
     * @param params 实际传入的参数值
     * @param paramTypes 显式指定的方法参数签名 Class 数组
     * @param classLoader [ClassLoader] 实例
     * @param searchSuper 是否向父类寻找
     * @return T 指定的返回值类型
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.callAnyMethod(
        name: String,
        params: Array<Any?> = emptyArray(),
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader,
        searchSuper: Boolean = true
    ): T {
        val method = this.getAnyMethod(
            name = name,
            paramTypes = paramTypes,
            classLoader = classLoader,
            searchSuper = searchSuper
        )
        if (Modifier.isStatic(method.modifiers)) {
            throw IllegalArgumentException(
                "❌ $name 是静态方法，请使用 callStaticMethod()"
            )
        }
        method.isAccessible = true
        return method.invoke(
            this,
            *params
        ) as T
    }

    /**
     * 调用静态方法 (可显式指定参数与返回类型)
     * @param name 方法名
     * @param params 实际传入的参数值
     * @param paramTypes 显式指定的方法参数签名 Class 数组
     * @param classLoader [ClassLoader] 实例
     * @param searchSuper 是否向父类寻找
     * @return T 指定的返回值类型
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Class<*>?.callStaticMethod(
        name: String,
        params: Array<Any?> = emptyArray(),
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader,
        searchSuper: Boolean = true
    ): T {
        val method = this.getAnyMethod(
            name = name,
            paramTypes = paramTypes,
            classLoader = classLoader,
            searchSuper = searchSuper
        )
        if (!Modifier.isStatic(method.modifiers)) {
            throw IllegalArgumentException(
                "❌ $name 是实例方法，请使用 callAnyMethod()"
            )
        }
        method.isAccessible = true
        return method.invoke(
            null,
            *params
        ) as T
    }

    /**
     * 调用静态方法 (可显式指定参数与返回类型)
     * @param name 方法名
     * @param className 所属类名
     * @param classLoader [ClassLoader] 实例
     * @param params 实际传入的参数值
     * @param paramTypes 显式指定的方法参数签名 Class 数组
     * @param searchSuper 是否向父类寻找
     * @return T 指定的返回值类型
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> callStaticMethod(
        name: String,
        className: String,
        classLoader: ClassLoader,
        params: Array<Any?> = emptyArray(),
        paramTypes: Array<Any?> = emptyArray(),
        searchSuper: Boolean = true
    ): T {
        return loadClass(
            name = className,
            loader = classLoader
        ).callStaticMethod(
            name = name,
            params = params,
            paramTypes = paramTypes,
            classLoader = classLoader,
            searchSuper = searchSuper
        )
    }

    /**
     * 取构造函数 (只取当前类声明的, 构造函数不会继承所以不向父类找)
     * 自动处理类加载
     * @param paramTypes 参数类型
     * @param classLoader [ClassLoader] 实例
     * @return Constructor 构造函数
     */
    fun Any?.getAnyConstructor(
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader
    ): Constructor<*> {
        val clazz: Class<*> = this as? Class<*>
            ?: (this?.javaClass ?: throw NullPointerException(
                "❌ 获取构造函数失败, 所在类不存在"
            ))
        val realParamTypes = paramTypes
            .toParamTypes(
                classLoader = classLoader
            )
        return try {
            clazz.getDeclaredConstructor(*realParamTypes)
        } catch (e: NoSuchMethodException) {
            throw NoSuchMethodException(
                "❌ 获取 ${clazz.name} 构造函数失败, 构造函数不存在"
            )
        }
    }

    /**
     * 取构造函数 (含类名参数)
     * @param className 完整类名 (包名.类名)
     * @param paramTypes 参数类型
     * @param classLoader [ClassLoader] 实例
     * @return Constructor 构造函数
     */
    fun getAnyConstructor(
        className: String,
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader
    ): Constructor<*> {
        return loadClass(
            name = className,
            loader = classLoader
        ).getAnyConstructor(
            paramTypes = paramTypes,
            classLoader = classLoader
        )
    }

    /**
     * 调用构造函数创建实例 (可显式指定参数签名)
     * @param params 实际传入的参数值
     * @param paramTypes 显式指定的构造函数参数签名
     * @param classLoader [ClassLoader] 实例
     * @return T 创建出来的对象
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> Any?.callConstructor(
        params: Array<Any?> = emptyArray(),
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader
    ): T {
        val constructor = this.getAnyConstructor(
            paramTypes = paramTypes,
            classLoader = classLoader
        )
        // 忽略 private 等安全检查
        constructor.isAccessible = true
        return try {
            constructor.newInstance(*params) as T
        } catch (e: InvocationTargetException) {
            // 解包, 直接抛出构造函数内部真正的异常
            throw e.targetException ?: e
        }
    }

    /**
     * 调用构造函数创建实例 (含类名参数)
     * @param className 完整类名 (包名.类名)
     * @param params 实际传入的参数值
     * @param paramTypes 显式指定的构造函数参数签名
     * @param classLoader [ClassLoader] 实例
     * @return T 创建出来的对象
     */
    fun <T> callConstructor(
        className: String,
        params: Array<Any?> = emptyArray(),
        paramTypes: Array<Any?> = emptyArray(),
        classLoader: ClassLoader
    ): T {
        return loadClass(
            name = className,
            loader = classLoader
        ).callConstructor(
            params = params,
            paramTypes = paramTypes,
            classLoader = classLoader
        )
    }

    /**
     * 设置实例(具体对象)的字段值
     * @param name 字段名称
     * @param value 要设置的值
     */
    fun Any?.setAnyField(name: String, value: Any?) {
        var clazz: Class<*> = this?.javaClass
            ?: throw NullPointerException("❌ 设置 $name 字段失败, 所在类不存在")
        while (true) {
            val field = runCatching {
                clazz.getDeclaredField(name)
            }.getOrNull()
            if (field != null) {
                field.isAccessible = true
                field.set(this, value)
                return
            }
            clazz = clazz.superclass
                ?: throw NullPointerException("❌ 设置 $name 字段失败, 字段不存在")
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
     * 参数类型转换
     * 支持: Class<*>、KClass<*>、String 自动 loadClass() 和 null
     * @param classLoader [ClassLoader] 实例
     * @return Class Array
     */
    private fun Array<Any?>?.toParamTypes(
        classLoader: ClassLoader
    ): Array<Class<*>?> {
        if (this == null) return emptyArray()
        return map { type ->
            when (type) {
                null -> null
                is Class<*> -> type
                is KClass<*> -> type.java
                is String -> loadClass(name = type, loader = classLoader)
                else -> throw IllegalArgumentException(
                    "❌ 不支持的参数类型：${type::class.qualifiedName}"
                )
            }
        }.toTypedArray()
    }
}