package com.eduocto.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eduocto.designsystem.EduOctoTheme

@Composable
fun EduOctoScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    loading: Boolean = false,
    empty: Boolean = false,
    emptyMessage: String = "Tidak ada data",
    error: String? = null,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        containerColor = EduOctoTheme.colors.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                loading -> EduOctoLoadingState()
                error != null -> EduOctoErrorState(title = error, description = "", onRetry = onRetry ?: {})
                empty -> EduOctoEmptyState(title = emptyMessage, tone = EmptyStateTone.Neutral)
                else -> content()
            }
        }
    }
}

@Composable
fun EduOctoLoadingState(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            ProgressRing(modifier = Modifier.padding(bottom = EduOctoTheme.spacing.md))
            Text("Memuat...", style = EduOctoTheme.typography.bodyMedium, color = EduOctoTheme.colors.onSurfaceMuted)
        }
    }
}

@Preview
@Composable
private fun EduOctoScreenScaffoldPreview() {
    com.eduocto.designsystem.EduOctoTheme {
        EduOctoScreenScaffold(loading = true) {}
    }
}
