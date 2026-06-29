# Teacher Blueprint

Blueprint for the My Schedule (Teacher self-view) and Teacher Directory screens, implementing
`ERP/Teacher.md`.

## My Schedule

```
MyScheduleScreen
└── MyScheduleContent(state: MyScheduleUiState)
    ├── EduOctoTopBar(title = "Jadwal Saya")
    ├── EduOctoTabRow(Today, This Week)
    └── ScheduleList / ScheduleGrid
          ├── Today tab    → LazyColumn of today's ScheduleSlot, chronological, current slot
          │                  highlighted with `secondary` (Academic Gold) accent
          └── Week tab     → grid (day columns × time rows) on Medium/Expanded; stacked day
                             sections on Compact
```

## Teacher Directory (Admin/Principal view)

```
TeacherDirectoryScreen
└── TeacherDirectoryContent(state: TeacherDirectoryUiState)
    ├── EduOctoTopBar(title, searchAction)
    └── LazyColumn of TeacherRow (EduOctoListItem + workload indicator, e.g. "24 jam/minggu")
          └── tap → TeacherDetail (profile, full schedule, assignment management for Admin)
```

## State Shape

```kotlin
data class MyScheduleUiState(
    val isLoading: Boolean = false,
    val todaySlots: ImmutableList<ScheduleSlotUiModel> = persistentListOf(),
    val weekSlots: ImmutableList<ScheduleSlotUiModel> = persistentListOf(),
    val error: AppError? = null,
)
data class ScheduleSlotUiModel(
    val id: String, val subjectName: String, val className: String,
    val startTime: LocalTime, val endTime: LocalTime, val room: String?, val isCurrent: Boolean,
)
```

## Required States

| Section | Loading | Empty | Error |
|---|---|---|---|
| Today's schedule | 3 skeleton rows | "Tidak ada jadwal hari ini" (neutral, e.g. weekend/holiday) | Inline retry |
| Week grid | Skeleton grid blocks | N/A — grid always renders, empty cells show nothing | Inline retry |
| Teacher Directory | 6 skeleton rows | "Belum ada data guru" + CTA (Admin only) | EduOctoErrorState |

## Permission Notes

A Teacher only ever sees their own schedule in "My Schedule." Teacher Directory (viewing all
teachers' schedules/assignments) is restricted to Principal/VP/Admin per `ERP/Teacher.md` matrix.
