package com.gustate.mcga.xposed.home.feature

import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class DrawerHook {

    companion object {
        const val DRAWER_TAG = "桌面抽屉"
    }

    /**
     * 隐藏抽屉应用名称
     */
    fun hideDrawerAppName(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        // 获取 ClassLoader 实例
        val classLoader = param.classLoader
        // 加载类
        // 加载 Adapter 类
        val baseAdapterClass = loadClass(
            name = "com.android.launcher3.allapps.BaseAllAppsAdapter",
            loader = classLoader
        )
        // 加载 BubbleTextView
        val bubbleTextViewClass = loadClass(
            name = "com.android.launcher3.BubbleTextView",
            loader = classLoader
        )
        // 找到内部类 ViewHolder
        val viewHolder = loadClass(
            name = $$"com.android.launcher3.allapps.BaseAllAppsAdapter$ViewHolder",
            loader = classLoader
        )
        // 匹配 onBindViewHolder 方法
        val onBindViewMethod = baseAdapterClass.getDeclaredMethod(
            "onBindViewHolder",
            viewHolder,
            Int::class.javaPrimitiveType
        ) ?: return log(
            module = module, tag = DRAWER_TAG,
            message = "❌ 未获取到 onBindViewHolder 函数"
        )
        // 执行 Hook
        module.hook(onBindViewMethod).intercept { chain ->
            val result = chain.proceed()
            // 获取第一个参数：ViewHolder
            val viewHolder = chain.args[0] ?: return@intercept result
            // 反射获取 itemView (ViewHolder 的公有字段)
            val itemView = viewHolder.javaClass.getField("itemView").get(viewHolder)
            // 如果是 BubbleTextView，直接执行操作
            if (bubbleTextViewClass.isInstance(itemView)) {
                itemView.apply {
                    callAnyMethod<Unit>(
                        name = "setText",
                        params = arrayOf(""),
                        classLoader = classLoader
                    )
                    callAnyMethod<Unit>(
                        name = "setMaxLines",
                        params = arrayOf(0),
                        classLoader = classLoader
                    )
                    callAnyMethod(
                        name = "setCompoundDrawablePadding",
                        params = arrayOf(0),
                        classLoader = classLoader
                    )
                }
                log(
                    module = module, tag = DRAWER_TAG,
                    message = "✅ 抽屉应用名隐藏成功"
                )
                result
            } else {
                log(
                    module = module, tag = DRAWER_TAG,
                    message = "❌ 隐藏抽屉应用名称失败"
                )
                result
            }
        }
    }
}