package com.sdm3.parent.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.theme.Border
import com.sdm3.parent.core.designsystem.theme.SDM3Theme

fun Modifier.doubleBezel(
    outerRadius: Dp = 28.dp,
    innerRadius: Dp = 24.dp,
    outerPadding: Dp = 6.dp,
    outerColor: Color = Color.Black.copy(alpha = 0.04f),
    innerColor: Color = Color.White
): Modifier = this
    .background(outerColor, RoundedCornerShape(outerRadius))
    .padding(outerPadding)
    .clip(RoundedCornerShape(innerRadius))
    .background(innerColor)
    .border(
        width = 1.dp,
        color = Border.copy(alpha = 0.5f),
        shape = RoundedCornerShape(innerRadius)
    )

fun Modifier.level1Shadow(
    borderRadius: Dp = 24.dp
): Modifier = this.shadow(
    elevation = 4.dp,
    shape = RoundedCornerShape(borderRadius),
    ambientColor = Color.Black.copy(alpha = 0.04f),
    spotColor = Color.Black.copy(alpha = 0.08f)
)

fun Modifier.level2Shadow(
    borderRadius: Dp = 28.dp
): Modifier = this.shadow(
    elevation = 8.dp,
    shape = RoundedCornerShape(borderRadius),
    ambientColor = Color.Black.copy(alpha = 0.06f),
    spotColor = Color.Black.copy(alpha = 0.12f)
)

@Preview
@Composable
private fun ModifiersPreview() {
    SDM3Theme {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .doubleBezel(),
                contentAlignment = Alignment.Center
            ) {
                Text("doubleBezel()", style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .level1Shadow()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("level1Shadow()", style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .level2Shadow()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("level2Shadow()", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
