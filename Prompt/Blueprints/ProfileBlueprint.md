# Profile Blueprint

Blueprint for the "My Profile" screen shared (with role-specific sections) across every role,
implementing `ERP/Settings.md` `UserPreferences` and tying together identity display.

## Composable Structure

```
ProfileScreen
└── ProfileContent(state: ProfileUiState)
    ├── ProfileHeader            (GlassSurface: avatar, name, role badge, contact info)
    ├── RoleSpecificSummarySection
    │     ├── (Student/Parent)   → linked children/own academic quick stats
    │     ├── (Teacher)          → assigned classes/subjects summary, links to ERP/Teacher.md detail
    │     └── (Finance/Admin)    → no extra summary; goes straight to Account section
    ├── PreferencesSection
    │     ├── EduOctoDropdown(Locale: id/en)
    │     ├── NotificationPreferenceList (per ERP/Notification.md NotificationType, toggles)
    │     └── ThemePreference (Light only for v1, control present but Dark/System disabled-stated)
    ├── AccountSection
    │     ├── Change Password action
    │     └── Logout action (confirmation dialog, per 16-DIALOG-STANDARDS.md)
    └── AboutSection             (app version, support contact)
```

## State Shape

```kotlin
data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserProfileUiModel? = null,
    val preferences: UserPreferences? = null,
    val roleSummary: RoleSummaryUiModel? = null,
    val isLoggingOut: Boolean = false,
    val error: AppError? = null,
)
data class UserProfileUiModel(val name: String, val role: UserRole, val email: String, val phone: String, val photoUrl: String?)
sealed interface RoleSummaryUiModel {
    data class ParentSummary(val children: List<ChildQuickStat>) : RoleSummaryUiModel
    data class TeacherSummary(val classCount: Int, val subjectCount: Int) : RoleSummaryUiModel
    data object None : RoleSummaryUiModel
}
```

## Required States

| Section | Loading | Empty | Error |
|---|---|---|---|
| Profile Header | Skeleton | N/A (always has a logged-in user) | Inline retry |
| Role Summary | Skeleton | "Belum ada data" (e.g. Parent with no linked child yet — rare/setup issue) | Inline retry |
| Preferences | Skeleton toggles | N/A (always has defaults) | Inline retry, preferences not saved until resolved |

## Permission Notes

Every role sees this same screen structure; only `RoleSummaryUiModel`'s variant changes. Logout is
available to all roles including Guest (clears any draft PPDB session per `ERP/PPDB.md`).
