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
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ClockPanel(
    onBack: () -> Unit,
    viewModel: SystemUIViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.value
    BasePanelPage(
        title = stringResource(id = R.string.clock),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.android.systemui")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "notification_clock",
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
                title = stringResource(id = R.string.notification_center_clock),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.architecture),
                            title = stringResource(id = R.string.enable_custom_settings),
                            checked = uiState.enableCustomNotificationClock,
                            onCheckedChange = { checked ->
                                viewModel.updateEnableCustomNotificationClock(value = checked)
                            }
                        )
                    }
                )
            )
        }
    }
}

