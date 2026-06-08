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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.SystemUIViewModel
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.SliderWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun NotificationPanel(
    onBack: () -> Unit,
    viewModel: SystemUIViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.value
    BasePanelPage(
        title = stringResource(id = R.string.notification),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.android.systemui")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "notification",
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
                title = stringResource(id = R.string.notification),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.architecture),
                            title = stringResource(id = R.string.enable_custom_settings),
                            checked = uiState.enableCustomNotification,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableCustomNotification(value = checked)
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.rounded_corner),
                            enabled = uiState.enableCustomNotification,
                            title = stringResource(id = R.string.bkg_corner_radius),
                            value = uiState.notificationBkgCornerRadius,
                            valueRange = 0f..96f,
                            onValueChange = {
                                viewModel.updateNotificationBkgCornerRadius(value = it)
                            }
                        )
                    },
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.format_color_fill),
                            enabled = uiState.enableCustomNotification,
                            title = stringResource(id = R.string.enable_textured_glossy_material),
                            checked = uiState.notificationBkgAddHighlight,
                            onCheckedChange = {
                                viewModel.updateNotificationBkgAddHighlight(value = it)
                            }
                        )
                    },
                    {
                        SliderWidget(
                            painter = painterResource(id = R.drawable.table_rows_narrow),
                            enabled = uiState.enableCustomNotification ||
                                    uiState.notificationBkgAddHighlight,
                            title = stringResource(id = R.string.thickness),
                            value = uiState.notificationBkgHighlightThickness.toFloat(),
                            valueRange = 0f..24f,
                            onValueChange = {
                                viewModel.updateNotificationBkgHighlightThickness(value = it.toInt())
                            }
                        )
                    }
                )
            )
        }
    }
}

