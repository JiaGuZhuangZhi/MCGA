package com.gustate.mcga.panel

import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.SystemUIViewModel
import com.gustate.mcga.ui.dialog.ColorPickDialog
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SliderWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import dev.chrisbanes.haze.hazeSource

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
        title = stringResource(R.string.systemui),
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
            SplicedColumnGroup(
                modifier = Modifier,
                title = stringResource(id = R.string.qs_detail_container),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.info_outline),
                            title = stringResource(id = R.string.enable_custom_settings),
                            checked = uiState.enableCustomQsDetail,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableCustomQsDetail(enabled = checked)
                            }
                        )
                    },
                    {
                        var showDialog by remember {
                            mutableStateOf(false)
                        }
                        OptionWidget(
                            painter = painterResource(id = R.drawable.info_outline),
                            enabled = uiState.enableCustomQsDetail,
                            title = stringResource(id = R.string.bkg_cover_color),
                            description = String.format(
                                "#%08X",
                                uiState.qsDetailBkgCoverColor.and(0xFFFFFFFF.toInt())
                            ),
                            onClick = {
                                showDialog = true
                            }
                        )
                        if (showDialog) {
                            ColorPickDialog(
                                painter = painterResource(id = R.drawable.info_outline),
                                title = stringResource(id = R.string.bkg_cover_color),
                                description = stringResource(id = R.string.color_picker),
                                initialColor = uiState.qsDetailBkgCoverColor,
                                onConfirmation = {
                                    Log.e("color", it.toString())
                                    viewModel.updateQsDetailBkgCoverColor(it)
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
                            painter = painterResource(id = R.drawable.info_outline),
                            enabled = uiState.enableCustomQsDetail,
                            title = stringResource(id = R.string.frg_cover_color),
                            description = String.format(
                                "#%08X",
                                uiState.qsDetailFrgCoverColor.and(0xFFFFFFFF.toInt())
                            ),
                            onClick = {
                                showDialog = true
                            }
                        )
                        if (showDialog) {
                            ColorPickDialog(
                                painter = painterResource(id = R.drawable.info_outline),
                                title = stringResource(id = R.string.frg_cover_color),
                                description = stringResource(id = R.string.color_picker),
                                initialColor = uiState.qsDetailFrgCoverColor,
                                onConfirmation = {
                                    Log.e("color", it.toString())
                                    viewModel.updateQsDetailFrgCoverColor(it)
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
                            painter = painterResource(id = R.drawable.panels_outline),
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
                            painter = painterResource(id = R.drawable.panels_outline),
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
            SplicedColumnGroup(
                modifier = Modifier,
                title = stringResource(id = R.string.qs_resizeable_tile),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.info_outline),
                            title = stringResource(id = R.string.enable_custom_settings),
                            checked = uiState.enableCustomQsResizeableTile,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableCustomQsResizeableTile(value = checked)
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.panels_outline),
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
            SplicedColumnGroup(
                modifier = Modifier,
                title = stringResource(id = R.string.aod),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.info_outline),
                            title = stringResource(id = R.string.enable_all_day_panoramic_aod),
                            description = stringResource(id = R.string.tip_all_day_panoramic_aod),
                            checked = uiState.enableAodPanoramicAllDay,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableAodPanoramicAllDay(value = checked)
                            }
                        )
                    }
                )
            )
        }
    }
}