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

package com.gustate.mcga.data.setting.home.group

import android.content.SharedPreferences
import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.ColorSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingExt.getValue
import com.gustate.mcga.data.setting.base.SettingExt.listenConfig
import com.gustate.mcga.data.setting.base.SettingGroup
import com.gustate.mcga.data.setting.base.SettingPage

/**
 * 桌面 Blur 设置组声明
 * (怎么这么像党名啊 hyw)
 */
object HomeBlurGroup : SettingGroup(
    title = R.string.global_home_blur,
    children = listOf(
        HomeBlurPage
    )
)

/**
 * 桌面 Blur 设置页声明
 */
object HomeBlurPage : SettingPage(
    route = "home/blur",
    command = "su -c pkill -f com.android.launcher",
    icon = R.drawable.blur_on,
    title = R.string.blur_param,
    children = listOf(
        HomeUnifyBlurGroup
    )
)

/**
 * 统一桌面模糊设置组声明
 * (怎么这么像党名啊 hyw)
 */
object HomeUnifyBlurGroup : SettingGroup(
    title = R.string.global_home_blur,
    children = listOf(
        EnableHomeUnifyBlur,
        HomeUnifyBlurRadiusLight,
        HomeUnifyBlurRadiusDark,
        HomeUnifyBlurBlendColorLight,
        HomeUnifyBlurBlendColorDark,
        HomeUnifyBlurMixedColorLight,
        HomeUnifyBlurMixedColorDark
    )
)

/**
 * 启用统一桌面模糊
 */
private object EnableHomeUnifyBlur : BooleanSetting(
    key = "enable_home_unify_blur",
    default = false,
    icon = R.drawable.blur_on,
    title = R.string.enable_global_blur_param,
    summary = null,
    dependency = null
)

/**
 * 统一桌面模糊半径 (浅色模式)
 */
private object HomeUnifyBlurRadiusLight : FloatSetting(
    key = "home_unify_blur_radius_light",
    default = 120f,
    min = 0f,
    max = 240f,
    icon = R.drawable.opacity,
    title = R.string.global_blur_radius_light,
    summary = R.string.effect_applied_globally,
    dependency = EnableHomeUnifyBlur
)

/**
 * 统一桌面模糊半径 (浅色模式)
 */
private object HomeUnifyBlurRadiusDark : FloatSetting(
    key = "home_unify_blur_radius_dark",
    default = 120f,
    min = 0f,
    max = 240f,
    icon = R.drawable.opacity,
    title = R.string.global_blur_radius_dark,
    summary = R.string.effect_applied_globally,
    dependency = EnableHomeUnifyBlur
)

/**
 * 统一桌面第一混合色 (浅色模式)
 */
private object HomeUnifyBlurBlendColorLight : ColorSetting(
    key = "home_unify_blend_color_light",
    default = 0X66526366,
    icon = R.drawable.format_color_fill,
    title = R.string.global_blend_color_light,
    summary = R.string.effect_applied_globally,
    dependency = EnableHomeUnifyBlur
)

/**
 * 统一桌面第一混合色 (深色模式)
 */
private object HomeUnifyBlurBlendColorDark : ColorSetting(
    key = "home_unify_blend_color_dark",
    default = 0X66526366,
    icon = R.drawable.format_color_fill,
    title = R.string.global_blend_color_dark,
    summary = R.string.effect_applied_globally,
    dependency = EnableHomeUnifyBlur
)

/**
 * 统一桌面第二混合色 (浅色模式)
 */
private object HomeUnifyBlurMixedColorLight : ColorSetting(
    key = "home_unify_mixed_color_light",
    default = 0XFF333333.toInt(),
    icon = R.drawable.format_color_fill,
    title = R.string.global_mixed_color_light,
    summary = R.string.effect_applied_globally,
    dependency = EnableHomeUnifyBlur
)

/**
 * 统一桌面第二混合色 (深色模式)
 */
private object HomeUnifyBlurMixedColorDark : ColorSetting(
    key = "home_unify_mixed_color_dark",
    default = 0XFF333333.toInt(),
    icon = R.drawable.format_color_fill,
    title = R.string.global_mixed_color_dark,
    summary = R.string.effect_applied_globally,
    dependency = EnableHomeUnifyBlur
)

/**
 * 从 SharedPreferences 读取桌面统一模糊的完整配置
 * 并填充到 [HomeBlurConfig] 中
 */
fun SharedPreferences.readHomeBlurConfig() = HomeBlurConfig(
    enabled = getValue(EnableHomeUnifyBlur),
    light = BlurModeConfig(
        radius = getValue(HomeUnifyBlurRadiusLight),
        blendColor = getValue(HomeUnifyBlurBlendColorLight),
        mixedColor = getValue(HomeUnifyBlurMixedColorLight)
    ),
    dark = BlurModeConfig(
        radius = getValue(HomeUnifyBlurRadiusDark),
        blendColor = getValue(HomeUnifyBlurBlendColorDark),
        mixedColor = getValue(HomeUnifyBlurMixedColorDark)
    )
)

/**
 * 监听桌面统一模糊配置变化
 * 当桌面统一模糊设置组中的任意配置发生变化时
 * 自动重新读取完整配置并回调
 * @param onConfigChanged 配置变化回调
 * @return 当前注册的监听器 可用于后续移除
 */
fun SharedPreferences.listenHomeBlurConfig(
    onConfigChanged: (HomeBlurConfig) -> Unit
): SharedPreferences.OnSharedPreferenceChangeListener =
    listenConfig(group = HomeUnifyBlurGroup) {
        onConfigChanged(readHomeBlurConfig())
    }

/**
 * 统一桌面模糊配置
 * @param enabled 是否启用统一桌面模糊参数
 * @param light 浅色模式模糊参数
 * @param dark 深色模式模糊参数
 */
data class HomeBlurConfig(
    val enabled: Boolean?,
    val light: BlurModeConfig,
    val dark: BlurModeConfig
)

/**
 * 单个主题模式下的桌面模糊参数
 * @param radius 模糊半径
 * @param blendColor 第一混合色
 * @param mixedColor 第二混合色
 */
data class BlurModeConfig(
    val radius: Float?,
    val blendColor: Int?,
    val mixedColor: Int?
)