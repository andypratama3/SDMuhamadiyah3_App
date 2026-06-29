package com.eduocto.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun Sparkline(
    data: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = EduOctoTheme.colors.primary,
    strokeWidth: Dp = 2.dp,
    fillOpacity: Float = 0.1f,
) {
    if (data.isEmpty()) return
    val fillColor = color.copy(alpha = fillOpacity)
    Canvas(modifier = modifier) {
        val stepX = size.width / (data.size - 1).coerceAtLeast(1)
        val max = data.max()
        val min = data.min()
        val range = (max - min).coerceAtLeast(1f)
        val points = data.mapIndexed { i, v ->
            Offset(i * stepX, size.height - ((v - min) / range) * size.height)
        }
        val path = Path().apply {
            moveTo(points.first().x, size.height)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, size.height)
            close()
        }
        drawPath(path, fillColor)
        val linePath = Path().apply {
            points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
        }
        drawPath(linePath, color, style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview
@Composable
private fun SparklinePreview() {
    com.eduocto.designsystem.EduOctoTheme {
        Sparkline(data = listOf(10f, 25f, 15f, 40f, 32f, 58f, 45f), modifier = Modifier.width(200.dp).height(48.dp))
    }
}
