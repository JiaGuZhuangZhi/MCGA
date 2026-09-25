package com.gustate.mcga.ui.widget.appbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class TopBarState(
    density: Density,
    private val haptic: HapticFeedback,
    private val maxResistancePx: Float,
    private var lastVibrateTime: Long,
    private var resistanceAccumulator: Float,
    private val maxScrollableHeightDp: Dp,
    private val toolbarHeightDp: Dp,
    val toolbarOffsetPx: MutableFloatState
) {

    private var maxOffsetPx: Float by mutableFloatStateOf(0f)
    var appBarHeight by mutableStateOf(116.dp)
        internal set

    init {
        // 初始化最大偏移量
        maxOffsetPx = with(receiver = density) {
            maxScrollableHeightDp.roundToPx().toFloat() - toolbarHeightDp.roundToPx().toFloat()
        }
    }

    val vibrateIntervalMs = 500L // 两次震动之间的最小间隔时间（毫秒）

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            val oldOffset = toolbarOffsetPx.floatValue

            // 【场景一：向上滑动（准备折叠）】
            if (delta < 0) {
                // 判断这一帧如果执行，是不是刚好撞到“完全折叠”的边界 (-maxOffsetPx)
                if (oldOffset > -maxOffsetPx && (oldOffset + delta) <= -maxOffsetPx) {

                    // 如果阻力还没攒够
                    if (resistanceAccumulator > -maxResistancePx) {
                        // 这一帧的位移被阻力“吃掉”
                        resistanceAccumulator += delta

                        // 触发震动逻辑
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastVibrateTime > vibrateIntervalMs) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress) // 或者用更轻的 TextHandleMove / Confirm
                            lastVibrateTime = currentTime
                        }

                        // 告诉底层 Column：我把这一帧全部消费了，你别动（这就实现了“停一下”）
                        return Offset(x = 0f, y = delta)
                    }
                }

                // 阻力攒够了，或者已经过了临界点，正常让 AppBar 折叠，直到死点
                val newOffset = (oldOffset + delta).coerceIn(-maxOffsetPx, 0f)
                toolbarOffsetPx.floatValue = newOffset

                // 如果已经完全折叠了，清空阻力计数器，方便下次反向滑动
                if (newOffset == -maxOffsetPx && resistanceAccumulator != 0f) {
                    resistanceAccumulator = 0f
                }

                return Offset(x = 0f, y = newOffset - oldOffset)
            }

            // 【场景二：向下滑动（准备展开）】——如果你希望展开到头时也有同样的卡顿，逻辑镜像即可
            if (delta > 0) {
                // 如果此时 AppBar 还没折叠完（还在向上滚动的途中），优先由 AppBar 响应
                if (oldOffset < 0f) {
                    val newOffset = (oldOffset + delta).coerceIn(-maxOffsetPx, 0f)
                    toolbarOffsetPx.floatValue = newOffset
                    return Offset(x = 0f, y = newOffset - oldOffset)
                }
            }

            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val delta = available.y
            // 向下滑动，且底下的内容已经到顶了，开始下拉展开 AppBar
            if (delta > 0) {
                val oldOffset = toolbarOffsetPx.floatValue

                // 判断是不是刚好要撞到“完全展开”的边界 (0f)
                if (oldOffset < 0f && (oldOffset + delta) >= 0f) {
                    if (resistanceAccumulator < maxResistancePx) {
                        resistanceAccumulator += delta

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastVibrateTime > vibrateIntervalMs) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            lastVibrateTime = currentTime
                        }
                        return Offset(x = 0f, y = delta) // 消费掉，停一下
                    }
                }

                val newOffset = (oldOffset + delta).coerceIn(-maxOffsetPx, 0f)
                toolbarOffsetPx.floatValue = newOffset

                if (newOffset == 0f && resistanceAccumulator != 0f) {
                    resistanceAccumulator = 0f
                }
                return Offset(x = 0f, y = newOffset - oldOffset)
            }
            return Offset.Zero
        }

        /*override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val delta = available.y
            // 向下划（delta > 0），且 Column 已经到顶滚不动了（available.y 还有剩余）
            if (delta > 0) {
                val oldOffset = toolbarOffsetPx.floatValue
                val newOffset = (oldOffset + delta).coerceIn(-maxOffsetPx, 0f)
                toolbarOffsetPx.floatValue = newOffset
                return Offset(x = 0f, y = newOffset - oldOffset)
            }
            return Offset.Zero
        }*/

        /*override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            // 吸附到最近的状态
            val target = if (-toolbarOffsetPx.floatValue > maxOffsetPx / 2) -maxOffsetPx else 0f
            Animatable(toolbarOffsetPx.floatValue).animateTo(
                target,
                spring(dampingRatio = 0.8f, stiffness = 300f)
            ) {
                toolbarOffsetPx.floatValue = value
            }
            return Velocity.Zero
        }*/
    }
}

@Composable
fun rememberTopBarState(
    maxScrollableHeightDp: Dp = scrollableHeight,
    toolbarHeightDp: Dp = 56.dp
): TopBarState {
    val density = LocalDensity.current
    val to = rememberSaveable { mutableFloatStateOf(value = 0f) }
    val haptic = LocalHapticFeedback.current
    val maxResistancePx = with(LocalDensity.current) { 60.dp.toPx() }
    var lastVibrateTime by remember { mutableLongStateOf(0L) }
    var resistanceAccumulator by remember { mutableFloatStateOf(0f) }
    return remember {
        TopBarState(
            density = density,
            haptic = haptic,
            maxResistancePx = maxResistancePx,
            lastVibrateTime = lastVibrateTime,
            resistanceAccumulator = resistanceAccumulator,
            maxScrollableHeightDp = maxScrollableHeightDp,
            toolbarHeightDp = toolbarHeightDp,
            toolbarOffsetPx = to
        )
    }
}

val scrollableHeight = 116.dp