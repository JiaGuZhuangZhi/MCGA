package com.gustate.mcga.xposed.helper

import io.github.libxposed.api.XposedInterface

object MethodHelper {

    /**
     * 修改 Chain 中的指定参数后继续执行拦截器链
     * 这是 [XposedInterface.Chain] 的扩展函数，可在 intercept 闭包中直接调用
     * 通过修改调用参数的值，实现对原始方法参数的动态替换
     * @receiver [XposedInterface.Chain] 拦截器链对象，提供 proceed 和 args 等核心能力
     * @param index 要修改的参数索引位置（从 0 开始）
     * @param value 新的参数值，类型需与原方法参数兼容
     * @return 原方法或后续拦截器的执行结果，void 方法返回 null
     * @throws IndexOutOfBoundsException 当 index 超出参数范围时
     * @throws Throwable 由 proceed() 传播的异常
     * @see XposedInterface.Chain.proceed
     * @see XposedInterface.Chain.getArgs
     */
    fun XposedInterface.Chain.proceedWithParam(
        index: Int,
        value: Any?
    ): Any? {
        // 取出 final 参数
        val roParams = args
        // 转换为可读写列表并修改参数
        val rwParams = roParams.toMutableList()
        rwParams[index] = value

        // 转换为可读写列表并修改圆角
        return proceed(rwParams.toTypedArray())
    }

}