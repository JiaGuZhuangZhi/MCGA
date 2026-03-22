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
            className = "com.android.launcher3.allapps.BaseAllAppsAdapter",
            classLoader = classLoader
        )
        // 加载 BubbleTextView
        val bubbleTextViewClass = loadClass(
            className = "com.android.launcher3.BubbleTextView",
            classLoader = classLoader
        )
        // 找到内部类 ViewHolder
        val viewHolder = loadClass(
            className = "com.android.launcher3.allapps.BaseAllAppsAdapter\$ViewHolder",
            classLoader = classLoader
        )

        // 匹配 onBindViewHolder 方法
        val onBindViewMethod = baseAdapterClass?.getDeclaredMethod(
            "onBindViewHolder",
            viewHolder,
            Int::class.javaPrimitiveType
        ) ?: throw NullPointerException()
        // 执行 Hook
        module.hook(onBindViewMethod).intercept { chain ->
            val result = chain.proceed()
            // 获取第一个参数：ViewHolder
            val viewHolder = chain.args[0] ?: return@intercept result
            // 反射获取 itemView (ViewHolder 的公有字段)
            val itemView = viewHolder.javaClass.getField("itemView").get(viewHolder)
            // 如果是 BubbleTextView，直接执行操作
            if (bubbleTextViewClass?.isInstance(itemView) == true) {
                itemView.apply {
                    callAnyMethod<Unit>("setText", "")
                    callAnyMethod<Unit>("setMaxLines", 0)
                    callAnyMethod<Unit>("setCompoundDrawablePadding", 0)
                }
                result
            }
            log(
                module = module,
                tag = DRAWER_TAG,
                message = "✅ 抽屉应用名隐藏成功"
            )
        }

    }

}

/*object DrawerAppName {
    fun goneDrawerAppName(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val baseAdapterClass = XposedHelpers.findClass(
                "com.android.launcher3.allapps.BaseAllAppsAdapter",
                lpparam.classLoader
            )

            val bubbleTextViewClass = XposedHelpers.findClass(
                "com.android.launcher3.BubbleTextView",
                lpparam.classLoader
            )

            //Hook onBindViewHolder(ViewHolder, int)
            val onBindViewMethod = baseAdapterClass.getDeclaredMethod(
                "onBindViewHolder",
                baseAdapterClass.declaredClasses.first {
                    it.simpleName == "ViewHolder"
                },
                Int::class.javaPrimitiveType
            )

            XposedBridge.hookMethod(
                onBindViewMethod,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val viewHolder = param.args[0]
                        // val viewType = XposedHelpers.callMethod(viewHolder, "getItemViewType") as Int
                        // if (viewType != 2) return // 只处理应用图标
                        val itemView = XposedHelpers.getObjectField(viewHolder, "itemView")
                        if (bubbleTextViewClass.isInstance(itemView)) {
                            // 清除文本
                            XposedHelpers.callMethod(itemView, "setText", "")
                            // 移除文字高度
                            XposedHelpers.callMethod(itemView, "setMaxLines", 0)
                            // 可选：移除 drawable 和 text 之间的 padding
                            XposedHelpers.callMethod(itemView, "setCompoundDrawablePadding", 0)
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            XposedBridge.log("HideAppNameHook failed: ${e.message}")
            e.printStackTrace()
        }
    }
}*/