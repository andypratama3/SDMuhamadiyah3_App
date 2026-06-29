package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoDataTable(
    columns: List<String>,
    rows: List<List<@Composable () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    val colors = EduOctoTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EduOctoTheme.spacing.md, vertical = EduOctoTheme.spacing.sm),
        ) {
            columns.forEachIndexed { i, col ->
                Text(
                    col,
                    style = EduOctoTheme.typography.labelLarge,
                    color = colors.onSurfaceMuted,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        HorizontalDivider(color = colors.outline.copy(alpha = 0.2f))
        rows.forEachIndexed { rIdx, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = EduOctoTheme.spacing.md, vertical = EduOctoTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                row.forEachIndexed { cIdx, cell ->
                    Box(modifier = Modifier.weight(1f)) { cell() }
                }
            }
            if (rIdx < rows.size - 1) {
                HorizontalDivider(color = colors.outline.copy(alpha = 0.12f))
            }
        }
    }
}

@Preview
@Composable
private fun EduOctoDataTablePreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoDataTable(
            columns = listOf("Nama", "Kelas", "Status"),
            rows = listOf(
                listOf({ Text("Ahmad") }, { Text("5A") }, { StatusPill("AKTIF", StatusPillColor.Success) }),
                listOf({ Text("Budi") }, { Text("5B") }, { StatusPill("ALUMNI", StatusPillColor.Neutral) }),
            ),
        )
    }
}
