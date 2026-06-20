package com.sdm3.parent.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdm3.parent.core.designsystem.component.StatusChip
import com.sdm3.parent.core.designsystem.theme.Primary
import com.sdm3.parent.core.designsystem.theme.Spacing
import com.sdm3.parent.core.designsystem.theme.StatusSuccess
import com.sdm3.parent.domain.entity.Student

@Composable
fun PilihAnakScreen(
    onChildSelected: (String) -> Unit
) {
    val sampleChildren = listOf(
        Student(
            id = "1", userId = null, nisn = "0012345678", nis = null,
            name = "Ahmad Fathan", className = "4-A (Ibnu Sina)",
            classroomId = "c1", academicYear = "2025/2026",
            gender = com.sdm3.parent.domain.entity.Gender.LakiLaki,
            birthPlace = "Samarinda", birthDate = "2016-05-12",
            photoUrl = null, status = com.sdm3.parent.domain.entity.StudentStatus.ACTIVE,
            fatherName = null, motherName = null, parentPhone = null
        ),
        Student(
            id = "2", userId = null, nisn = "0012345679", nis = null,
            name = "Aisyah Zahra", className = "2-B (Al Farabi)",
            classroomId = "c2", academicYear = "2025/2026",
            gender = com.sdm3.parent.domain.entity.Gender.Perempuan,
            birthPlace = "Samarinda", birthDate = "2018-03-20",
            photoUrl = null, status = com.sdm3.parent.domain.entity.StudentStatus.ACTIVE,
            fatherName = null, motherName = null, parentPhone = null
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.lg)
    ) {
        Spacer(modifier = Modifier.height(Spacing.xl))

        Text(
            text = "Pilih Data Anak",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = "Pilih anak yang ingin Anda pantau",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            items(sampleChildren) { student ->
                ChildCard(
                    name = student.name,
                    className = student.className,
                    academicYear = student.academicYear,
                    onClick = { onChildSelected(student.id) }
                )
            }
        }
    }
}

@Composable
private fun ChildCard(
    name: String,
    className: String,
    academicYear: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name.first().toString(),
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Spacing.md)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Kelas $className | TA $academicYear",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            StatusChip(
                text = "Aktif",
                color = StatusSuccess
            )
        }
    }
}
