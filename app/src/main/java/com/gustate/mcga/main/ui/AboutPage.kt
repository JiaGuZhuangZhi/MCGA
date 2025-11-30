package com.gustate.mcga.main.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.gustate.mcga.R
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    modifier: Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            )
    ) {
        SplicedColumnGroup(
            modifier = Modifier,
            title = stringResource(id = R.string.app_name),
            content = listOf(
                {
                    val githubLink = stringResource(id = R.string.github_link)
                    OptionWidget(
                        painter = painterResource(id = R.drawable.grid_off_filled),
                        title = stringResource(id = R.string.open_source_address),
                        description = githubLink,
                        onClick = {
                            openUrlInBrowser(
                                context = context,
                                url = githubLink
                            )
                        }
                    )
                },
                {
                    val coolapkLink = "http://www.coolapk.com/u/19175527"
                    OptionWidget(
                        painter = painterResource(id = R.drawable.dock_to_bottom_filled),
                        title = stringResource(id = R.string.xiaomeng),
                        description = coolapkLink,
                        onClick = {
                            openUrlInBrowser(
                                context = context,
                                url = coolapkLink
                            )
                        }
                    )
                }
            )
        )
        SplicedColumnGroup(
            modifier = Modifier,
            title = stringResource(id = R.string.group),
            content = listOf(
                {
                    val qqLink = "https://qm.qq.com/q/yPA0nIaF2g"
                    OptionWidget(
                        painter = painterResource(id = R.drawable.qq),
                        title = stringResource(id = R.string.qq_group),
                        description = qqLink,
                        onClick = {
                            openUrlInBrowser(
                                context = context,
                                url = qqLink
                            )
                        }
                    )
                },
                {
                    val telegramLink = "https://t.me/+eYK0laXMEiMxMjc1"
                    OptionWidget(
                        painter = painterResource(id = R.drawable.telegram),
                        title = stringResource(id = R.string.telegram_group),
                        description = telegramLink,
                        onClick = {
                            openUrlInBrowser(
                                context = context,
                                url = telegramLink
                            )
                        }
                    )
                },
                {
                    val telegramLink = "https://t.me/+1FHZSKBrZrU3MTg1"
                    OptionWidget(
                        painter = painterResource(id = R.drawable.telegram),
                        title = stringResource(id = R.string.telegram_group_Xiaomeng),
                        description = telegramLink,
                        onClick = {
                            openUrlInBrowser(
                                context = context,
                                url = telegramLink
                            )
                        }
                    )
                }
            )
        )
    }
}

private fun openUrlInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = url.toUri()
        // 确保在新任务栈打开
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}