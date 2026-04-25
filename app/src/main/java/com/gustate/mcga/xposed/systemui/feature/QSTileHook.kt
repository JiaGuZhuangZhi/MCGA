package com.gustate.mcga.xposed.systemui.feature

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.RootUtils
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.systemui.feature.qs.tile.MediaTileHook
import com.gustate.mcga.xposed.systemui.feature.qs.tile.SliderTileHook
import com.gustate.mcga.xposed.systemui.feature.qs.tile.TwoXOneTileHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 控制中心磁贴 (1x1 & 2x1) 调度类
 */
class QSTileHook {

    companion object {
        const val QS_TILE_1X1_LOG = "控制中心 1*1 磁贴"
        const val QS_TILE_2X1_LOG = "控制中心 2*1 磁贴"
        const val QS_TILE_MEDIA_LOG = "控制中心媒体磁贴"
    }

    private val twoXOneTileHook = TwoXOneTileHook()
    private val sliderTileHook = SliderTileHook()
    private val mediaTileHook = MediaTileHook()

    /**
     * 修改控制中心 1*1 磁贴圆角半径
     * @param module XposedModule 实例
     * @param param 软件包加载参数
     * @param bkgCornerRadius 圆角半径 (dp)
     */
    fun hookQsOneXOneTile(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        bkgCornerRadius: Float
    ) {
        val osVer = RootUtils.getColorOSVersion()
        if (osVer.startsWith(prefix = "V16.1"))
            hookQsOneXOneTileOS161(
                module = module,
                param = param,
                bkgCornerRadius = bkgCornerRadius
            )
        else
            hookQsOneXOneTileOS160(
                module = module,
                param = param,
                bkgCornerRadius = bkgCornerRadius
            )
    }

    /**
     * 修改控制中心 1*1 磁贴圆角半径
     * 适配 ColorOS V16.1.0
     * @param module XposedModule 实例
     * @param param 软件包加载参数
     * @param bkgCornerRadius 圆角半径 (dp)
     */
    private fun hookQsOneXOneTileOS161(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        bkgCornerRadius: Float
    ) {
        val classLoader = param.classLoader
        try {
            val sepQSResPoolClazz = loadClass(
                className = "com.oplus.systemui.qs.base.res.SepQSResPool",
                classLoader = classLoader
            )
            val qsConstantClazz = loadClass(
                className = "com.oplus.systemui.qs.base.res.util.QSConstant",
                classLoader = classLoader
            )
            val getTileOutline = sepQSResPoolClazz
                .getDeclaredMethod("getTileOutline")
            val getCustomOutline = qsConstantClazz.getDeclaredMethod(
                "getSmoothRoundRectOutlineProvider",
                Context::class.java,
                Float::class.javaPrimitiveType
            )
            module.hook(getTileOutline).intercept { chain ->
                try {
                    val tileOutlineField = sepQSResPoolClazz
                        .getDeclaredField("_tileOutline")
                        .apply { isAccessible = true }
                    val context = ContextHelper.getContext(classLoader = classLoader)
                    val outline = getCustomOutline.invoke(
                        null, context,
                        bkgCornerRadius.dpToPx(context = context)
                    )
                    // 直接 setValue 替换
                    val mutableStateFlow = tileOutlineField.get(null) // static 字段传 null
                    val setValueMethod = mutableStateFlow::class.java
                        .getMethod("setValue", Any::class.java)
                    setValueMethod.invoke(mutableStateFlow, outline)
                    log(
                        module = module, tag = QS_TILE_1X1_LOG,
                        message = "✅ 成功修改 1*1 磁贴圆角半径为 $bkgCornerRadius dp"
                    )
                    return@intercept chain.proceed()
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_1X1_LOG,
                        message = "❌ 修改 1*1 磁贴圆角半径失败",
                        throwable = e
                    )
                    return@intercept chain.proceed()
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_1X1_LOG,
                message = "❌ 修改 1*1 磁贴圆角半径失败",
                throwable = e
            )
        }
    }

    /**
     * 修改控制中心 1*1 磁贴圆角半径
     * 适配 ColorOS V16.0.0
     * @param module XposedModule 实例
     * @param param 软件包加载参数
     * @param bkgCornerRadius 圆角半径 (dp)
     */
    private fun hookQsOneXOneTileOS160(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        bkgCornerRadius: Float
    ) {
        try {
            val tileViewClass = loadClass(
                className = "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSResizeableTileViewOneXOne",
                classLoader = param.classLoader
            )
            // Hook getViewRadius 返回圆角（px）
            val getViewRadius = tileViewClass.getDeclaredMethod("getViewRadius")
            module.hook(getViewRadius).intercept { chain ->
                val view = chain.thisObject
                val context = view
                    .getAnyField<Context>("mContext")
                    ?: return@intercept chain.proceed()
                val radiusPx = bkgCornerRadius.dpToPx(context)
                radiusPx
            }
            // Hook createBgOutlineProvider 禁用 circleShape
            val createBgOutlineProvider = tileViewClass.getDeclaredMethod(
                "createBgOutlineProvider",
                View::class.java
            )
            module.hook(createBgOutlineProvider).intercept { chain ->
                val result = chain.proceed()
                try {
                    val outlineProvider = chain.proceed()
                    if (outlineProvider != null) {
                        // 反射调用 setCircleShape(false) 让它变成圆角矩形而不是圆形
                        val setCircleShapeMethod = outlineProvider.javaClass.getMethod(
                            "setCircleShape",
                            Boolean::class.javaPrimitiveType
                        )
                        setCircleShapeMethod.invoke(outlineProvider, false)
                    }
                    log(
                        module = module, tag = QS_TILE_1X1_LOG,
                        message = "✅ 成功修改控制中心 1*1 磁贴圆角半径为 ${bkgCornerRadius}dp"
                    )
                    return@intercept outlineProvider
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_1X1_LOG,
                        message = "❌ 修改控制中心 1*1 磁贴圆角半径失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }

        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_1X1_LOG,
                message = "❌ 修改控制中心 1*1 磁贴圆角半径失败",
                throwable = e
            )
        }
    }

    /**
     * 修改 1x1 磁贴的列表行数
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param columns 目标列数/行数配置
     */
    fun hookQsTileOneXOneRowColumns(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        columns: Int
    ) {
        try {
            val calculatorClass = loadClass(
                className = "com.oplus.systemui.plugins.qs.CellCalculatorManager",
                classLoader = param.classLoader
            )
            val method = calculatorClass.getDeclaredMethod(
                "setNoPersonalRowCountPort",
                Int::class.javaPrimitiveType
            )
            module.hook(method).intercept { chain ->
                try {
                    val args = chain.args.toMutableList()
                    args[0] = columns
                    log(
                        module = module, tag = QS_TILE_1X1_LOG,
                        message = "✅ 成功修改 1x1 磁贴的列表行数为 $columns 行"
                    )
                    return@intercept chain.proceed(args.toTypedArray())
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_1X1_LOG,
                        message = "❌ 修改 1x1 磁贴的列表行数失败",
                        throwable = e
                    )
                    return@intercept chain.proceed()
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_1X1_LOG,
                message = "❌ 修改 1x1 磁贴的列表行数失败",
                throwable = e
            )
        }
    }

    /**
     * Hook 控制中心 2*1 磁贴的综合入口
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 2*1 磁贴圆角半径
     * @param fillTileStateFullBkg 使磁贴状态填满控制中心 2*1 磁贴
     * @param hideTileIconBkg 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * @param tileIconSizeDp 控制中心 2*1 磁贴图标大小
     * @param inactiveTitleColor 非激活标题颜色
     * @param inactiveDesColor 非激活描述颜色
     * @param activeTitleColor 激活标题颜色
     * @param activeDesColor 激活描述颜色
     */
    fun hookTwoXOneTile(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float?,
        fillTileStateFullBkg: Boolean?,
        hideTileIconBkg: Boolean?,
        tileIconSizeDp: Float?,
        @ColorInt inactiveTitleColor: Int,
        @ColorInt inactiveDesColor: Int,
        @ColorInt activeTitleColor: Int,
        @ColorInt activeDesColor: Int
    ) {
        // 修改圆角
        cornerRadiusDp?.let {
            twoXOneTileHook.modifyCornerRadius(module, param, it)
        }
        // 填满背景
        if (fillTileStateFullBkg == true) {
            twoXOneTileHook.modifyTileStateFullBkg(module, param)
        }
        // 隐藏图标背景
        if (hideTileIconBkg == true) {
            twoXOneTileHook.hideTileIconBkg(module, param)
        }
        // 图标大小
        tileIconSizeDp?.let {
            twoXOneTileHook.modifyTileIconSize(module, param, it)
        }
        // 字体颜色
        twoXOneTileHook.modifyTileTextColor(
            module, param,
            inactiveTitleColor, inactiveDesColor,
            activeTitleColor, activeDesColor
        )
    }

    /**
     * 修改控制中心拖动条磁贴圆角半径
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    fun hookSliderTile(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        sliderTileHook.modifyCornerRadius(
            module = module,
            param = param,
            cornerRadiusDp = cornerRadiusDp
        )
    }

    /**
     * 修改控制中心媒体磁贴圆角半径
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    fun hookMediaTile(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        mediaTileHook.modifyCornerRadius(
            module = module,
            param = param,
            cornerRadiusDp = cornerRadiusDp
        )
    }

}