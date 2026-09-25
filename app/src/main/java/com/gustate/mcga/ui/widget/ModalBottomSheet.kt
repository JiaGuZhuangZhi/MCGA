package com.gustate.mcga.ui.widget

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    sheetGesturesEnabled: Boolean = true,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(backgroundColor = containerColor),
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable ColumnScope.() -> Unit,
) {

    val minAnchorState = remember { mutableStateOf(value = 0f) }
    val maxAnchorState = remember { mutableStateOf(value = 0f) }

    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {

        /*Scrim(
            color = scrimColor,
            onDismissRequest = {
                scope
                    .launch {
                        sheetState.hide()
                    }
                    .invokeOnCompletion {
                        onDismissRequest()
                    }
            },
            visible = sheetState.targetValue != SheetValue.Hidden,
            dismissEnabled = true
        )*/

        Surface(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = sheetMaxWidth)
                .fillMaxWidth()
                .then(
                    if (sheetGesturesEnabled)
                        Modifier.nestedScroll(connection = nestedScroll(sheetState))
                    else Modifier
                )
                .draggableAnchors(
                    sheetState.anchoredDraggableState,
                    Orientation.Vertical
                ) { sheetSize, constraints ->

                    val fullHeight = constraints.maxHeight.toFloat()

                    val anchors = DraggableAnchors {
                        Hidden at fullHeight
                        if (
                            sheetSize.height > fullHeight / 2 &&
                            !sheetState.skipPartiallyExpanded
                        ) {
                            PartiallyExpanded at fullHeight / 2f
                        }
                        if (sheetSize.height != 0) {
                            Expanded at max(0f, fullHeight - sheetSize.height)
                        }
                    }

                    val target =
                        when (sheetState.targetValue) {
                            Hidden -> Hidden
                            PartiallyExpanded ->
                                if (anchors.hasAnchorFor(PartiallyExpanded))
                                    PartiallyExpanded
                                else Expanded
                            Expanded ->
                                if (anchors.hasAnchorFor(Expanded))
                                    Expanded
                                else Hidden
                        }

                    anchors to target
                }
                .draggable(
                    state = sheetState.anchoredDraggableState.draggableState,
                    orientation = Orientation.Vertical,
                    enabled = sheetGesturesEnabled && sheetState.isVisible,
                    startDragImmediately = sheetState.isAnimationRunning,
                    onDragStopped = { velocity ->
                        scope.launch {
                            sheetState.settle(velocity)
                            if (!sheetState.isVisible) {
                                onDismissRequest()
                            }
                        }
                    }
                ),
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(contentWindowInsets())
            ) {
                if (dragHandle != null) {
                    Box(Modifier.align(Alignment.CenterHorizontally)) {
                        dragHandle()
                    }
                }
                content()
            }
        }
    }

    if (sheetState.hasExpandedState) {
        LaunchedEffect(key1 = sheetState) {
            sheetState.show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun nestedScroll(
    sheetState: SheetState
): NestedScrollConnection =
    object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (source != NestedScrollSource.UserInput) return Offset.Zero

            val delta = available.y
            val current = sheetState.offset
            val min = sheetState.anchoredDraggableState.anchors.minAnchor()
            val max = sheetState.anchoredDraggableState.anchors.maxAnchor()

            val newOffset = current + delta

            return if (newOffset < min || newOffset > max) {
                Offset.Zero // 🚫 禁止越界
            } else {
                Offset(0f, sheetState.anchoredDraggableState.dispatchRawDelta(delta))
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (source != NestedScrollSource.UserInput) return Offset.Zero

            val delta = available.y
            val current = sheetState.offset
            val min = sheetState.anchoredDraggableState.anchors.minAnchor()
            val max = sheetState.anchoredDraggableState.anchors.maxAnchor()

            val newOffset = current + delta

            return if (newOffset < min || newOffset > max) {
                Offset.Zero
            } else {
                Offset(0f, sheetState.anchoredDraggableState.dispatchRawDelta(delta))
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            sheetState.settle(available.y)
            return available
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            sheetState.settle(available.y)
            return available
        }
    }*/