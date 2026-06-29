# Student Blueprint

Blueprint for the Student List + Detail screens, implementing `ERP/Student.md` and
`14-LIST-STANDARDS.md`.

## Student List

```
StudentListScreen
└── StudentListContent(state: StudentListUiState)
    ├── EduOctoTopBar(title, searchAction)
    ├── FilterChipsRow      (class, status — removable chips, per 14-LIST-STANDARDS.md)
    └── LazyColumn
          └── StudentRow per item (EduOctoListItem + EduOctoAvatar + StatusPill for StudentStatus)
              ├── Loading → 6 skeleton rows
              ├── Empty   → EduOctoEmptyState ("Belum ada siswa terdaftar" + CTA if Admin/Principal)
              └── Error   → EduOctoErrorState with retry
```

## Student Detail

```
StudentDetailScreen
└── StudentDetailContent(state: StudentDetailUiState)
    ├── EduOctoTopBar(back, studentName, editAction if permitted)
    ├── ProfileHeader        (GlassSurface: avatar, name, NISN, class, status pill)
    ├── GuardianSection      (list of GuardianLink, primary contact highlighted)
    ├── QuickLinksRow        (Attendance summary tile, Payment status tile, Report Card tile —
    │                         each navigates to the respective module's detail for this student)
    └── ClassHistorySection  (timeline of ClassAssignment/transfer records)
```

## State Shape

```kotlin
data class StudentListUiState(
    val isLoading: Boolean = false,
    val students: ImmutableList<StudentRowUiModel> = persistentListOf(),
    val filters: StudentFilters = StudentFilters(),
    val searchQuery: String = "",
    val error: AppError? = null,
)
data class StudentRowUiModel(val id: String, val name: String, val nisn: String, val className: String, val status: StudentStatus, val photoUrl: String?)
data class StudentFilters(val classId: String? = null, val status: StudentStatus? = null)

data class StudentDetailUiState(
    val isLoading: Boolean = false,
    val student: Student? = null,
    val attendanceSummary: AttendanceQuickSummary? = null,
    val paymentSummary: PaymentQuickSummary? = null,
    val error: AppError? = null,
)
```

## Permission Notes (per `ERP/Student.md` matrix)

- List scope (full roster vs. own class vs. own children vs. self) is determined server-side based
  on role — the ViewModel requests "students visible to me" from the repository, never "all students"
  filtered client-side.
- Edit/Class-Transfer actions in the top bar/detail are visible only for Principal/VP/Admin Staff.
