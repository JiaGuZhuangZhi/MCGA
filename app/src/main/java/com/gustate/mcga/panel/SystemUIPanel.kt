package com.gustate.mcga.panel

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gustate.mcga.R
import com.gustate.mcga.data.state.SystemUIUiState
import com.gustate.mcga.data.viewmodel.SystemUIViewModel
import com.gustate.mcga.ui.dialog.ColorPickDialog
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SliderWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import com.gustate.mcga.utils.RootUtils
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SystemUIPanel(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val viewModel = viewModel<SystemUIViewModel>()
    val uiState = viewModel.uiState.value
    BasePanelPage(
        title = stringResource(id = R.string.systemui),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.android.systemui")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "systemui",
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    ) { paddingValues, scrollBehavior, hazeState ->
        Column(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            QsPanelLayoutSettings(uiState, viewModel)
            QsTileOneXOneSettings(uiState, viewModel)
            QsResizeableTileSettings(uiState, viewModel)
            QsDetailContainerSettings(uiState, viewModel)
            SplicedColumnGroup(
                modifier = Modifier,
                title = stringResource(id = R.string.aod),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.aod_tablet),
                            title = stringResource(id = R.string.enable_all_day_panoramic_aod),
                            description = stringResource(id = R.string.tip_all_day_panoramic_aod),
                            checked = uiState.enableAodPanoramicAllDay,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableAodPanoramicAllDay(value = checked)
                            }
                        )
                    },
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.settings_panorama),
                            title = stringResource(id = R.string.enable_aod_display_settings),
                            checked = uiState.enableAllDayAodSettings,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableAllDayAodSettings(value = checked)
                            }
                        )
                    },
                    {
                        OptionWidget(
                            painter = painterResource(id = R.drawable.mobile_share),
                            title = stringResource(id = R.string.activate_aod_display_settings),
                            onClick = {
                                viewModel.viewModelScope.launch {
                                    RootUtils.executeRootCommand(
                                        command = "am start -n com.oplus.aod/" +
                                                "com.oplus.aod.activity." +
                                                "AodSettingsDisplayActivity" +
                                                " -f 0x10000000"
                                    )
                                }
                            }
                        )
                    }
                )
            )
        }
    }
}

@Composable
private fun QsPanelLayoutSettings(
    uiState: SystemUIUiState,
    viewModel: SystemUIViewModel
) {
    SplicedColumnGroup(
        modifier = Modifier,
        title = stringResource(id = R.string.qs_panel),
        content = listOf(
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.architecture),
                    title = stringResource(id = R.string.enable_custom_settings),
                    checked = uiState.enableCustomQsPanelLayout,
                    onCheckedChange = { checked ->
                        viewModel.updateEnableCustomQsPanelLayout(enabled = checked)
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.panels_outline),
                    enabled = uiState.enableCustomQsPanelLayout,
                    title = stringResource(id = R.string.qs_panel_status_bar_margin_top),
                    description = stringResource(id = R.string.qs_panel_status_bar_margin_top_tips),
                    value = uiState.qsPanelStatusBarMarginTop,
                    valueRange = 8f..64f,
                    onValueChange = {
                        viewModel.updateQsPanelStatusBarMarginTop(marginTop = it)
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.height),
                    enabled = uiState.enableCustomQsPanelLayout,
                    title = stringResource(id = R.string.qs_panel_cell_height),
                    value = uiState.qsPanelCellHeight,
                    valueRange = 48f..96f,
                    onValueChange = {
                        viewModel.updateQsPanelCellHeight(height = it)
                    }
                )
            }
        )
    )
}

@Composable
private fun QsTileOneXOneSettings(
    uiState: SystemUIUiState,
    viewModel: SystemUIViewModel
) {
    SplicedColumnGroup(
        modifier = Modifier,
        title = stringResource(id = R.string.qs_1x1_tile),
        content = listOf(
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.architecture),
                    title = stringResource(id = R.string.enable_custom_settings),
                    checked = uiState.enableCustomQsTileOneXOne,
                    onCheckedChange = { checked ->
                        viewModel.updateEnableCustomQsTileOneXOne(enabled = checked)
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.rounded_corner),
                    enabled = uiState.enableCustomQsTileOneXOne,
                    title = stringResource(id = R.string.bkg_corner_radius),
                    value = uiState.qsTileOneXOneCornerRadius,
                    valueRange = 0f..96f,
                    onValueChange = {
                        viewModel.updateQsTileOneXOneCornerRadius(bkgCornerRadius = it)
                    }
                )
            }
        )
    )
}

@Composable
private fun QsResizeableTileSettings(
    uiState: SystemUIUiState,
    viewModel: SystemUIViewModel
) {
    SplicedColumnGroup(
        modifier = Modifier,
        title = stringResource(id = R.string.qs_resizeable_tile),
        content = listOf(
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.architecture),
                    title = stringResource(id = R.string.enable_custom_settings),
                    checked = uiState.enableCustomQsResizeableTile,
                    onCheckedChange = { checked ->
                        viewModel.updateEnableCustomQsResizeableTile(value = checked)
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.rounded_corner),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.bkg_corner_radius),
                    value = uiState.qsResizeableTileCornerRadius,
                    valueRange = 0f..96f,
                    onValueChange = {
                        viewModel.updateQsResizeableTileCornerRadius(value = it)
                    }
                )
            }
        )
    )
}

@Composable
private fun QsDetailContainerSettings(
    uiState: SystemUIUiState,
    viewModel: SystemUIViewModel
) {
    SplicedColumnGroup(
        modifier = Modifier,
        title = stringResource(id = R.string.qs_detail_container),
        content = listOf(
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.architecture),
                    title = stringResource(id = R.string.enable_custom_settings),
                    checked = uiState.enableCustomQsDetail,
                    onCheckedChange = { checked ->
                        viewModel.updateEnableCustomQsDetail(enabled = checked)
                    }
                )
            },
            {
                var showDialog by remember { mutableStateOf(false) }
                OptionWidget(
                    painter = painterResource(id = R.drawable.format_color_fill),
                    enabled = uiState.enableCustomQsDetail,
                    title = stringResource(id = R.string.bkg_cover_color),
                    description = String.format(
                        "#%08X",
                        uiState.qsDetailBkgCoverColor.and(0xFFFFFFFF.toInt())
                    ) + " (${stringResource(id = R.string.only_keep_rgb_channel)})",
                    onClick = {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    ColorPickDialog(
                        painter = painterResource(id = R.drawable.format_color_fill),
                        title = stringResource(id = R.string.bkg_cover_color),
                        description = stringResource(id = R.string.color_picker),
                        initialColor = uiState.qsDetailBkgCoverColor,
                        onConfirmation = {
                            viewModel.updateQsDetailBkgCoverColor(value = it)
                            showDialog = false
                        },
                        onDismissRequest = {
                            showDialog = false
                        }
                    )
                }
            },
            {
                var showDialog by remember {
                    mutableStateOf(false)
                }
                OptionWidget(
                    painter = painterResource(id = R.drawable.opacity),
                    enabled = uiState.enableCustomQsDetail,
                    title = stringResource(id = R.string.frg_cover_color),
                    description = String.format(
                        "#%08X",
                        uiState.qsDetailFrgCoverColor.and(0xFFFFFFFF.toInt())
                    ) + " (${stringResource(id = R.string.only_keep_alpha_channel)})",
                    onClick = {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    ColorPickDialog(
                        painter = painterResource(id = R.drawable.opacity),
                        title = stringResource(id = R.string.frg_cover_color),
                        description = stringResource(id = R.string.color_picker),
                        initialColor = uiState.qsDetailFrgCoverColor,
                        onConfirmation = {
                            viewModel.updateQsDetailFrgCoverColor(value = it)
                            showDialog = false
                        },
                        onDismissRequest = {
                            showDialog = false
                        }
                    )
                }
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.blur_on),
                    enabled = uiState.enableCustomQsDetail,
                    title = stringResource(id = R.string.bkg_blur_radius),
                    value = uiState.qsDetailBkgBlurRadius.toFloat(),
                    valueRange = 0f..2560f,
                    onValueChange = {
                        viewModel.updateQsDetailBkgBlurRadius(value = it.toInt())
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.rounded_corner),
                    enabled = uiState.enableCustomQsDetail,
                    title = stringResource(id = R.string.bkg_corner_radius),
                    value = uiState.qsDetailBkgCornerRadius,
                    valueRange = 0f..96f,
                    onValueChange = {
                        viewModel.updateQsDetailBkgCornerRadius(value = it)
                    }
                )
            }
        )
    )
}