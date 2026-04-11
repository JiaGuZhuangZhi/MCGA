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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.HomeViewModel
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.SliderWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import dev.chrisbanes.haze.hazeSource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomePanel(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val viewModel = viewModel<HomeViewModel>()
    val uiState by viewModel.uiState
    BasePanelPage(
        title = stringResource(R.string.hook_launcher),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.android.launcher")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "home",
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
                title = stringResource(id = R.string.dock_bar),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.dock_to_bottom_filled),
                            title = stringResource(id = R.string.force_dock_bkg),
                            checked = uiState.enableDockBkg,
                            onCheckedChange = { checked ->
                                viewModel.updateDockBkg(enabled = checked)
                            }
                        )
                    },
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.blur_on),
                            title = stringResource(id = R.string.force_dock_blur),
                            enabled = uiState.enableDockBkg,
                            checked = uiState.enableDockBlur,
                            onCheckedChange = { checked ->
                                viewModel.updateDockBlur(enabled = checked)
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.opacity),
                            title = stringResource(id = R.string.bkg_blur_radius),
                            enabled = uiState.enableDockBkg || uiState.enableDockBlur,
                            value = uiState.dockBlurRadius.toFloat(),
                            valueRange = 0f..1999f,
                            onValueChange = { value ->
                                viewModel.updateDockBlurRadius(value = value.roundToInt())
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.rounded_corner),
                            title = stringResource(id = R.string.bkg_corner_radius),
                            enabled = uiState.enableDockBkg || uiState.enableDockBlur,
                            value = uiState.dockCornerRadius,
                            valueRange = 0f..99f,
                            onValueChange = { value ->
                                viewModel.updateDockCornerRadius(value = value)
                            }
                        )
                    }
                )
            )
            SplicedColumnGroup(
                modifier = Modifier,
                title = stringResource(id = R.string.more),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.receipt_long_off),
                            title = stringResource(R.string.hide_drawer_app_name),
                            checked = uiState.hideDrawerName,
                            onCheckedChange = { checked ->
                                viewModel.updateHideAppName(checked)
                            }
                        )
                    },
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.family_history),
                            title = stringResource(R.string.modify_the_clear_all_apps_button),
                            checked = uiState.clearAllButton,
                            onCheckedChange = { checked ->
                                viewModel.updateClearAllButton(checked)
                            }
                        )
                    }
                )
            )
        }
    }
}