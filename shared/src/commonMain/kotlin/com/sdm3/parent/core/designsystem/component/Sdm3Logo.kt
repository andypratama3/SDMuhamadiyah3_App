package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import sdmuhammadiyah3samarinda.shared.generated.resources.Res
import sdmuhammadiyah3samarinda.shared.generated.resources.logo_sd
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.designsystem.theme.glassBorderColor
import com.sdm3.parent.core.designsystem.theme.glassSurfaceColor
import com.sdm3.parent.core.designsystem.theme.heroContentColor
import com.sdm3.parent.core.designsystem.theme.heroContentColor

@Composable
fun Sdm3Logo(
    size: Dp = 100.dp,
    modifier: Modifier = Modifier,
    useTextFallback: Boolean = false,
    showBackground: Boolean = true
) {
    val glassSurface = glassSurfaceColor()
    val glassBorder = glassBorderColor()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (showBackground) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                glassSurface.copy(alpha = 0.4f),
                                glassSurface.copy(alpha = 0.1f),
                                glassSurface.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(size / 3f)
                    )
                    .padding(2.dp)
                    .background(
                        color = glassSurface.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(size / 3.2f)
                    )
            )

            Box(
                modifier = Modifier
                    .size(size * 0.85f)
                    .clip(RoundedCornerShape(size / 4f))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                glassSurface.copy(alpha = 0.25f),
                                glassSurface.copy(alpha = 0.15f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                glassBorder.copy(alpha = 0.4f),
                                Color.Transparent,
                                glassBorder.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(size / 4f)
                    )
            ) {
                LogoContent(useTextFallback, size)
            }

            Box(
                modifier = Modifier
                    .size(size / 2)
                    .background(glassSurface.copy(alpha = 0.1f), RoundedCornerShape(100))
            )
        } else {
            LogoContent(useTextFallback, size)
        }
    }
}

@Composable
private fun LogoContent(useTextFallback: Boolean, size: Dp) {
    val heroContent = heroContentColor()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (useTextFallback) {
            Text(
                text = "SDM3",
                color = Color.Black.copy(alpha = 0.2f),
                fontSize = (size.value / 4.5f).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier
            )
            Text(
                text = "SDM3",
                color = heroContent,
                fontSize = (size.value / 4.5f).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.logo_sd),
                contentDescription = "Logo SDM3",
                modifier = Modifier
                    .size(size)
                    .padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Sdm3LogoPreview() {
    SDM3Theme(darkTheme = true) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF001B3D)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Sdm3Logo(size = 120.dp, showBackground = true)
                Sdm3Logo(size = 80.dp, showBackground = false)
            }
        }
    }
}
