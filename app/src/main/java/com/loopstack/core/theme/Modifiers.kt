package com.loopstack.core.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neonGlow(
    color: Color,
    radius: Dp = 8.dp,
    alpha: Float = 0.5f
) = this.drawBehind {
    val canvasSize = size
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = color.copy(alpha = alpha)
            this.style = PaintingStyle.Stroke
            this.strokeWidth = 2.dp.toPx()
        }

        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.setShadowLayer(
            radius.toPx(),
            0f,
            0f,
            color.copy(alpha = alpha).toArgb()
        )

        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = canvasSize.width,
            bottom = canvasSize.height,
            radiusX = 12.dp.toPx(),
            radiusY = 12.dp.toPx(),
            paint = paint
        )
    }
}

fun Modifier.glassmorphic(
    alpha: Float = 0.85f,
    blurRadius: Float = 0f
) = this.background(
    color = Color(0x1AFFFFFF),
    shape = RoundedCornerShape(16.dp)
)
