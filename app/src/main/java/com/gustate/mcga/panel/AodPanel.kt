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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.AodViewModel
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import com.gustate.mcga.utils.RootUtils
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AodPanel(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val viewModel = viewModel<AodViewModel>()
    val uiState = viewModel.uiState.value
    BasePanelPage(
        title = stringResource(id = R.string.aod),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.oplus.aod")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "aod",
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