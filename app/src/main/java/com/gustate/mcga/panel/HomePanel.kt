package com.gustate.mcga.panel

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomePanel(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
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
    ) { paddingValues, scrollBehavior ->
        Column(
            modifier = Modifier
                .nestedScroll(
                    connection = scrollBehavior.nestedScrollConnection
                )
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SplicedColumnGroup(
                modifier = Modifier,
                title = stringResource(id = R.string.qs_detail_container),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.dock_to_bottom_filled),
                            title = stringResource(id = R.string.force_dock_blur),
                            checked = uiState.enableDockBlur,
                            onCheckedChange = { checked ->
                                viewModel.updateDockBlur(checked)
                            }
                        )
                    },
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.receipt_long_off),
                            title = stringResource(R.string.hide_drawer_app_name),
                            checked = uiState.hideDrawerName,
                            onCheckedChange = { checked ->
                                viewModel.updateHideAppName(checked)
                            }
                        )
                    }
                )
            )
        }
    }
}