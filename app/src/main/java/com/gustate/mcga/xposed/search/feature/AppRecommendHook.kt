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
        val adapterClass = loadClass(
            className = "com.heytap.quicksearchbox.adapter.NewRecommendAppAdapter",
            classLoader = param.classLoader
        ) ?: return log(
            module = module, tag = SEARCH_LOG,
            message = "❌ 未获取到 NewRecommendAppAdapter 类"
        )
        val viewHolderClass = loadClass(
            className = "androidx.recyclerview.widget.RecyclerView\$ViewHolder",
            classLoader = param.classLoader
        ) ?: return log(
            module = module, tag = SEARCH_LOG,
            message = "❌ 未获取到 RecyclerView\$ViewHolder 类"
        )
        try {
            val onBindViewHolder = adapterClass.getDeclaredMethod(
                "onBindViewHolder",
                viewHolderClass,
                Int::class.javaPrimitiveType
            ) ?: return log(
                module = module, tag = SEARCH_LOG,
                message = "❌ 未获取到 onBindViewHolder 函数"
            )
            module.hook(onBindViewHolder).intercept { chain ->
                val result = chain.proceed()
                val viewHolder = chain.args[0]
                val itemView = viewHolder
                    .getAnyField<View>(fieldName = "itemView")
                    ?: run {
                        log(
                            module = module, tag = SEARCH_LOG,
                            message = "❌ 获取 NewRecommendAppAdapter 中 ViewHolder 的 itemView 失败"
                        )
                        return@intercept result
                    }
                val res = itemView.resources
                val names = listOf("app_name", "last_app_name")
                ResourceHelper.getIdentifier(
                    res = res,
                    pkgName = param.packageName,
                    resType = "id",
                    resNames = names,
                    onReadyResIds = { resIds ->
                        // 分类正确与错误的 ids
                        val (valid, invalid) = resIds.entries.partition { it.value != 0 }
                        // 错误报日志
                        invalid.forEach {
                            log(
                                module = module, tag = SEARCH_LOG,
                                message = "❌ 获取 ${it.key} 资源 ID 失败 (ID 为 0)"
                            )
                        }
                        // 正确走流程
                        valid.forEach {
                            itemView.findViewById<TextView>(it.value)?.visibility = View.GONE
                        }
                        log(
                            module = module, tag = SEARCH_LOG,
                            message = "✅ 成功隐藏应用建议中应用的名称"
                        )
                    }
                )
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
        val cardViewClass = loadClass(
            className = "com.heytap.quicksearchbox.ui.card.AppSuggestCardView",
            classLoader = param.classLoader
        ) ?: return log(
            module = module, tag = SEARCH_LOG,
            message = "❌ 未获取到 AppSuggestCardView 类"
        )

        try {
            // Hook 构造函数：AppSuggestCardView(Context, AttributeSet)
            val constructor = cardViewClass.getDeclaredConstructor(
                Context::class.java,
                AttributeSet::class.java
            ) ?: return log(
                module = module, tag = SEARCH_LOG,
                message = "❌ 未获取到 AppSuggestCardView 构造函数"
            )
            module.hook(constructor).intercept { chain ->
                val result = chain.proceed()
                val instance = chain.thisObject
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
                        message = "✅ 成功修改 AppSuggestCardView 高度: " +
                                "$newCollapsedPx / $newExpandedPx px"
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