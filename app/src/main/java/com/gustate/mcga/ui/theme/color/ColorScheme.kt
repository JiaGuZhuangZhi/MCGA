package com.gustate.mcga.ui.theme.color

import android.content.Context
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun gustateLightColorScheme(
    pm: Color = primary,
    onPm: Color = onPrimary,
    bkg: Color = background,
    onBkgPrimary: Color = onBackgroundPrimary,
    onBkgSecondary: Color = onBackgroundSecondary,
    onBkgSubtitle: Color = onBackgroundSubtitle,
    filledBtn: Color = filledButton,
    onFilledBtn: Color = onFilledButton,
    filledTonalBtn: Color = filledTonalButton,
    onFilledTonalBtn: Color = onFilledTonalButton,
    cd: Color = container,
    onCdPrimary: Color = onContainerPrimary,
    onCdSecondary: Color = onContainerSecondary,
    primaryCd: Color = primaryContainer,
    onPrimaryCd: Color = onPrimaryContainer,
    errorCd: Color = errorContainer,
    onErrorCd: Color = onErrorContainer,
    warningCd: Color = warningContainer,
    onWarningCd: Color = onWarningContainer,
    dlg: Color = dialog,
    onDlgPrimary: Color = onDialogPrimary,
    onDlgSecondary: Color = onDialogSecondary,
    stcTrackUnchecked: Color = switchTrackUnchecked,
    stcTrackChecked: Color = switchTrackChecked,
    stcThumbUnchecked: Color = switchThumbUnchecked,
    stcThumbChecked: Color = switchThumbChecked
): ColorScheme =
    ColorScheme(
        primary = pm,
        onPrimary = onPm,
        background = bkg,
        onBackgroundPrimary = onBkgPrimary,
        onBackgroundSecondary = onBkgSecondary,
        onBackgroundSubtitle = onBkgSubtitle,
        filledButton = filledBtn,
        onFilledButton = onFilledBtn,
        filledTonalButton = filledTonalBtn,
        onFilledTonalButton = onFilledTonalBtn,
        container = cd,
        onContainerPrimary = onCdPrimary,
        onContainerSecondary = onCdSecondary,
        primaryContainer = primaryCd,
        onPrimaryContainer = onPrimaryCd,
        errorContainer = errorCd,
        onErrorContainer = onErrorCd,
        warningContainer = warningCd,
        onWarningContainer = onWarningCd,
        dialog = dlg,
        onDialogPrimary = onDlgPrimary,
        onDialogSecondary = onDlgSecondary,
        switchTrackUnchecked = stcTrackUnchecked,
        switchTrackChecked = stcTrackChecked,
        switchThumbUnchecked = stcThumbUnchecked,
        switchThumbChecked = stcThumbChecked
    )

fun gustateDarkColorScheme(
    pm: Color = primaryDark,
    onPm: Color = onPrimaryDark,
    bkg: Color = backgroundDark,
    onBkgPrimary: Color = onBackgroundPrimaryDark,
    onBkgSecondary: Color = onBackgroundSecondaryDark,
    onBkgSubtitle: Color = onBackgroundSubtitleDark,
    filledBtn: Color = filledButtonDark,
    onFilledBtn: Color = onFilledButtonDark,
    filledTonalBtn: Color = filledTonalButtonDark,
    onFilledTonalBtn: Color = onFilledTonalButtonDark,
    cd: Color = containerDark,
    onCdPrimary: Color = onContainerPrimaryDark,
    onCdSecondary: Color = onContainerSecondaryDark,
    primaryCd: Color = primaryContainerDark,
    onPrimaryCd: Color = onPrimaryContainerDark,
    errorCd: Color = errorContainerDark,
    onErrorCd: Color = onErrorContainerDark,
    warningCd: Color = warningContainerDark,
    onWarningCd: Color = onWarningContainerDark,
    dlg: Color = dialogDark,
    onDlgPrimary: Color = onDialogPrimaryDark,
    onDlgSecondary: Color = onDialogSecondaryDark,
    stcTrackUnchecked: Color = switchTrackUncheckedDark,
    stcTrackChecked: Color = switchTrackCheckedDark,
    stcThumbUnchecked: Color = switchThumbUncheckedDark,
    stcThumbChecked: Color = switchThumbCheckedDark
): ColorScheme =
    ColorScheme(
        primary = pm,
        onPrimary = onPm,
        background = bkg,
        onBackgroundPrimary = onBkgPrimary,
        onBackgroundSecondary = onBkgSecondary,
        onBackgroundSubtitle = onBkgSubtitle,
        filledButton = filledBtn,
        onFilledButton = onFilledBtn,
        filledTonalButton = filledTonalBtn,
        onFilledTonalButton = onFilledTonalBtn,
        container = cd,
        onContainerPrimary = onCdPrimary,
        onContainerSecondary = onCdSecondary,
        primaryContainer = primaryCd,
        onPrimaryContainer = onPrimaryCd,
        errorContainer = errorCd,
        onErrorContainer = onErrorCd,
        warningContainer = warningCd,
        onWarningContainer = onWarningCd,
        dialog = dlg,
        onDialogPrimary = onDlgPrimary,
        onDialogSecondary = onDlgSecondary,
        switchTrackUnchecked = stcTrackUnchecked,
        switchTrackChecked = stcTrackChecked,
        switchThumbUnchecked = stcThumbUnchecked,
        switchThumbChecked = stcThumbChecked
    )

fun dynamicLightColorScheme(context: Context): ColorScheme {
    val scheme = androidx.compose.material3.dynamicLightColorScheme(context)
    return gustateLightColorScheme(
        pm = scheme.primary,
        onPm = scheme.onPrimary,
        bkg = scheme.background,
        onBkgPrimary = scheme.onBackground,
        onBkgSecondary = scheme.onBackground.copy(alpha = 0.8f),
        onBkgSubtitle = scheme.onBackground.copy(alpha = 0.6f),
        filledBtn = scheme.primary,
        onFilledBtn = scheme.onPrimary,
        filledTonalBtn = scheme.primaryContainer.copy(alpha = 0.6f),
        onFilledTonalBtn = scheme.onPrimaryContainer,
        cd = scheme.surfaceContainer,
        onCdPrimary = scheme.onSurface,
        onCdSecondary = scheme.onSurface.copy(alpha = 0.8f),
        primaryCd = scheme.primary,
        onPrimaryCd = scheme.onPrimary,
        errorCd = scheme.errorContainer,
        onErrorCd = scheme.onErrorContainer,
        warningCd = scheme.tertiaryContainer,
        onWarningCd = scheme.onTertiaryContainer,
        dlg = scheme.surfaceContainer,
        onDlgPrimary = scheme.onSurface,
        onDlgSecondary = scheme.onSurface.copy(alpha = 0.8f),
        stcTrackUnchecked = scheme.surfaceContainerHighest,
        stcTrackChecked = scheme.primary,
        stcThumbUnchecked = scheme.outline,
        stcThumbChecked = scheme.onPrimary
    )
}

fun dynamicDarkColorScheme(context: Context): ColorScheme {
    val scheme = androidx.compose.material3.dynamicDarkColorScheme(context)
    return gustateDarkColorScheme(
        pm = scheme.primary,
        onPm = scheme.onPrimary,
        bkg = scheme.background,
        onBkgPrimary = scheme.onBackground,
        onBkgSecondary = scheme.onBackground.copy(alpha = 0.8f),
        onBkgSubtitle = scheme.onBackground.copy(alpha = 0.6f),
        filledBtn = scheme.primary,
        onFilledBtn = scheme.onPrimary,
        filledTonalBtn = scheme.primaryContainer.copy(alpha = 0.6f),
        onFilledTonalBtn = scheme.onPrimaryContainer,
        cd = scheme.surfaceContainer,
        onCdPrimary = scheme.onSurface,
        onCdSecondary = scheme.onSurface.copy(alpha = 0.8f),
        primaryCd = scheme.primary,
        onPrimaryCd = scheme.onPrimary,
        errorCd = scheme.errorContainer,
        onErrorCd = scheme.onErrorContainer,
        warningCd = scheme.tertiaryContainer,
        onWarningCd = scheme.onTertiaryContainer,
        dlg = scheme.surfaceContainer,
        onDlgPrimary = scheme.onSurface,
        onDlgSecondary = scheme.onSurface.copy(alpha = 0.8f),
        stcTrackUnchecked = scheme.surfaceContainerHighest,
        stcTrackChecked = scheme.primary,
        stcThumbUnchecked = scheme.outline,
        stcThumbChecked = scheme.onPrimary
    )
}

@Composable
fun rememberAnimatedColorScheme(target: ColorScheme): ColorScheme {
    val transition = updateTransition(targetState = target, label = "ColorScheme")

    @Composable
    fun anim(label: String, get: (ColorScheme) -> Color): Color {
        return transition.animateColor(
            label = label,
            transitionSpec = {
                tween(durationMillis = 600, easing = FastOutSlowInEasing)
            }
        ) { state ->
            get(state)
        }.value
    }

    return ColorScheme(
        primary = anim(label = "primary") { it.primary },
        onPrimary = anim(label = "onPrimary") { it.onPrimary },
        background = anim(label = "background") { it.background },
        onBackgroundPrimary = anim(label = "onBackgroundPrimary") { it.onBackgroundPrimary },
        onBackgroundSecondary = anim(label = "onBackgroundSecondary") { it.onBackgroundSecondary },
        onBackgroundSubtitle = anim(label = "onBackgroundSubtitle") { it.onBackgroundSubtitle },
        filledButton = anim(label = "filledButton") { it.filledButton },
        onFilledButton = anim(label = "onFilledButton") { it.onFilledButton },
        filledTonalButton = anim(label = "filledTonalButton") { it.filledTonalButton },
        onFilledTonalButton = anim(label = "onFilledTonalButton") { it.onFilledTonalButton },
        container = anim(label = "card") { it.container },
        onContainerPrimary = anim(label = "onCardPrimary") { it.onContainerPrimary },
        onContainerSecondary = anim(label = "onCardSecondary") { it.onContainerSecondary },
        primaryContainer = anim(label = "primaryContainer") { it.primaryContainer },
        onPrimaryContainer = anim(label = "onPrimaryContainer") { it.onPrimaryContainer },
        errorContainer = anim(label = "errorCard") { it.errorContainer },
        onErrorContainer = anim(label = "onErrorCard") { it.onErrorContainer },
        warningContainer = anim(label = "warningContainer") { it.warningContainer },
        onWarningContainer = anim(label = "onWarningContainer") { it.onWarningContainer },
        dialog = anim(label = "dialog") { it.dialog },
        onDialogPrimary = anim(label = "onDialogPrimary") { it.onDialogPrimary },
        onDialogSecondary = anim(label = "onDialogSecondary") { it.onDialogSecondary },
        switchTrackUnchecked = anim(label = "switchTrackUnchecked") { it.switchTrackUnchecked },
        switchTrackChecked = anim(label = "switchTrackChecked") { it.switchTrackChecked },
        switchThumbUnchecked = anim(label = "switchThumbUnchecked") { it.switchThumbUnchecked },
        switchThumbChecked = anim(label = "switchThumbChecked") { it.switchThumbChecked }
    )
}

data class ColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val onBackgroundPrimary: Color,
    val onBackgroundSecondary: Color,
    val onBackgroundSubtitle: Color,
    val filledButton: Color,
    val onFilledButton: Color,
    val filledTonalButton: Color,
    val onFilledTonalButton: Color,
    val container: Color,
    val onContainerPrimary: Color,
    val onContainerSecondary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val dialog: Color,
    val onDialogPrimary: Color,
    val onDialogSecondary: Color,
    val switchTrackUnchecked: Color,
    val switchTrackChecked: Color,
    val switchThumbUnchecked: Color,
    val switchThumbChecked: Color
)