package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.sdm3.parent.core.designsystem.component.Sdm3Card
import com.sdm3.parent.core.designsystem.theme.*
import com.sdm3.parent.data.remote.dto.StudentDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihAnakBottomSheet(
    students: List<StudentDto>,
    selectedStudentId: String,
    onStudentSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colorScheme.background,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = colorScheme.primary.copy(alpha = 0.1f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Pilih Identitas Siswa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Pilih profil untuk memantau aktivitas akademik spesifik.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(students) { student ->
                    val isSelected = student.id == selectedStudentId

                    Sdm3Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStudentSelected(student.id) },
                        padding = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .background(if (isSelected) colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = colorScheme.primary.copy(alpha = 0.05f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.primary
                                )
                                Text(
                                    text = "Kelas ${student.className ?: "-"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (isSelected) {
                                Surface(
                                    modifier = Modifier.size(28.dp),
                                    shape = RoundedCornerShape(100),
                                    color = colorScheme.secondary
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PilihAnakBottomSheetPreview() {
    val dummyStudents = listOf(
        StudentDto(
            id = "1", name = "Ahmad Fathan", nisn = "0012345678", nis = "12345",
            gender = "L", birthPlace = "Samarinda", birthDate = "2015-06-12",
            className = "4-A - Ibnu Sina", spp = 350000, dpp = 0
        )
    )

    SDM3Theme {
        PilihAnakBottomSheet(
            students = dummyStudents,
            selectedStudentId = "1",
            onStudentSelected = {},
            onDismiss = {}
        )
    }
}
