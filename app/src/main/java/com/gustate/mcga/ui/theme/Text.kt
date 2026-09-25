package com.gustate.mcga.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun collapsedHeaderTextStyle() = TextStyle(
    color = gustateColors.onBackgroundPrimary,
    fontSize = 18.sp,
    fontWeight = FontWeight.W600
)

@Composable
fun expandedHeaderTextStyle() = TextStyle(
    color = gustateColors.onBackgroundPrimary,
    fontSize = 28.sp,
    fontWeight = FontWeight.W800
)

@Composable
fun buttonBasicTextStyle() = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.W600
)

@Composable
fun inputTextStyle() = TextStyle(
    color = gustateColors.onContainerPrimary,
    fontSize = 16.sp,
    fontWeight = FontWeight.W600
)

@Composable
fun describeTextStyle() = TextStyle(
    color = gustateColors.onBackgroundSecondary,
    fontSize = 12.sp,
    fontWeight = FontWeight.W400
)

@Composable
fun optionGroupTitleTextStyle() = TextStyle(
    color = gustateColors.onBackgroundSubtitle,
    fontSize = 12.sp,
    fontWeight = FontWeight.W400
)

@Composable
fun optionTitleTextStyle() = TextStyle(
    color = gustateColors.onContainerPrimary,
    fontSize = 18.sp,
    fontWeight = FontWeight.W600
)

@Composable
fun optionDescriptionTextStyle() = TextStyle(
    color = gustateColors.onContainerSecondary,
    fontSize = 14.sp,
    fontWeight = FontWeight.W500
)