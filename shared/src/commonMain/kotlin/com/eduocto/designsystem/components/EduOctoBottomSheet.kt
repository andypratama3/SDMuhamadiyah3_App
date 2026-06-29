package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduocto.designsystem.EduOctoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduOctoBottomSheet(
    title: String? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = EduOctoTheme.shapes.radiusLarge, topEnd = EduOctoTheme.shapes.radiusLarge),
        containerColor = EduOctoTheme.colors.surface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EduOctoTheme.spacing.md)
                .navigationBarsPadding(),
        ) {
            if (title != null) {
                Text(title, style = EduOctoTheme.typography.titleMedium, color = EduOctoTheme.colors.onSurface)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = EduOctoTheme.spacing.md))
            }
            content()
            androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = EduOctoTheme.spacing.md))
        }
    }
}

@Preview
@Composable
private fun EduOctoBottomSheetPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoBottomSheet(title = "Pilih Kelas", onDismiss = {}) {
            Text("Kelas 5A", style = EduOctoTheme.typography.bodyLarge)
            Text("Kelas 5B", style = EduOctoTheme.typography.bodyLarge)
        }
    }
}
