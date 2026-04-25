package com.gustate.mcga.panel.systemui

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
fun TilePanel(
    onBack: () -> Unit,
    viewModel: SystemUIViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    BasePanelPage(
        title = stringResource(id = R.string.tile),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.android.systemui")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "tile",
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
            QsTileOneXOneSettings(viewModel = viewModel)
            QsTileTwoXOneSettings(viewModel = viewModel)
            QsTileSliderSettings(viewModel = viewModel)
            QsTileMediaSettings(viewModel = viewModel)
        }
    }
}

@Composable
private fun QsTileOneXOneSettings(viewModel: SystemUIViewModel) {
    val uiState = viewModel.uiState.value
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
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.table_rows_narrow),
                    enabled = uiState.enableCustomQsTileOneXOne,
                    title = stringResource(id = R.string.list_row_count),
                    value = uiState.qsTileOneXOneRowColumns.toFloat(),
                    steps = 7,
                    valueRange = 0f..8f,
                    onValueChange = {
                        viewModel.updateQsTileOneXOneRowColumns(columns = it.toInt())
                    }
                )
            }
        )
    )
}

@Composable
private fun QsTileTwoXOneSettings(viewModel: SystemUIViewModel) {
    val uiState = viewModel.uiState.value
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
            },
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.format_color_fill),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.fill_the_tile_state_with_tiles),
                    checked = uiState.qsTwoXOneTileFillStateFullBkg,
                    onCheckedChange = { checked ->
                        viewModel.updateQsTwoXOneTileFillStateFullBkg(value = checked)
                    }
                )
            },
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.grid_off_filled),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.hide_the_background_of_tile_icons),
                    checked = uiState.qsTwoXOneTileHideIconBkg,
                    onCheckedChange = { checked ->
                        viewModel.updateQsTwoXOneTileHideIconBkg(value = checked)
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.panels_outline),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.change_tile_icon_size),
                    description = stringResource(id = R.string.affect_entire_control_center),
                    value = uiState.qsTwoXOneTileIconSize,
                    valueRange = 0f..42f,
                    onValueChange = {
                        viewModel.updateQsTwoXOneTileIconSize(value = it)
                    }
                )
            },
            {
                var showDialog by remember { mutableStateOf(false) }
                OptionWidget(
                    painter = painterResource(id = R.drawable.opacity),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.title_color_in_inactive_state_of_tile),
                    description = String.format(
                        "#%08X",
                        uiState.qsTwoXOneTileInactiveTitleColor.and(0xFFFFFFFF.toInt())
                    ),
                    onClick = {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    ColorPickDialog(
                        painter = painterResource(id = R.drawable.opacity),
                        title = stringResource(id = R.string.title_color_in_inactive_state_of_tile),
                        description = stringResource(id = R.string.color_picker),
                        initialColor = uiState.qsTwoXOneTileInactiveTitleColor,
                        onConfirmation = {
                            viewModel.updateQsTwoXOneTileInactiveTitleColor(value = it)
                            showDialog = false
                        },
                        onDismissRequest = {
                            showDialog = false
                        }
                    )
                }
            },
            {
                var showDialog by remember { mutableStateOf(false) }
                OptionWidget(
                    painter = painterResource(id = R.drawable.opacity),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.title_color_in_active_state_of_tile),
                    description = String.format(
                        "#%08X",
                        uiState.qsTwoXOneTileActiveTitleColor.and(0xFFFFFFFF.toInt())
                    ),
                    onClick = {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    ColorPickDialog(
                        painter = painterResource(id = R.drawable.opacity),
                        title = stringResource(id = R.string.title_color_in_active_state_of_tile),
                        description = stringResource(id = R.string.color_picker),
                        initialColor = uiState.qsTwoXOneTileActiveTitleColor,
                        onConfirmation = {
                            viewModel.updateQsTwoXOneTileActiveTitleColor(value = it)
                            showDialog = false
                        },
                        onDismissRequest = {
                            showDialog = false
                        }
                    )
                }
            },
            {
                var showDialog by remember { mutableStateOf(false) }
                OptionWidget(
                    painter = painterResource(id = R.drawable.opacity),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.des_color_in_inactive_state_of_tile),
                    description = String.format(
                        "#%08X",
                        uiState.qsTwoXOneTileInactiveDesColor.and(0xFFFFFFFF.toInt())
                    ),
                    onClick = {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    ColorPickDialog(
                        painter = painterResource(id = R.drawable.opacity),
                        title = stringResource(id = R.string.des_color_in_inactive_state_of_tile),
                        description = stringResource(id = R.string.color_picker),
                        initialColor = uiState.qsTwoXOneTileInactiveDesColor,
                        onConfirmation = {
                            viewModel.updateQsTwoXOneTileInactiveDesColor(value = it)
                            showDialog = false
                        },
                        onDismissRequest = {
                            showDialog = false
                        }
                    )
                }
            },
            {
                var showDialog by remember { mutableStateOf(false) }
                OptionWidget(
                    painter = painterResource(id = R.drawable.opacity),
                    enabled = uiState.enableCustomQsResizeableTile,
                    title = stringResource(id = R.string.des_color_in_active_state_of_tile),
                    description = String.format(
                        "#%08X",
                        uiState.qsTwoXOneTileActiveDesColor.and(0xFFFFFFFF.toInt())
                    ),
                    onClick = {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    ColorPickDialog(
                        painter = painterResource(id = R.drawable.opacity),
                        title = stringResource(id = R.string.des_color_in_active_state_of_tile),
                        description = stringResource(id = R.string.color_picker),
                        initialColor = uiState.qsTwoXOneTileActiveDesColor,
                        onConfirmation = {
                            viewModel.updateQsTwoXOneTileActiveDesColor(value = it)
                            showDialog = false
                        },
                        onDismissRequest = {
                            showDialog = false
                        }
                    )
                }
            }
        )
    )
}

@Composable
private fun QsTileSliderSettings(viewModel: SystemUIViewModel) {
    val uiState = viewModel.uiState.value
    SplicedColumnGroup(
        modifier = Modifier,
        title = stringResource(id = R.string.qs_slider_tile),
        content = listOf(
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.architecture),
                    title = stringResource(id = R.string.enable_custom_settings),
                    checked = uiState.enableCustomQsSliderTile,
                    onCheckedChange = { checked ->
                        viewModel.updateEnableCustomQsSliderTile(enabled = checked)
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.rounded_corner),
                    enabled = uiState.enableCustomQsSliderTile,
                    title = stringResource(id = R.string.bkg_corner_radius),
                    value = uiState.qsSliderTileCornerRadius,
                    valueRange = 0f..96f,
                    onValueChange = {
                        viewModel.updateQsSliderTileCornerRadius(value = it)
                    }
                )
            }
        )
    )
}

@Composable
private fun QsTileMediaSettings(viewModel: SystemUIViewModel) {
    val uiState = viewModel.uiState.value
    SplicedColumnGroup(
        modifier = Modifier,
        title = stringResource(id = R.string.qs_media_tile),
        content = listOf(
            {
                SwitchWidget(
                    painter = painterResource(id = R.drawable.architecture),
                    title = stringResource(id = R.string.enable_custom_settings),
                    checked = uiState.enableCustomQsMediaTile,
                    onCheckedChange = { checked ->
                        viewModel.updateEnableCustomQsMediaTile(enabled = checked)
                    }
                )
            },
            {
                SliderWidget(
                    painter = painterResource(id = R.drawable.rounded_corner),
                    enabled = uiState.enableCustomQsMediaTile,
                    title = stringResource(id = R.string.bkg_corner_radius),
                    value = uiState.qsMediaTileCornerRadius,
                    valueRange = 0f..96f,
                    onValueChange = {
                        viewModel.updateQsMediaTileCornerRadius(value = it)
                    }
                )
            }
        )
    )
}