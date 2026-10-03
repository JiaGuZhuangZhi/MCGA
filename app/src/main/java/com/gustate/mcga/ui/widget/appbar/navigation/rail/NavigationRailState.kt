package com.gustate.mcga.ui.widget.appbar.navigation.rail/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*package com.gustate.mcga.ui.weight.appbar.navigation.rail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density

/** [WideNavigationRailState] 的可能取值 */
enum class WideNavigationRailValue {
    /** 导航栏处于折叠状态 */
    Collapsed,

    /** 导航栏处于展开状态 */
    Expanded
}

/**
 * 可被提升用于观察宽导航栏状态的状态对象 允许将导航栏设置为折叠或展开
 *
 * @see rememberWideNavigationRailState 构建默认实现
 */
interface WideNavigationRailState {
    /** 当前状态是否正在执行动画 */
    val isAnimating: Boolean

    /** 导航栏目标是否为展开状态 */
    val targetValue: WideNavigationRailValue

    /** 导航栏当前是否为展开状态 */
    val currentValue: WideNavigationRailValue

    /** 以动画方式展开导航栏 并挂起直到完全展开 */
    suspend fun expand()

    /** 以动画方式折叠导航栏 并挂起直到完全折叠 */
    suspend fun collapse()

    /** 若当前为展开则折叠 若当前为折叠则展开 并挂起直到切换完成 */
    suspend fun toggle()

    /**
     * 无动画地直接设置状态 并挂起直到设置完成
     *
     * @param targetValue 要设置的目标状态
     */
    suspend fun snapTo(targetValue: WideNavigationRailValue)
}

/** 创建并 [rememberSaveable] 一个 [WideNavigationRailState] */
@Composable
fun rememberWideNavigationRailState(
    initialValue: WideNavigationRailValue = WideNavigationRailValue.Collapsed
): WideNavigationRailState {
    // TODO 从组件 tokens 文件中加载 motionScheme tokens
    val animationSpec = MotionSchemeKeyTokens.DefaultSpatial.value<Float>()
    return rememberSaveable(saver = WideNavigationRailStateImpl.Saver(animationSpec)) {
        WideNavigationRailStateImpl(initialValue = initialValue, animationSpec = animationSpec)
    }
}

internal val WideNavigationRailValue.isExpanded
    get() = this == WideNavigationRailValue.Expanded

internal operator fun WideNavigationRailValue.not(): WideNavigationRailValue {
    return if (this == WideNavigationRailValue.Collapsed) {
        WideNavigationRailValue.Expanded
    } else {
        WideNavigationRailValue.Collapsed
    }
}

internal class WideNavigationRailStateImpl(
    var initialValue: WideNavigationRailValue,
    private val animationSpec: AnimationSpec<Float>,
) : WideNavigationRailState {

    private val internalValue = if (initialValue.isExpanded) Expanded else Collapsed
    private val internalState = Animatable(internalValue, Float.VectorConverter)
    private val _currentVal = derivedStateOf {
        if (internalState.value == Expanded) {
            WideNavigationRailValue.Expanded
        } else {
            WideNavigationRailValue.Collapsed
        }
    }

    override val isAnimating: Boolean
        get() = internalState.isRunning

    override val targetValue: WideNavigationRailValue
        get() =
            if (internalState.targetValue == Expanded) {
                WideNavigationRailValue.Expanded
            } else {
                WideNavigationRailValue.Collapsed
            }

    override val currentValue: WideNavigationRailValue
        get() = _currentVal.value

    override suspend fun expand() {
        internalState.animateTo(targetValue = Expanded, animationSpec = animationSpec)
    }

    override suspend fun collapse() {
        internalState.animateTo(targetValue = Collapsed, animationSpec = animationSpec)
    }

    override suspend fun toggle() {
        internalState.animateTo(
            targetValue = if (targetValue.isExpanded) Collapsed else Expanded,
            animationSpec = animationSpec,
        )
    }

    override suspend fun snapTo(targetValue: WideNavigationRailValue) {
        val target = if (targetValue.isExpanded) Expanded else Collapsed
        internalState.snapTo(targetValue = target)
    }

    companion object {
        private const val Collapsed = 0f
        private const val Expanded = 1f

        /** [WideNavigationRailState] 的默认 [Saver] 实现 */
        fun Saver(animationSpec: AnimationSpec<Float>) =
            Saver<WideNavigationRailState, WideNavigationRailValue>(
                save = { it.targetValue },
                restore = { WideNavigationRailStateImpl(it, animationSpec) },
            )
    }
}

internal class ModalWideNavigationRailState(
    state: WideNavigationRailState,
    val density: Density,
    val animationSpec: AnimationSpec<Float>,
) : WideNavigationRailState by state {

    /**
     * 初始化
     * 现在的构造函数只需要 initialValue。
     * confirmValueChange 已被移除, 逻辑应由外部 anchors 控制
     */
    internal val anchoredDraggableState: AnchoredDraggableState<WideNavigationRailValue> =
        AnchoredDraggableState(
            initialValue = state.targetValue
        )

    override val currentValue: WideNavigationRailValue
        get() = anchoredDraggableState.currentValue

    override val targetValue: WideNavigationRailValue
        get() = anchoredDraggableState.targetValue

    override val isAnimating: Boolean
        get() = anchoredDraggableState.isAnimationRunning

    override suspend fun expand() = animateTo(WideNavigationRailValue.Expanded)

    override suspend fun collapse() = animateTo(WideNavigationRailValue.Collapsed)

    override suspend fun toggle() {
        val target = if (targetValue == WideNavigationRailValue.Collapsed)
            WideNavigationRailValue.Expanded else WideNavigationRailValue.Collapsed
        animateTo(target)
    }

    override suspend fun snapTo(targetValue: WideNavigationRailValue) {
        anchoredDraggableState.snapTo(targetValue)
    }

    val currentOffset: Float
        get() = anchoredDraggableState.offset

    /**
     * 简化 animateTo
     * 直接使用扩展方法，不再需要手动计算 positionOf。
     */
    private suspend fun animateTo(
        targetValue: WideNavigationRailValue,
        spec: AnimationSpec<Float> = this.animationSpec,
    ) {
        anchoredDraggableState.animateTo(
            targetValue = targetValue,
            animationSpec = spec
        )
    }
}

@Stable
internal class RailPredictiveBackState {
    var swipeEdgeMatchesRail by mutableStateOf(true)

    fun update(isSwipeEdgeLeft: Boolean, isRtl: Boolean) {
        swipeEdgeMatchesRail = (isSwipeEdgeLeft && !isRtl) || (!isSwipeEdgeLeft && isRtl)
    }
}*/