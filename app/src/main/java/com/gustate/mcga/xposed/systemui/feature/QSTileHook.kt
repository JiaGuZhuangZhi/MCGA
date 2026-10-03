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

package com.gustate.mcga.xposed.systemui.feature

import android.content.Context
import android.util.Log
import android.view.View
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.RootUtils
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.callStaticMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getStaticField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.systemui.feature.qs.SmoothRoundHook
import com.gustate.mcga.xposed.systemui.feature.qs.tile.MediaTileHook
import com.gustate.mcga.xposed.systemui.feature.qs.tile.SliderTileHook
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

    //private val twoXOneTileHook = TwoXOneTileHook()
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
        SmoothRoundHook().hookSmoothRoundSize(
            module = module,
            param = param
        )
        val osVer = RootUtils.getColorOSVersion()
        val action = when {
            listOf("V17.0", "V16.1").any {
                osVer.startsWith(prefix = it)
            } -> ::hookQsOneXOneTileOS161And170

            osVer.startsWith(prefix = "V16.0") ->
                ::hookQsOneXOneTileOS160

            else -> return log(
                module = module, tag = QS_TILE_2X1_LOG, priority = Log.ERROR,
                message = "❌ 修改 1*1 磁贴圆角半径失败, 该版本尚未适配此功能"
            )
        }
        action(
            module,
            param,
            bkgCornerRadius
        )
    }

    /**
     * 修改控制中心 1*1 磁贴圆角半径
     * 适配 ColorOS V16.1.0 与 ColorOS 17.0.0
     * @param module XposedModule 实例
     * @param param 软件包加载参数
     * @param bkgCornerRadius 圆角半径 (dp)
     */
    private fun hookQsOneXOneTileOS161And170(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        bkgCornerRadius: Float
    ) {
        val classLoader = param.classLoader
        try {
            val sepQSResPoolClazz = loadClass(
                name = "com.oplus.systemui.qs.base.res.SepQSResPool",
                loader = classLoader
            )
            val qsConstantClazz = loadClass(
                name = "com.oplus.systemui.qs.base.res.util.QSConstant",
                loader = classLoader
            )
            val getTileOutline = sepQSResPoolClazz.getAnyMethod(
                name = "getTileOutline",
                classLoader = classLoader
            )
            module.hook(getTileOutline).intercept { chain ->
                try {
                    val context = ContextHelper.getContext(classLoader = classLoader)
                    val cornerRadiusPx = bkgCornerRadius.dpToPx(context = context)
                    val outline = qsConstantClazz.callStaticMethod<Any>(
                        name = "getSmoothRoundRectOutlineProvider",
                        params = arrayOf(context, cornerRadiusPx),
                        paramTypes = arrayOf(
                            Context::class.java,
                            Float::class.javaPrimitiveType
                        ),
                        classLoader = classLoader
                    )
                    val tileOutlineStateFlow = sepQSResPoolClazz
                        .getStaticField<Any>(name = "_tileOutline")
                    tileOutlineStateFlow.callAnyMethod<Any>(
                        name = "setValue",
                        params = arrayOf(outline),
                        paramTypes = arrayOf(
                            Any::class.java
                        ),
                        classLoader = classLoader
                    )
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
                name = "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSResizeableTileViewOneXOne",
                loader = param.classLoader
            )
            // Hook getViewRadius 返回圆角（px）
            val getViewRadius = tileViewClass.getDeclaredMethod("getViewRadius")
            module.hook(getViewRadius).intercept { chain ->
                val view = chain.thisObject
                val context = view
                    .getAnyField<Context>("mContext")
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
                name = "com.oplus.systemui.plugins.qs.CellCalculatorManager",
                loader = param.classLoader
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

    /*/**
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
    }*/

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