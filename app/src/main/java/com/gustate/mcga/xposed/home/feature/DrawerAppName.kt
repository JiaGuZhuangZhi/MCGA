package com.gustate.mcga.xposed.home.feature

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object DrawerAppName {
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
                    it.simpleName == "ViewHolder" },
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
}