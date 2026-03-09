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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.WalletViewModel
import com.gustate.mcga.ui.dialog.ColorPickDialog
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SliderWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import dev.chrisbanes.haze.hazeSource
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WalletPanel(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val viewModel = viewModel<WalletViewModel>()
    val uiState = viewModel.uiState.value
    BasePanelPage(
        title = stringResource(id = R.string.wallet),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.finshell.wallet")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "wallet",
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
                title = stringResource(id = R.string.nfc_page),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.architecture),
                            title = stringResource(id = R.string.enable_custom_settings),
                            checked = uiState.enableCustomNfcCardPage,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableCustomNfcCardPage(value = checked)
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.blur_on),
                            title = stringResource(id = R.string.bkg_blur_radius),
                            enabled = uiState.enableCustomNfcCardPage,
                            value = uiState.nfcCardPageBkgBlurRadius.toFloat(),
                            valueRange = 0f..512f,
                            onValueChange = { value ->
                                viewModel.updateNfcCardPageBkgBlurRadius(value = value.roundToInt())
                            }
                        )
                    },
                    {
                        var showDialog by remember { mutableStateOf(false) }
                        OptionWidget(
                            painter = painterResource(id = R.drawable.format_color_fill),
                            enabled = uiState.enableCustomNfcCardPage,
                            title = stringResource(id = R.string.bkg_cover_color) + "  " + stringResource(
                                id = R.string.light
                            ),
                            description = String.format(
                                "#%08X",
                                uiState.nfcCardPageBkgScrimLight.and(0xFFFFFFFF.toInt())
                            ),
                            onClick = {
                                showDialog = true
                            }
                        )
                        if (showDialog) {
                            ColorPickDialog(
                                painter = painterResource(id = R.drawable.format_color_fill),
                                title = stringResource(id = R.string.bkg_cover_color) + "  " + stringResource(
                                    id = R.string.light
                                ),
                                description = stringResource(id = R.string.color_picker),
                                initialColor = uiState.nfcCardPageBkgScrimLight,
                                onConfirmation = {
                                    viewModel.updateNfcCardPageBkgScrimLight(value = it)
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
                            painter = painterResource(id = R.drawable.format_color_fill),
                            enabled = uiState.enableCustomNfcCardPage,
                            title = stringResource(id = R.string.bkg_cover_color) + "  " + stringResource(
                                id = R.string.dark
                            ),
                            description = String.format(
                                "#%08X",
                                uiState.nfcCardPageBkgScrimDark.and(0xFFFFFFFF.toInt())
                            ),
                            onClick = {
                                showDialog = true
                            }
                        )
                        if (showDialog) {
                            ColorPickDialog(
                                painter = painterResource(id = R.drawable.format_color_fill),
                                title = stringResource(id = R.string.bkg_cover_color) + "  " + stringResource(
                                    id = R.string.dark
                                ),
                                description = stringResource(id = R.string.color_picker),
                                initialColor = uiState.nfcCardPageBkgScrimDark,
                                onConfirmation = {
                                    viewModel.updateNfcCardPageBkgScrimDark(value = it)
                                    showDialog = false
                                },
                                onDismissRequest = {
                                    showDialog = false
                                }
                            )
                        }
                    },
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.difference),
                            title = stringResource(id = R.string.enable_option_smooth_rounded_corners),
                            enabled = uiState.enableCustomNfcCardPage,
                            checked = uiState.nfcCardPageWidgetSquircle,
                            onCheckedChange = { checked ->
                                viewModel.updateNfcCardPageWidgetSquircle(value = checked)
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.opacity),
                            title = stringResource(id = R.string.option_alpha),
                            enabled = uiState.enableCustomNfcCardPage,
                            value = uiState.nfcCardPageWidgetAlpha,
                            valueRange = 0f..1f,
                            onValueChange = { value ->
                                viewModel.updateNfcCardPageWidgetAlpha(value = value)
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.rounded_corner),
                            title = stringResource(id = R.string.option_corner_radius),
                            enabled = uiState.enableCustomNfcCardPage,
                            value = uiState.nfcCardPageWidgetCornerRadius,
                            valueRange = 0f..99f,
                            onValueChange = { value ->
                                viewModel.updateNfcCardPageWidgetCornerRadius(value = value)
                            }
                        )
                    }
                )
            )
        }
    }
}