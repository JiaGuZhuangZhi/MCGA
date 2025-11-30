package com.gustate.mcga.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.ModuleViewModel
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun HomePage(
    modifier: Modifier,
    viewModel: ModuleViewModel
) {
    val uiState by viewModel.uiState
    Column(
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            )
    ) {
        XpStateCard(
            isModuleActive = uiState.isModuleActive,
            isRootAvailable = uiState.isRootAvailable
        )
        SplicedColumnGroup(
            modifier = Modifier,
            title = stringResource(id = R.string.app_config),
            content = listOf(
                {
                    OptionWidget(
                        painter = painterResource(id = R.drawable.family_history),
                        title = stringResource(id = R.string.root_manager),
                        description = stringResource(id = uiState.rootManagerInfo.rootManagerName),
                        onClick = {}
                    )
                },
                {
                    OptionWidget(
                        painter = painterResource(id = R.drawable.difference),
                        title = stringResource(id = R.string.root_manager_version),
                        description = uiState.rootManagerInfo.rootManagerVer,
                        onClick = {}
                    )
                }
            )
        )
    }
}

@Composable
private fun XpStateCard(
    isModuleActive: Boolean,
    isRootAvailable: Boolean
) {
    val cardShape = ContinuousRoundedRectangle(size = 24.dp)
    val onCardColor = when {
        !isModuleActive -> MaterialTheme.colorScheme.onErrorContainer
        !isRootAvailable -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceBright
    }
    val moduleActiveInfo = when {
        !isModuleActive -> stringResource(id = R.string.module_not_activated)
        !isRootAvailable -> stringResource(id = R.string.root_permission_not_configured)
        else -> stringResource(id = R.string.configured)
    }
    Row(
        modifier = Modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 16.dp
            )
            .fillMaxWidth()
            .background(
                color =
                    when {
                        !isModuleActive -> MaterialTheme.colorScheme.errorContainer
                        !isRootAvailable -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primary
                    },
                shape = cardShape
            )
            .clip(shape = cardShape)
            .padding(vertical = 18.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                id = when {
                    !isModuleActive -> R.drawable.warning_filled
                    !isRootAvailable -> R.drawable.info_filled
                    else -> R.drawable.check_circle
                }
            ),
            contentDescription = moduleActiveInfo,
            modifier = Modifier
                .size(size = 24.dp),
            tint = onCardColor
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
        ) {
            Text(
                text = moduleActiveInfo,
                style = MaterialTheme.typography.titleMedium,
                color = onCardColor
            )
            val context = LocalContext.current
            val packageInfo = context.packageManager
                .getPackageInfo(context.packageName, 0)

            Text(
                text = "v${packageInfo.versionName} (${packageInfo.longVersionCode}) - XiaoMeng",
                modifier = Modifier.padding(top = 2.dp),
                style = MaterialTheme.typography.labelMedium,
                color = onCardColor
            )
        }
    }
}