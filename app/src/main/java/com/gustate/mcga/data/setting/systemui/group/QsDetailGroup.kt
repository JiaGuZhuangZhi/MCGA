package com.gustate.mcga.data.setting.systemui.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.ColorSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.IntSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 控制中心快速设置面板
 * @param title 标题
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
object QsDetailGroup : SettingGroup(
    title = R.string.qs_detail_container,
    dependency = null,
    children = listOf(
        EnableCustomQsDetail,
        QsDetailBkgCoverColor,
        QsDetailFrgCoverColor,
        DetailBkgBlurRadius,
        QsDetailBkgCornerRadius
    )
)

/**
 * 启用对控制中心详情面板的修改
 */
object EnableCustomQsDetail : BooleanSetting(
    key = "enable_custom_qs_detail",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings,
    dependency = null
)

/**
 * 控制中心详情面板背景混色1
 */
object QsDetailBkgCoverColor : ColorSetting(
    key = "qs_detail_cover_color",
    default = 0X80000000.toInt(),
    icon = R.drawable.format_color_fill,
    title = R.string.bkg_cover_color,
    dependency = EnableCustomQsDetail
)

/**
 * 控制中心详情面板背景混色2
 */
object QsDetailFrgCoverColor : ColorSetting(
    key = "qs_detail_cover_frg_color",
    default = 0X80000000.toInt(),
    icon = R.drawable.opacity,
    title = R.string.frg_cover_color,
    dependency = EnableCustomQsDetail
)

/**
 * 控制中心详情面板背景模糊半径
 */
object DetailBkgBlurRadius : IntSetting(
    key = "qs_detail_blur_radius",
    icon = R.drawable.blur_on,
    title = R.string.bkg_blur_radius,
    min = 0,
    max = 2560,
    default = 1024,
    dependency = EnableCustomQsDetail
)

/**
 * 控制中心详情面板背景圆角半径
 */
object QsDetailBkgCornerRadius : FloatSetting(
    key = "qs_detail_corner_radius",
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    min = 0f,
    max = 96f,
    default = 24f,
    dependency = EnableCustomQsDetail
)