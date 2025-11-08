package com.gustate.mcga.xposed.search.feature

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.gustate.mcga.utils.ViewUtils.dpToPx
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.math.abs

object AppRecommend {
    /**
     * 隐藏应用推荐中应用名称
     * @param lpparam 进程信息
     */
    fun goneAdviceAppName(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            // NewRecommendAppAdapter$LocalAppViewHolder 类
            val localVHClass = XposedHelpers.findClass(
                "com.heytap.quicksearchbox.adapter.NewRecommendAppAdapter" +
                        "\$LocalAppViewHolder",
                lpparam.classLoader
            )
            // d 函数: void d(BaseAppInfo, int)
            val baseAppInfoClass = XposedHelpers.findClass(
                "com.heytap.quicksearchbox.core.db.entity.BaseAppInfo",
                lpparam.classLoader
            )
            // Hook d 函数
            XposedBridge.hookMethod(
                localVHClass.getDeclaredMethod(
                    "d",
                    baseAppInfoClass,
                    Int::class.javaPrimitiveType
                ), object : XC_MethodHook() {
                    @SuppressLint("DiscouragedApi")
                    override fun afterHookedMethod(param: MethodHookParam) {
                        // 获取 itemView
                        val holder = param.thisObject
                        val itemView = XposedHelpers
                            .getObjectField(holder, "itemView") as View
                        //val context = itemView.context
                        // 获取 app_name 资源 ID
                        val appNameId = itemView.resources.getIdentifier(
                            "app_name", "id", lpparam.packageName
                        )
                        val lastNameId = itemView.resources.getIdentifier(
                            "last_app_name", "id", lpparam.packageName
                        )
                        // GONE app_name
                        if (appNameId != 0 || lastNameId != 0) {
                            val appNameTv = itemView.findViewById<TextView>(appNameId)
                            val lastNameTv = itemView.findViewById<TextView>(lastNameId)
                            appNameTv?.visibility = View.GONE
                            lastNameTv?.visibility = View.GONE
                            XposedBridge.log("✅ 成功隐藏应用建议中应用的名称")
                            return
                        } else {
                            XposedBridge.log("❌ 获取到 app_name 控件的资源id为0")
                            return
                        }
                    }
                })
        } catch (e: Throwable) {
            XposedBridge.log("❌ Hook failed: ${e.message}")
            e.printStackTrace()
        }
    }
    fun fixAdviceAppCard(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val clazz = XposedHelpers.findClass(
                "com.heytap.quicksearchbox.ui.card.AppSuggestCardView",
                lpparam.classLoader
            )

            // Hook 构造函数：AppSuggestCardView(Context, AttributeSet)
            XposedBridge.hookMethod(
                clazz.getConstructor(Context::class.java, AttributeSet::class.java),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val instance = param.thisObject
                        val context = param.args[0] as Context
                        // 单行 84, 冗余 130-84 = 46
                        // 两行 168, 冗余 214-168 = 46
                        // 盲猜 46 是动画预留
                        // 原始值（用于识别字段）
                        val originalCollapsedPx = 130f.dpToPx(context).toInt()
                        val originalExpandedPx = 214f.dpToPx(context).toInt()
                        // 新高度
                        val newCollapsedPx = 110f.dpToPx(context).toInt()
                        val newExpandedPx = 174f.dpToPx(context).toInt()
                        // 查找所有 private final int 字段
                        val fields = instance.javaClass.declaredFields
                        val intFields = fields.filter { field ->
                            field.type == Int::class.javaPrimitiveType &&
                                    (field.modifiers and Modifier.PRIVATE) != 0 &&
                                    (field.modifiers and Modifier.FINAL) != 0
                        }
                        var collapsedField: Field? = null
                        var expandedField: Field? = null
                        for (field in intFields) {
                            field.isAccessible = true
                            val value = field.getInt(instance)
                            // 允许 ±2px 误差（因 float 转 int 四舍五入）
                            if (abs(value - originalCollapsedPx) <= 2) {
                                collapsedField = field
                            } else if (abs(value - originalExpandedPx) <= 2) {
                                expandedField = field
                            }
                        }

                        if (collapsedField != null && expandedField != null) {
                            collapsedField.setInt(instance, newCollapsedPx)
                            expandedField.setInt(instance, newExpandedPx)
                            XposedBridge.log("✅ 成功修改 AppSuggestCardView 高度: $newCollapsedPx / $newExpandedPx px")
                        } else {
                            XposedBridge.log("❌ 未找匹配到 130dp / 214dp 的字段（此字段在一加Ace5Pro上验证过（204版本））")
                            // 可选：fallback 到排序方式
                            val sortedFields = intFields.sortedBy { it.getInt(instance) }
                            if (sortedFields.size >= 2) {
                                sortedFields[0].setInt(instance, newCollapsedPx)
                                sortedFields[1].setInt(instance, newExpandedPx)
                                XposedBridge.log("⚠️ 使用 fallback 方式设置高度")
                            }
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            XposedBridge.log("❌ Hook AppSuggestCardView failed: ${e.message}")
            e.printStackTrace()
        }
    }
}