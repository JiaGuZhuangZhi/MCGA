package com.gustate.mcga.panel

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.ColorSetting
import com.gustate.mcga.data.setting.base.CommandSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.IntSetting
import com.gustate.mcga.data.setting.base.LinkSetting
import com.gustate.mcga.data.setting.base.SettingGroup
import com.gustate.mcga.data.setting.base.SettingNode
import com.gustate.mcga.data.setting.base.SettingPage
import com.gustate.mcga.ui.dialog.ColorPickDialog
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.widget.appbar.TopAppBar
import com.gustate.mcga.ui.widget.appbar.rememberTopBarState
import com.gustate.mcga.ui.widget.option.OptionGroup
import com.gustate.mcga.ui.widget.option.RichOption
import com.gustate.mcga.ui.widget.option.SliderOption
import com.gustate.mcga.ui.widget.option.SwitchOption
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlin.math.roundToInt

@Composable
fun SettingPage.PanelRenderer(
    hazeState: HazeState,
    navController: NavHostController,
    isReturnGroup: Boolean
) {
    val viewModel = viewModel<SettingViewModel>()
    val context = LocalContext.current
    if (isReturnGroup) {
        Column() {
            val nodes = this@PanelRenderer.children
            nodes.forEach { node ->
                val node = node as SettingGroup
                OptionGroup(
                    title = stringResource(id = node.title),
                    content = if (node.dependency?.let { viewModel.getState(it).value } != false) {
                        node.children.map { setting ->
                            { shape ->
                                setting.RenderItem(
                                    viewModel = viewModel,
                                    navController = navController
                                )
                            }
                        }
                    } else emptyList()
                )
            }
        }
    } else {
        val topBarState = rememberTopBarState()
        Scaffold(
            topBar = {
                TopAppBar(
                    hazeState = hazeState,
                    title = stringResource(id = title),
                    topBarState = topBarState,
                    rightIcon = {
                        if (command != null) {
                            Icon(
                                painter = painterResource(id = R.drawable.refresh),
                                contentDescription = stringResource(id = R.string.restart_component),
                                tint = gustateColors.onBackgroundPrimary
                            )
                        }
                    },
                    onRightIconClick = {
                        command?.let { pageCommand ->
                            viewModel.executeCommand(pageCommand) { succeeded ->
                                Toast.makeText(
                                    context,
                                    context.getString(
                                        if (succeeded) R.string.command_succeeded
                                        else R.string.command_failed
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
                    .background(color = gustateColors.background)
                    .nestedScroll(topBarState.nestedScrollConnection),
                contentPadding = paddingValues
            ) {
                val nodes = this@PanelRenderer.children
                items(count = nodes.size) { index ->
                    val node = nodes[index] as SettingGroup
                    OptionGroup(
                        title = stringResource(id = node.title),
                        content = if (node.dependency?.let { viewModel.getState(it).value } != false) {
                            node.children.map { setting ->
                                { shape ->
                                    setting.RenderItem(
                                        viewModel = viewModel,
                                        navController = navController
                                    )
                                }
                            }
                        } else emptyList()
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingNode.RenderItem(
    viewModel: SettingViewModel,
    navController: NavHostController
) {

    val context = LocalContext.current

    when (this) {

        is SettingPage -> {
            val enabled = dependency?.let { viewModel.getState(it).value } ?: true
            RichOption(
                painter = painterResource(id = icon),
                title = stringResource(id = title),
                enabled = enabled,
                isNavigation = true,
                onClick = {
                    navController.navigate(route = route)
                }
            )
        }

        is LinkSetting -> {
            val enabled = dependency?.let { viewModel.getState(it).value } ?: true
            RichOption(
                painter = painterResource(id = icon),
                title = stringResource(id = title),
                description = summary?.let { stringResource(id = it) },
                enabled = enabled,
                onClick = {
                    url?.let {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = url.toUri()
                            // 确保在新任务栈打开
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }

        is CommandSetting -> {
            val enabled = dependency?.let { viewModel.getState(it).value } ?: true
            RichOption(
                painter = painterResource(id = icon),
                title = stringResource(id = title),
                description = summary?.let { stringResource(id = it) },
                enabled = enabled,
                onClick = {
                    viewModel.executeCommand(command) { succeeded ->
                        Toast.makeText(
                            context,
                            context.getString(
                                if (succeeded) R.string.command_succeeded
                                else R.string.command_failed
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

        is BooleanSetting -> {
            val enabled = dependency?.let { viewModel.getState(it).value } ?: true
            val state = viewModel.getState(setting = this)
            SwitchOption(
                checked = state.value,
                painter = painterResource(id = icon),
                title = stringResource(id = title),
                description = summary?.let { stringResource(id = it) },
                enabled = enabled,
                onCheckedChange = { checked -> viewModel.updateSetting(this, checked) }
            )
        }

        is ColorSetting -> {
            val enabled = dependency?.let { viewModel.getState(it).value } ?: true
            val state = viewModel.getState(setting = this)
            var showColorPicker by remember(this) { mutableStateOf(false) }
            if (showColorPicker) {
                ColorPickDialog(
                    painter = painterResource(id = icon),
                    painterDescription = stringResource(id = title),
                    title = stringResource(id = title),
                    description = summary?.let { stringResource(id = it) } ?: "",
                    initialColor = state.value,
                    onDismissRequest = { showColorPicker = false },
                    onConfirmation = { color ->
                        viewModel.updateSetting(this, color)
                        showColorPicker = false
                    }
                )
            }
            RichOption(
                painter = painterResource(id = icon),
                title = stringResource(id = title),
                description = summary?.let { stringResource(id = it) },
                rightText = "#${state.value.toUInt().toString(16).uppercase().padStart(8, '0')}",
                enabled = enabled,
                onClick = { showColorPicker = true }
            )
        }

        is FloatSetting -> {
            val enabled = dependency?.let { viewModel.getState(it).value } ?: true
            val state = viewModel.getState(setting = this)
            SliderOption(
                painter = painterResource(id = icon),
                title = stringResource(id = title),
                description = summary?.let { stringResource(id = it) },
                value = state.value,
                valueRange = this.min..this.max,
                enabled = enabled,
                onValueChange = { viewModel.updateSetting(this, it) }
            )
        }

        is IntSetting -> {
            val enabled = dependency?.let { viewModel.getState(it).value } ?: true
            val state = viewModel.getState(setting = this)
            SliderOption(
                painter = painterResource(id = icon),
                title = stringResource(id = title),
                description = summary?.let { stringResource(id = it) },
                value = state.value.toFloat(),
                valueRange = this.min.toFloat()..this.max.toFloat(),
                valueLabel = state.value.toString(),
                enabled = enabled,
                onValueChange = { viewModel.updateSetting(this, it.roundToInt()) }
            )
        }

        else -> {
            // TODO: 未知类
        }
    }
}
