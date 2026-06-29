# Detail Example

A worked example of the Student Detail screen (`Blueprints/StudentBlueprint.md`), demonstrating
cross-module composition (Student + Attendance + Payment quick summaries) within Clean Architecture
boundaries.

## UI State

```kotlin
@Immutable
data class StudentDetailUiState(
    val isLoading: Boolean = false,
    val student: StudentDetailUiModel? = null,
    val attendanceSummary: AttendanceQuickSummary? = null,
    val paymentSummary: PaymentQuickSummary? = null,
    val canEdit: Boolean = false,
    val error: AppError? = null,
)
data class StudentDetailUiModel(
    val id: String, val name: String, val nisn: String, val className: String,
    val status: StudentStatus, val photoUrl: String?, val guardians: ImmutableList<GuardianUiModel>,
)
data class GuardianUiModel(val name: String, val relationship: GuardianRelationship, val isPrimaryContact: Boolean, val phone: String)
data class AttendanceQuickSummary(val ratePercent: Double, val absenceCountThisMonth: Int)
data class PaymentQuickSummary(val outstandingAmount: Long, val isOverdue: Boolean)
```

## UseCase composing three repositories (Clean Architecture, `23-CLEAN-ARCHITECTURE.md`)

```kotlin
class GetStudentDetailUseCase(
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val invoiceRepository: InvoiceRepository,
    private val permissionChecker: PermissionChecker,
) {
    suspend operator fun invoke(studentId: String, requesterId: String): AppResult<StudentDetailBundle> = runCatching {
        val student = studentRepository.getStudent(studentId) ?: throw NotFoundException()
        permissionChecker.assertCanView(requesterId, student) // throws if not permitted; never silently filters

        coroutineScope {
            val attendance = async { attendanceRepository.getMonthSummary(studentId) }
            val outstanding = async { invoiceRepository.getOutstandingSummary(studentId) }
            StudentDetailBundle(
                student = student,
                attendanceSummary = attendance.await(),
                paymentSummary = outstanding.await(),
                canEdit = permissionChecker.canEdit(requesterId, student),
            )
        }
    }.fold(onSuccess = { AppResult.Success(it) }, onFailure = { AppResult.Failure(it.toAppError()) })
}
```

Note: `attendance` and `outstanding` fetch concurrently (`async`/`coroutineScope`) rather than
sequentially — both are independent reads with no data dependency between them, so there is no reason
to block one on the other.

## Composable — composed detail layout

```kotlin
@Composable
fun StudentDetailContent(state: StudentDetailUiState, onIntent: (StudentDetailIntent) -> Unit, modifier: Modifier = Modifier) {
    when {
        state.isLoading -> StudentDetailSkeleton()
        state.error != null -> EduOctoErrorState(
            title = "Gagal memuat data siswa", description = state.error.toUserMessage(),
            onRetry = { onIntent(StudentDetailIntent.Retry) },
        )
        state.student != null -> Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            ProfileHeader(state.student, canEdit = state.canEdit, onEdit = { onIntent(StudentDetailIntent.EditClicked) })

            Spacer(Modifier.height(EduOctoTheme.spacing.lg))
            EduOctoSection(title = "Wali Murid") {
                state.student.guardians.forEach { guardian -> GuardianRow(guardian) }
            }

            Spacer(Modifier.height(EduOctoTheme.spacing.lg))
            Row(Modifier.fillMaxWidth().padding(horizontal = EduOctoTheme.spacing.md), horizontalArrangement = Arrangement.spacedBy(EduOctoTheme.spacing.md)) {
                state.attendanceSummary?.let {
                    QuickStatTile(
                        label = "Kehadiran Bulan Ini", value = "${it.ratePercent.toInt()}%",
                        tone = if (it.absenceCountThisMonth >= 3) StatTone.Warning else StatTone.Success,
                        onClick = { onIntent(StudentDetailIntent.ViewAttendance) },
                        modifier = Modifier.weight(1f),
                    )
                }
                state.paymentSummary?.let {
                    QuickStatTile(
                        label = "Status Pembayaran",
                        value = if (it.outstandingAmount == 0L) "Lunas" else formatCurrencyIdr(it.outstandingAmount),
                        tone = if (it.isOverdue) StatTone.Danger else if (it.outstandingAmount > 0) StatTone.Warning else StatTone.Success,
                        onClick = { onIntent(StudentDetailIntent.ViewPayments) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
```

This demonstrates: concurrent independent data fetching at the UseCase layer, permission enforcement
inside the UseCase (not just hiding UI), and composing quick-summary tiles from two different ERP
modules (`ERP/Attendance.md`, `ERP/Payment.md`) into one cohesive detail screen, each tile navigating
into its own module's full detail view per `11-NAVIGATION.md`.
