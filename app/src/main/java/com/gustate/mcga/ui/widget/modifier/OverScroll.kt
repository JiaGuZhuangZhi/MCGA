package com.gustate.mcga.ui.widget.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * 抛物线滚动缓动曲线
 *
 * 当向相同方向滚动时，距离0点越远，"阻力"越大；距离0点越近，"阻力"越小；
 * 当滚动方向与当前存在的overscroll偏移方向相反时，不施加拖拽效果
 *
 * 注意：当[p] = 50f时，其表现应与iOS一致
 * @param currentOffset 当前越界的偏移值
 * @param newOffset 新的滚动偏移
 * @param p 抛物线曲线计算的关键参数
 * @param density 如果没有这个参数，偏移量的单位是像素，
 * 所以我们需要这个变量在不同设备上有相同的预期效果
 */
@Stable
fun parabolaScrollEasing(
    currentOffset: Float,
    newOffset: Float,
    p: Float = 50f,
    density: Float = 4f
): Float {
    // 将p乘以密度进行归一化处理，保证不同屏幕密度下效果一致
    val realP = p * density
    // p / sqrt(p * |当前偏移+新偏移/2|)
    // 使用sqrt开方实现非线性阻尼，离0越远阻力越大（曲线越平缓）
    val ratio =
        (realP / (sqrt(
            x = realP * abs(x = currentOffset + newOffset / 2)
                .coerceAtLeast(minimumValue = Float.MIN_VALUE)
        ))).coerceIn(Float.MIN_VALUE, 1f)

    // 如果当前偏移和新偏移方向相同，应用阻尼效果（偏移量被ratio缩放）
    // 如果方向相反，直接叠加（快速复位）
    return if (sign(x = currentOffset) == sign(x = newOffset)) {
        currentOffset + newOffset * ratio
    } else {
        currentOffset + newOffset
    }
}

/**
 * 线性滚动缓动，直接相加，无阻尼效果
 */
val LinearScrollEasing: (currentOffset: Float, newOffset: Float) -> Float =
    { currentOffset, newOffset -> currentOffset + newOffset }

// 默认的抛物线滚动缓动，使用当前设备的屏幕密度
internal val DefaultParabolaScrollEasing: (currentOffset: Float, newOffset: Float) -> Float
    @Composable
    get() {
        val density = LocalDensity.current.density
        return { currentOffset, newOffset ->
            parabolaScrollEasing(currentOffset, newOffset, density = density)
        }
    }

// 弹簧动画的默认参数
internal const val OutBoundSpringStiff = 150f  // 刚度：决定回复力的强弱
internal const val OutBoundSpringDamp = 0.86f   // 阻尼：决定振荡衰减的速度

/**
 * 垂直方向的越界滚动修饰符
 * @see overScrollOutOfBound
 */
fun Modifier.overScrollVertical(
    nestedScrollToParent: Boolean = true,  // 是否将嵌套滚动事件分发给父级
    scrollEasing: ((currentOffset: Float, newOffset: Float) -> Float)? = null,  // 滚动缓动函数
    springStiff: Float = OutBoundSpringStiff,  // 弹簧刚度
    springDamp: Float = OutBoundSpringDamp,    // 弹簧阻尼
): Modifier = overScrollOutOfBound(
    isVertical = true,
    nestedScrollToParent,
    scrollEasing,
    springStiff,
    springDamp
)

/**
 * 水平方向的越界滚动修饰符
 * @see overScrollOutOfBound
 */
fun Modifier.overScrollHorizontal(
    nestedScrollToParent: Boolean = true,
    scrollEasing: ((currentOffset: Float, newOffset: Float) -> Float)? = null,
    springStiff: Float = OutBoundSpringStiff,
    springDamp: Float = OutBoundSpringDamp,
): Modifier = overScrollOutOfBound(
    isVertical = false,
    nestedScrollToParent,
    scrollEasing,
    springStiff,
    springDamp
)

/**
 * 为可滚动组件提供越界弹性滚动效果（iOS风格）
 *
 * - 你应当在具有类似语义的 Modifier 之前调用它，以确保嵌套滚动正常工作
 * - 你应该配合[rememberOverscrollFlingBehavior]一起使用
 * @Author: cormor
 * @Email: cangtiansuo@gmail.com
 * @param isVertical 是否是垂直滚动
 * @param nestedScrollToParent 是否将嵌套滚动事件分发给父级
 * @param scrollEasing 可参考[DefaultParabolaScrollEasing]，入参为当前越界偏移和新手势偏移
 *                      通过修改此函数配合[springStiff]自定义滑动阻尼效果
 *                      当前默认阻尼效果来自iOS，一般不需要修改！
 * @param springStiff 越界效果的弹簧刚度，为获得更好的用户体验
 *                    不建议高于[androidx.compose.animation.core.Spring.StiffnessMediumLow]
 * @param springDamp 越界效果的弹簧阻尼，一般不需要设置
 */
@Suppress("NAME_SHADOWING")
fun Modifier.overScrollOutOfBound(
    isVertical: Boolean = true,
    nestedScrollToParent: Boolean = true,
    scrollEasing: ((currentOffset: Float, newOffset: Float) -> Float)?,
    springStiff: Float = OutBoundSpringStiff,
    springDamp: Float = OutBoundSpringDamp,
): Modifier = composed {
    // 使用rememberUpdatedState确保在重组时获取最新的值
    val nestedScrollToParent by rememberUpdatedState(newValue = nestedScrollToParent)
    val scrollEasing by rememberUpdatedState(newValue = scrollEasing ?: DefaultParabolaScrollEasing)
    val springStiff by rememberUpdatedState(newValue = springStiff)
    val springDamp by rememberUpdatedState(newValue = springDamp)
    val isVertical by rememberUpdatedState(newValue = isVertical)

    // 嵌套滚动调度器，用于在父子组件间传递滚动事件
    val dispatcher = remember { NestedScrollDispatcher() }
    // 当前越界偏移量，使用可变状态，变化时会触发重组更新UI
    var offset by remember { mutableFloatStateOf(value = 0f) }

    // 嵌套滚动连接器，核心逻辑所在
    val nestedConnection = remember {
        object : NestedScrollConnection {
            /**
             * 可见性阈值，当偏移量小于此值时，我们认为动画已结束
             */
            val visibilityThreshold = 0.5f

            // 上一次滑翔动画的动画器，用于控制动画和停止
            lateinit var lastFlingAnimator: Animatable<Float, AnimationVector1D>

            /**
             * 前置滚动处理 - 在子组件消耗滚动之前调用
             * 处理拖拽时的越界阻尼效果
             */
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 只处理拖拽事件，滑翔事件由onPreFling处理
                if (source != NestedScrollSource.UserInput) {
                    return dispatcher.dispatchPreScroll(available, source)
                }

                // 如果正在执行滑翔动画，立即停止
                if (::lastFlingAnimator.isInitialized && lastFlingAnimator.isRunning) {
                    dispatcher.coroutineScope.launch {
                        lastFlingAnimator.stop()
                    }
                }

                // 如果需要分发给父级，先让父级消费一部分滚动
                val realAvailable = when {
                    nestedScrollToParent -> available - dispatcher.dispatchPreScroll(
                        available,
                        source
                    )

                    else -> available
                }
                // 获取当前方向的实际滚动偏移
                val realOffset = if (isVertical) realAvailable.y else realAvailable.x

                // 判断滚动方向是否与当前越界方向相同
                val isSameDirection = sign(realOffset) == sign(offset)

                // 如果越界偏移接近0，或者方向相同，直接让父级正常处理
                if (abs(offset) <= visibilityThreshold || isSameDirection) {
                    return available - realAvailable
                }

                // 应用缓动函数计算新的越界偏移
                val offsetAtLast = scrollEasing(offset, realOffset)

                // 如果方向反转（从正变负或从负变正），需要复位到0并开始正常滚动
                return if (sign(offset) != sign(offsetAtLast)) {
                    offset = 0f  // 复位越界偏移
                    // 返回实际被消耗的滚动量，让父级处理剩余的滚动
                    if (isVertical) {
                        Offset(
                            x = available.x - realAvailable.x,
                            y = available.y - realAvailable.y + realOffset
                        )
                    } else {
                        Offset(
                            x = available.x - realAvailable.x + realOffset,
                            y = available.y - realAvailable.y
                        )
                    }
                } else {
                    // 方向未反转，更新越界偏移
                    offset = offsetAtLast
                    // 消耗了全部滚动，没有剩余传递给父级
                    if (isVertical) {
                        Offset(x = available.x - realAvailable.x, y = available.y)
                    } else {
                        Offset(x = available.x, y = available.y - realAvailable.y)
                    }
                }
            }

            /**
             * 后置滚动处理 - 在子组件消耗滚动之后调用
             * 处理拖拽结束时超出边界的偏移累积
             */
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 只处理拖拽事件
                if (source != NestedScrollSource.UserInput) {
                    return dispatcher.dispatchPreScroll(available, source)
                }

                // 处理父级嵌套滚动
                val realAvailable = when {
                    nestedScrollToParent -> available - dispatcher.dispatchPostScroll(
                        consumed,
                        available,
                        source
                    )

                    else -> available
                }

                // 将未被消耗的滚动量加到越界偏移中（产生弹性拉伸效果）
                offset = scrollEasing(offset, if (isVertical) realAvailable.y else realAvailable.x)

                // 返回实际消耗的滚动量
                return if (isVertical) {
                    Offset(x = available.x - realAvailable.x, y = available.y)
                } else {
                    Offset(x = available.x, y = available.y - realAvailable.y)
                }
            }

            /**
             * 前置滑翔处理 - 在滑翔动画开始前调用
             * 处理越界时的快速复位动画
             */
            override suspend fun onPreFling(available: Velocity): Velocity {
                // 停止正在进行的滑翔动画
                if (::lastFlingAnimator.isInitialized && lastFlingAnimator.isRunning) {
                    lastFlingAnimator.stop()
                }

                // 处理父级嵌套滑翔
                val parentConsumed = when {
                    nestedScrollToParent -> dispatcher.dispatchPreFling(available)
                    else -> Velocity.Zero
                }
                val realAvailable = available - parentConsumed
                var leftVelocity = if (isVertical) realAvailable.y else realAvailable.x

                // 如果存在越界偏移，并且滑翔方向与越界偏移方向相反（向边界外回弹）
                if (abs(offset) >= visibilityThreshold && sign(leftVelocity) != sign(offset)) {
                    // 创建动画器，设置边界（只能回弹到0，不能过冲）
                    lastFlingAnimator = Animatable(offset).apply {
                        when {
                            leftVelocity < 0 -> updateBounds(lowerBound = 0f)
                            leftVelocity > 0 -> updateBounds(upperBound = 0f)
                        }
                    }
                    // 执行弹簧动画复位到0
                    leftVelocity = lastFlingAnimator.animateTo(
                        0f,
                        spring(springDamp, springStiff, visibilityThreshold),
                        leftVelocity
                    ) {
                        // 每帧更新offset，触发UI更新
                        offset = scrollEasing(offset, value - offset)
                    }.endState.velocity
                }

                // 返回剩余的滑翔速度
                return if (isVertical) {
                    Velocity(parentConsumed.x, y = available.y - leftVelocity)
                } else {
                    Velocity(available.x - leftVelocity, y = parentConsumed.y)
                }
            }

            /**
             * 后置滑翔处理 - 在滑翔动画结束后调用
             * 处理滑翔结束后的回弹动画
             */
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // 处理父级嵌套滑翔
                val realAvailable = when {
                    nestedScrollToParent -> available - dispatcher.dispatchPostFling(
                        consumed,
                        available
                    )

                    else -> available
                }

                // 创建动画器，执行回弹动画到0
                lastFlingAnimator = Animatable(offset)
                lastFlingAnimator.animateTo(
                    0f,
                    spring(springDamp, springStiff, visibilityThreshold),
                    if (isVertical) realAvailable.y else realAvailable.x
                ) {
                    offset = scrollEasing(offset, value - offset)
                }

                // 返回未被消耗的滑翔速度
                return if (isVertical) {
                    Velocity(x = available.x - realAvailable.x, y = available.y)
                } else {
                    Velocity(x = available.x, y = available.y - realAvailable.y)
                }
            }
        }
    }

    // 组合修饰符链
    this
        .clipToBounds()  // 裁剪超出边界的部分，防止越界UI穿透
        .nestedScroll(nestedConnection, dispatcher)  // 注册嵌套滚动连接器
        .graphicsLayer {
            // 根据越界偏移平移UI，产生弹性拉伸效果
            if (isVertical) translationY = offset else translationX = offset
        }
}

/**
 * 记住越界滚动所需的滑翔行为
 * 应该配合[overScrollVertical]使用
 * @param decaySpec 衰减规范，可以使用 rememberSplineBasedDecay 替代
 * @param getScrollState 传入你的[ScrollableState]，对于 LazyColumn/LazyRow，它是 LazyListState
 */
@Composable
fun rememberOverscrollFlingBehavior(
    decaySpec: DecayAnimationSpec<Float> = exponentialDecay(),
    getScrollState: () -> ScrollableState,
): FlingBehavior = remember(decaySpec, getScrollState) {
    object : FlingBehavior {
        /**
         * 速度是否无法被消耗的检测扩展属性
         * - 我们应当在滑翔的每一帧检查它
         * - 当返回true时应立即停止滑翔并返回剩余速度
         * - 如果没有这个检测，scrollBy()会持续消耗速度，导致嵌套滚动中的速度误差
         */
        private val Float.canNotBeConsumed: Boolean
            get() {
                val state = getScrollState()
                // 如果速度为负（向上/向左）但组件不能向后滚动，或速度为正但不能向前滚动，则无法消耗
                return !(this < 0 && state.canScrollBackward || this > 0 && state.canScrollForward)
            }

        /**
         * 执行滑翔动画
         * 重写FlingBehavior接口的方法，实现带有边界检测的滑翔
         */
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            // 如果初始速度无法被消耗（已到达边界），直接返回
            if (initialVelocity.canNotBeConsumed) {
                return initialVelocity
            }

            // 只有速度大于1时才执行动画，避免微小的速度抖动
            return if (abs(initialVelocity) > 1f) {
                var velocityLeft = initialVelocity
                var lastValue = 0f

                // 创建动画状态并执行衰减动画
                AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity,
                ).animateDecay(decaySpec) {
                    // 计算本帧需要滚动的距离
                    val delta = value - lastValue
                    // 执行实际滚动，获取被消耗的距离
                    val consumed = scrollBy(delta)
                    lastValue = value
                    velocityLeft = this.velocity

                    // 如果存在未消耗的滚动量（到达边界），或者剩余速度无法被消耗，取消动画
                    if (abs(delta - consumed) > 0.5f || velocityLeft.canNotBeConsumed) {
                        cancelAnimation()
                    }
                }
                velocityLeft
            } else {
                initialVelocity
            }
        }
    }
}