package com.gustate.mcga.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.gustate.mcga.data.viewmodel.HomeViewModel
import com.gustate.mcga.data.viewmodel.SearchViewModel
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel
) {
    val homeUiState by homeViewModel.uiState
    val searchUiState by searchViewModel.uiState
    Column(
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            ).nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        SplicedColumnGroup(
            modifier = Modifier,
            title = "系统桌面",
            content = listOf(
                {
                    SwitchWidget(
                        icon = Icons.Default.DateRange,
                        title = "强制启用 Dock 栏模糊",
                        checked = homeUiState.enableDockBlur,
                        onCheckedChange = { checked ->
                            homeViewModel.updateDockBlur(checked)
                        }
                    )
                },
                {
                    SwitchWidget(
                        icon = Icons.Default.Menu,
                        title = "隐藏“抽屉全部页”应用名称",
                        checked = homeUiState.hideDrawerName,
                        onCheckedChange = { checked ->
                            homeViewModel.updateHideAppName(checked)
                        }
                    )
                }
            )
        )
        SplicedColumnGroup(
            modifier = Modifier,
            title = "全局搜索",
            content = listOf(
                {
                    SwitchWidget(
                        icon = Icons.Default.Menu,
                        title = "隐藏“应用建议”中的应用名称",
                        checked = searchUiState.hideRecomAppName,
                        onCheckedChange = { checked ->
                            searchViewModel.updateHideRecomAppName(checked)
                        }
                    )
                },
                {
                    SwitchWidget(
                        icon = Icons.Default.Build,
                        title = "修正“应用建议”卡片的高度",
                        checked = searchUiState.fixRecomCardHeight,
                        onCheckedChange = { checked ->
                            searchViewModel.updateFixRecomCardHeight(checked)
                        }
                    )
                }
            )
        )
    }
}