package com.gustate.mcga.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.gustate.mcga.ui.theme.blur.BaseBlurStyles
import com.gustate.mcga.ui.theme.blur.BlurStyles
import com.gustate.mcga.ui.theme.blur.GustateBlurStyles
import com.gustate.mcga.ui.theme.blur.IosBlurStyles
import com.gustate.mcga.ui.theme.blur.MaterialBlurStyles
import com.gustate.mcga.ui.theme.color.ColorScheme
import com.gustate.mcga.ui.theme.color.dynamicDarkColorScheme
import com.gustate.mcga.ui.theme.color.dynamicLightColorScheme
import com.gustate.mcga.ui.theme.color.gustateDarkColorScheme
import com.gustate.mcga.ui.theme.color.gustateLightColorScheme
import com.gustate.mcga.ui.theme.color.rememberAnimatedColorScheme

// 创建 CompositionLocal 用于提供自定义属性
// 提供自定义 ColorScheme
private val LocalgustateColors =
    staticCompositionLocalOf { gustateLightColorScheme() }

// 提供自定义主题配置
private val LocalgustateThemes =
    staticCompositionLocalOf { GustateThemeConfig() }

// 提供自定义 BlurStyle
private val LocalgustateBlurStyles =
    staticCompositionLocalOf<BaseBlurStyles> { GustateBlurStyles() }

/**
 * Gustate Theme
 * @param themeType 强制设置主体类型
 * @param dynamicColorForce 强制设置莫奈取色
 * @param darkTheme 强制设置为深色模式
 * @param content Composable
 */
@Composable
fun GustateTheme(
    themeType: ThemeType = ThemeType.Gustate,
    dynamicColorForce: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = rememberAnimatedColorScheme(
        target = rememberColorScheme(
            darkTheme = darkTheme,
            dynamicColorForce = dynamicColorForce
        )
    )
    val themeConfig = GustateThemeConfig(
        isDark = darkTheme,
        isDynamicColor = dynamicColorForce,
        themeType = themeType
    )
    val blurStyles = when (themeType) {
        ThemeType.Gustate -> GustateBlurStyles()
        ThemeType.Material -> MaterialBlurStyles()
        ThemeType.Ios -> IosBlurStyles()
    }

    CompositionLocalProvider(
        LocalgustateColors provides colorScheme,
        LocalgustateThemes provides themeConfig,
        LocalgustateBlurStyles provides blurStyles
    ) {

        MaterialTheme(
            content = content
        )

    }

}

/**
 * 主题颜色生成器
 * @param darkTheme 是否开启深色模式
 * @param dynamicColorForce 是否开启动态去色 (仅安卓)
 */
@Composable
fun rememberColorScheme(
    darkTheme: Boolean,
    dynamicColorForce: Boolean?
): ColorScheme {
    val context = LocalContext.current
    print(darkTheme)
    return remember(key1 = darkTheme, key2 = dynamicColorForce) {
        if (dynamicColorForce == true) {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        } else {
            if (darkTheme) gustateDarkColorScheme()
            else gustateLightColorScheme()
        }
    }
}

/**
 * 基础主题配置
 * @param isDark 是否启用深色模式
 * @param themeType 主体类型
 */
data class GustateThemeConfig(
    val isDark: Boolean = false,
    val isDynamicColor: Boolean = false,
    val themeType: ThemeType = ThemeType.Gustate
)

/**
 * 主题接口
 */
sealed interface ThemeType {

    // 主体信息载体
    val themeCode: Int
    val themeName: String

    // 主题实现
    object Gustate : ThemeType {
        override val themeCode = 0
        override val themeName = "gustate"
    }

    object Material : ThemeType {
        override val themeCode = 1
        override val themeName = "Material"
    }

    object Ios : ThemeType {
        override val themeCode = 2
        override val themeName = "iOS"
    }

    /**
     * 从主题代码反序列化到主题
     */
    companion object {
        val entries = listOf(Gustate, Material, Ios)
        fun fromThemeCode(code: Int): ThemeType {
            return when (code) {
                0 -> Gustate
                1 -> Material
                2 -> Ios
                else -> Gustate
            }
        }
    }

}

// 供外部读取颜色值
val gustateColors: ColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalgustateColors.current

// 供外部读取主题状态
val gustateThemes: GustateThemeConfig
    @Composable
    @ReadOnlyComposable
    get() = LocalgustateThemes.current

// 供外部读取模糊样式
val gustateBlurStyles: BlurStyles
    @Composable
    @ReadOnlyComposable
    get() = LocalgustateBlurStyles.current

// 供外部读取是否使用 MD3E 主题
val isMaterialTheme: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalgustateThemes.current.themeType == ThemeType.Material