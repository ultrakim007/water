package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.SeniorCareThemeDefaults

@Composable
fun WaterCupIcon(
    isFilled: Boolean,
    cupNumber: Int,
    modifier: Modifier = Modifier
) {
    val colors = SeniorCareThemeDefaults.colors
    val fillProgress by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "water_cup_fill_$cupNumber"
    )

    val description = if (isFilled) {
        stringResource(R.string.water_cup_filled_desc, cupNumber)
    } else {
        stringResource(R.string.water_cup_empty_desc, cupNumber)
    }

    Canvas(
        modifier = modifier
            .size(width = 58.dp, height = 66.dp)
            .semantics { contentDescription = description }
    ) {
        val w = size.width
        val h = size.height
        val strokeWidth = w * 0.065f

        val topLeftX = w * 0.12f
        val topRightX = w * 0.88f
        val topY = h * 0.14f
        val bottomLeftX = w * 0.22f
        val bottomRightX = w * 0.78f
        val bottomY = h * 0.90f
        val cornerRadius = w * 0.10f

        val cupPath = Path().apply {
            moveTo(topLeftX, topY)
            lineTo(topRightX, topY)
            lineTo(bottomRightX, bottomY - cornerRadius)
            quadraticTo(bottomRightX, bottomY, bottomRightX - cornerRadius, bottomY)
            lineTo(bottomLeftX + cornerRadius, bottomY)
            quadraticTo(bottomLeftX, bottomY, bottomLeftX, bottomY - cornerRadius)
            close()
        }

        // Empty cup base
        drawPath(
            path = cupPath,
            color = colors.waterCupEmpty
        )

        // Animated water fill inside clipped cup silhouette
        if (fillProgress > 0.01f) {
            val clampedProgress = fillProgress.coerceIn(0f, 1f)
            val waterMaxHeight = (bottomY - topY) * 0.84f
            val currentWaterTop = bottomY - (waterMaxHeight * clampedProgress)

            clipPath(cupPath) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(colors.waterFill, colors.waterPrimary),
                        startY = currentWaterTop,
                        endY = bottomY
                    ),
                    topLeft = Offset(0f, currentWaterTop),
                    size = Size(w, bottomY - currentWaterTop + strokeWidth)
                )

                // Soft water surface meniscus highlight
                drawOval(
                    color = colors.waterHighlight.copy(alpha = 0.65f * clampedProgress),
                    topLeft = Offset(w * 0.14f, currentWaterTop - h * 0.035f),
                    size = Size(w * 0.72f, h * 0.075f)
                )

                // Clean specular glass shine on left wall
                drawLine(
                    color = colors.onColoredSurface.copy(alpha = 0.55f * clampedProgress),
                    start = Offset(w * 0.27f, currentWaterTop + h * 0.10f),
                    end = Offset(w * 0.31f, bottomY - h * 0.12f),
                    strokeWidth = strokeWidth * 0.8f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Crisp cup outline
        val outlineColor = if (isFilled) colors.waterPrimary else colors.waterCupOutline
        drawPath(
            path = cupPath,
            color = outlineColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Top rim highlight oval for tactile glass depth
        drawArc(
            color = outlineColor,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(topLeftX, topY - h * 0.045f),
            size = Size(topRightX - topLeftX, h * 0.09f),
            style = Stroke(width = strokeWidth * 0.85f, cap = StrokeCap.Round)
        )
    }
}
