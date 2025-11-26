package com.gustate.mcga.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    isHomePage: Boolean = false,
    isBackAvailable: Boolean = false,
    onBackClick: () -> Unit = {},
    isRestartAvailable: Boolean = false,
    onRestartClick: () -> Unit = {}
) {
    LargeTopAppBar(
        title = title,
        modifier = modifier.padding(start = 6.dp),
        navigationIcon = {
            if (isHomePage) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(size = 48.dp)
                )
            }
            if (isBackAvailable) {
                IconButton(
                    onClick = {
                        onBackClick()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "返回"
                    )
                }
            }
        },
        actions = {
            if (isRestartAvailable) {
                IconButton(
                    onClick = {
                        onRestartClick()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.refresh),
                        contentDescription = "重启作用域"
                    )
                }
            }
        },
        expandedHeight = 128.dp,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        scrollBehavior = scrollBehavior
    )
}