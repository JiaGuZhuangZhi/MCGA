package com.gustate.mcga.xposed.search.feature

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ResourceHelper
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.math.abs

/**
 * 全局搜索 - 应用建议/推荐功能拦截
 */
class AppRecommendHook {

    companion object {
        const val SEARCH_LOG = "全局搜索"
    }

    /**
     * 隐藏应用推荐中的应用名称
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    fun goneAdviceAppName(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        val adapterClass =
            loadClass("com.heytap.quicksearchbox.adapter.NewRecommendAppAdapter", param.classLoader)
                ?: return
        val viewHolderClass =
            loadClass("androidx.recyclerview.widget.RecyclerView\$ViewHolder", param.classLoader)
                ?: return

        try {
            val onBindViewHolder = adapterClass.getDeclaredMethod(
                "onBindViewHolder",
                viewHolderClass,
                Int::class.javaPrimitiveType
            )

            module.hook(onBindViewHolder).intercept { chain ->
                val result = chain.proceed()
                val viewHolder = chain.args[0]
                val itemView = viewHolder.getAnyField<View>("itemView") ?: return@intercept result

                // 使用 ResourceHelper 动态获取 ID，干掉 app_name 和 last_app_name
                val appNameId = ResourceHelper.getIdentifier(
                    param.classLoader,
                    param.packageName,
                    "id",
                    "app_name"
                )
                val lastNameId = ResourceHelper.getIdentifier(
                    param.classLoader,
                    param.packageName,
                    "id",
                    "last_app_name"
                )

                if (appNameId != 0 || lastNameId != 0) {
                    itemView.findViewById<TextView>(appNameId)?.visibility = View.GONE
                    itemView.findViewById<TextView>(lastNameId)?.visibility = View.GONE
                    log(
                        module = module, tag = SEARCH_LOG,
                        message = "✅ 成功隐藏应用建议中应用的名称"
                    )
                } else {
                    log(
                        module = module, tag = SEARCH_LOG,
                        message = "❌ 获取 app_name 资源 ID 失败 (ID 为 0)"
                    )
                }
                result
            }
        } catch (e: Throwable) {
            log(
                module = module, tag = SEARCH_LOG,
                message = "❌ Hook NewRecommendAppAdapter 失败 ${e.message}"
            )
        }
    }

    /**
     * 修正应用建议卡片的高度 (去冗余)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    fun fixAdviceAppCard(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        val cardViewClass =
            loadClass("com.heytap.quicksearchbox.ui.card.AppSuggestCardView", param.classLoader)
                ?: return

        try {
            // Hook 构造函数：AppSuggestCardView(Context, AttributeSet)
            val constructor =
                cardViewClass.getDeclaredConstructor(Context::class.java, AttributeSet::class.java)

            module.hook(constructor).intercept { chain ->
                val result = chain.proceed()
                val instance = chain.getThisObject()
                val context = chain.args[0] as Context

                // 原始值像素特征 (130dp / 214dp)
                val originalCollapsedPx = 130f.dpToPx(context).toInt()
                val originalExpandedPx = 214f.dpToPx(context).toInt()

                // 压缩后的新高度 (110dp / 174dp)
                val newCollapsedPx = 110f.dpToPx(context).toInt()
                val newExpandedPx = 174f.dpToPx(context).toInt()

                // 暴力扫描所有 private final int 字段来定位被混淆的高度变量
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
                    // 允许 ±2px 误差（四舍五入容错）
                    if (abs(value - originalCollapsedPx) <= 2) {
                        collapsedField = field
                    } else if (abs(value - originalExpandedPx) <= 2) {
                        expandedField = field
                    }
                }

                if (collapsedField != null && expandedField != null) {
                    collapsedField.setInt(instance, newCollapsedPx)
                    expandedField.setInt(instance, newExpandedPx)
                    log(
                        module = module, tag = SEARCH_LOG,
                        message = "✅ 成功修改 AppSuggestCardView 高度: $newCollapsedPx / $newExpandedPx px"
                    )
                } else {
                    log(
                        module = module, tag = SEARCH_LOG,
                        message = "⚠️ 未匹配到目标字段，尝试使用 fallback 排序注入"
                    )
                    val sortedFields = intFields.sortedBy { it.getInt(instance) }
                    if (sortedFields.size >= 2) {
                        sortedFields[0].setInt(instance, newCollapsedPx)
                        sortedFields[1].setInt(instance, newExpandedPx)
                    }
                }
                result
            }
        } catch (e: Throwable) {
            log(
                module = module, tag = SEARCH_LOG,
                message = "❌ Hook AppSuggestCardView 失败 ${e.message}"
            )
        }
    }
}
/*package com.gustate.mcga.xposed.search.feature

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.gustate.mcga.utils.LogUtils.log
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
            val adapterClass = XposedHelpers.findClass(
                "com.heytap.quicksearchbox.adapter.NewRecommendAppAdapter",
                lpparam.classLoader
            )

            // Hook onBindViewHolder(ViewHolder, int)
            val viewHolderClass = XposedHelpers.findClass(
                "androidx.recyclerview.widget.RecyclerView\$ViewHolder",
                lpparam.classLoader
            )

            val onBindViewHolderMethod = adapterClass.getDeclaredMethod(
                "onBindViewHolder",
                viewHolderClass,
                Int::class.javaPrimitiveType
            )
            // Hook onBindViewHolder
            XposedBridge.hookMethod(
                onBindViewHolderMethod, object : XC_MethodHook() {
                    @SuppressLint("DiscouragedApi")
                    override fun afterHookedMethod(param: MethodHookParam) {
                        // 获取 itemView
                        val viewHolder = param.args[0]
                        val itemView = XposedHelpers
                            .getObjectField(viewHolder, "itemView") as View
                        // 获取 app_name 资源 ID
                        val appNameId = itemView.resources.getIdentifier(
                            "app_name",
                            "id",
                            lpparam.packageName
                        )
                        val lastNameId = itemView.resources.getIdentifier(
                            "last_app_name",
                            "id",
                            lpparam.packageName
                        )
                        // GONE app_name
                        if (appNameId != 0 || lastNameId != 0) {
                            val appNameTv = itemView.findViewById<TextView>(appNameId)
                            val lastNameTv = itemView.findViewById<TextView>(lastNameId)
                            appNameTv?.visibility = View.GONE
                            lastNameTv?.visibility = View.GONE
                            log(tag = "全局搜索", message = "✅ 成功隐藏应用建议中应用的名称")
                        } else {
                            log(tag = "全局搜索", message = "❌ 获取到 app_name 控件的 id 为 0")
                        }
                    }
                })
        } catch (e: Throwable) {
            log(tag = "全局搜索", message = "❌ Hook failed", throwable = e)
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
                clazz.getConstructor(
                    Context::class.java,
                    AttributeSet::class.java
                ),
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
                            log(
                                tag = "全局搜索",
                                message ="✅ 成功修改 AppSuggestCardView 高度: " +
                                        "$newCollapsedPx / $newExpandedPx px"
                            )
                        } else {
                            log(
                                tag = "全局搜索",
                                message = "❌ 未找匹配到 130dp / 214dp 的字段" +
                                        "（此字段在一加Ace5Pro上验证过（204版本））"
                            )
                            // 可选：fallback 到排序方式
                            val sortedFields = intFields.sortedBy { it.getInt(instance) }
                            if (sortedFields.size >= 2) {
                                sortedFields[0].setInt(instance, newCollapsedPx)
                                sortedFields[1].setInt(instance, newExpandedPx)
                                log(
                                    tag = "全局搜索",
                                    message = "⚠️ 使用 fallback 方式设置高度"
                                )
                            }
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            log(tag = "全局搜索", message = "❌ Hook AppSuggestCardView failed", throwable = e)
            e.printStackTrace()
        }
    }
}*/