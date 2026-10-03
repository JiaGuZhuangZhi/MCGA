package com.gustate.mcga.xposed.systemui.feature.qs

import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 平滑圆角 (SmoothRound) 相关功能拦截
 */
class SmoothRoundHook {

    companion object {
        private const val TAG = "系统界面磁铁平滑圆角"
    }

    fun hookSmoothRoundSize(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
    ) {
        /*try {
            val classLoader = param.classLoader
            val oplusQsSmoothRoundUtilClass = loadClass(
                name = "com.oplusos.systemui.common.util." +
                        "OplusQsSmoothRoundUtil",
                loader = classLoader
            )
            val getRoundParamsMethod = oplusQsSmoothRoundUtilClass
                .getAnyMethod(
                    name = "getRoundParams",
                    paramTypes = arrayOf(
                        Context::class.java,
                        Float::class.javaPrimitiveType
                    ),
                    classLoader = classLoader
                )
            module.hook(
                getRoundParamsMethod
            ).intercept { chain ->
                val radius = chain.args[1] ?: 0f
                val result = chain.proceed()
                result.callAnyMethod<Any>(
                    name = "setRadius",
                    params = arrayOf(radius),
                    paramsType = arrayOf(
                        Float::class.javaPrimitiveType
                    )
                )
                return@intercept result
            }

            log(
                module = module, tag = TAG,
                message = "✅ 平滑圆角大小限制解除成功"
            )
        } catch (e: Exception) {
            log(
                module = module, tag = TAG,
                message = "❌ 平滑圆角大小限制解除失败",
                throwable = e
            )
        }*/
    }

}