/*package com.gustate.mcga.data.setting.base

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
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SliderWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import dev.chrisbanes.haze.hazeSource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SettingRenderer(
    page: SettingPage,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    BasePanelPage(
        title = stringResource(page.title),
        onBackClick = onBack,
        onRestartClick = page.command?.let { command ->
            {
                try {
                    Runtime.getRuntime().exec(command)
                } catch (_: Exception) {
                }
            }
        } ?: {},
        sharedKey = page.title.toString(),
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    ) { paddingValues, scrollBehavior, hazeState ->
        Column(
            modifier = modifier
                .hazeSource(state = hazeState)
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            page.children.forEach { child ->
                renderSettingNode(child)
            }
        }
    }
}

@Composable
private fun RenderSettingNode(node: SettingNode) {
    when (node) {
        is SettingGroup -> {
            SplicedColumnGroup(
                title = stringResource(node.title),
                content = node.children.map { child ->
                    {
                        RenderSettingNode(child)
                    }
                }
            )
        }

        is BooleanSetting -> {
            SwitchWidget(
                painter = painterResource(node.icon),
                title = stringResource(node.title),
                checked = node.getValue(),
                onCheckedChange = { node.setValue(it) },
                enabled = node.dependency?.getValue() ?: true,
            )
        }

        is FloatSetting -> {
            SliderWidget(
                painter = painterResource(node.icon),
                title = stringResource(node.title),
                value = node.getValue(),
                valueRange = node.min..node.max,
                onValueChange = { node.setValue(it) },
                enabled = node.dependency?.getValue() ?: true
            )
        }

        is IntSetting -> {
            SliderWidget(
                painter = painterResource(node.icon),
                title = stringResource(node.title),
                value = node.getValue().toFloat(),
                valueRange = node.min.toFloat()..node.max.toFloat(),
                onValueChange = { node.setValue(it.roundToInt()) },
                enabled = node.dependency?.getValue() ?: true
            )
        }

        is ColorSetting -> {
            OptionWidget(
                painter = painterResource(node.icon),
                title = stringResource(node.title),
                description = node.getSummaryText(),
                enabled = node.dependency?.getValue() ?: true,
                onClick = { /* TODO: show color picker */ }
            )
        }

        is CommandSetting -> {
            OptionWidget(
                painter = painterResource(node.icon),
                title = stringResource(node.title),
                description = node.summary ?: "",
                enabled = node.dependency?.getValue() ?: true,
                onClick = { node.command?.let { Runtime.getRuntime().exec(it) } }
            )
        }

        else -> {}
    }
}*/