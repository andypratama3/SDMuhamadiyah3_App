# Attendance Blueprint

Blueprint for Mark Attendance (Teacher) and Attendance History (Student/Parent), implementing
`ERP/Attendance.md` and `14-LIST-STANDARDS.md` swipe-action patterns.

## Mark Attendance

```
MarkAttendanceScreen
└── MarkAttendanceContent(state: MarkAttendanceUiState)
    ├── EduOctoTopBar(className, date, subtitle = "X dari Y ditandai")
    ├── QuickActionRow      ("Tandai Semua Hadir" — single haptic per 10-HAPTICS.md bulk rule)
    └── LazyColumn of AttendanceRow
          ├── EduOctoAvatar + student name
          ├── StatusPill showing current status (defaults to Present after bulk action)
          ├── Swipe-right → Present (success tint), Swipe-left → Absent (danger tint)
          └── Tap → opens a small status picker (Present/Sick/Excused/Absent/Late) for precise control
    └── EduOctoButton(Primary, "Simpan Kehadiran", sticky bottom, loading state)
```

## Attendance History (Student/Parent)

```
AttendanceHistoryScreen
└── AttendanceHistoryContent(state: AttendanceHistoryUiState)
    ├── EduOctoTopBar(title = "Riwayat Kehadiran")
    ├── AttendanceRateSummaryCard (ProgressRing showing % this term)
    ├── EduOctoTabRow(Calendar, List)
    └── Calendar view (color-coded days per status) / List view (chronological AttendanceRow)
```

## State Shape

```kotlin
data class MarkAttendanceUiState(
    val isLoading: Boolean = false,
    val classId: String = "",
    val date: LocalDate = LocalDate.today(),
    val roster: ImmutableList<AttendanceRowUiModel> = persistentListOf(),
    val isSubmitting: Boolean = false,
    val error: AppError? = null,
) {
    val markedCount: Int get() = roster.count { it.status != null }
}
data class AttendanceRowUiModel(val studentId: String, val name: String, val photoUrl: String?, val status: AttendanceStatus?)

data class AttendanceHistoryUiState(
    val isLoading: Boolean = false,
    val ratePercent: Double = 0.0,
    val records: ImmutableList<AttendanceRecordUiModel> = persistentListOf(),
    val error: AppError? = null,
)
```

## Business Rule Hooks (per `ERP/Attendance.md`)

- "Tandai Semua Hadir" sets every unmarked row to `Present` locally; the actual submit still writes
  one `AttendanceRecord` per student (rule 1), batched in a single transaction/use case call, with a
  single audit entry summarizing the batch.
- Editing a date older than the configured edit window (rule 4) shows a co-approval-required notice
  instead of the normal save button.

## Required States

| Section | Loading | Empty | Error |
|---|---|---|---|
| Mark Attendance roster | Skeleton rows | "Tidak ada siswa di kelas ini" (rare/config issue) | EduOctoErrorState, blocks submission until resolved |
| Attendance History | Skeleton summary + rows | "Belum ada data kehadiran" | EduOctoErrorState with retry |

## Permission Notes

Only the assigned teacher (subject) or homeroom teacher (whole-day) may mark attendance for a given
class/session, enforced per `ERP/Attendance.md` and `ERP/Teacher.md` rule 2.
