package com.kinship.mobile.app.ui.compose.common
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kinship.mobile.app.ui.theme.PastelGreenDark
import com.kinship.mobile.app.ui.theme.Silver
import com.kinship.mobile.app.ui.theme.White

@Composable
fun CustomSwitch(
    scale: Float = 1f,
    width: Dp = 47.dp,
    height: Dp = 23.dp,
    checkedTrackColor: Color = PastelGreenDark,
    uncheckedTrackColor: Color = Silver,
    gapBetweenThumbAndTrackEdge: Dp = 2.dp,
    switchState: Boolean,
    enabled: Boolean = true, // Add an enabled parameter
    onClick: (Offset) -> Unit = {}
) {
    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge
    val animatePosition = animateFloatAsState(
        targetValue = if (switchState)
            with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
        else
            with(LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() }, label = ""
    )

    Canvas(
        modifier = Modifier
            .size(width = width, height = height)
            .scale(scale = scale)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(onTap = onClick)
                }
            }
    ) {
        // Track
        drawRoundRect(
            color = if (switchState) checkedTrackColor else uncheckedTrackColor,
            cornerRadius = CornerRadius(x = 12.dp.toPx(), y = 12.dp.toPx())
        )

        // Thumb
        drawCircle(
            color = White,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePosition.value,
                y = size.height / 2
            )
        )
    }
}