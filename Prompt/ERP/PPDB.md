# ERP — PPDB (Admissions) Module

## Purpose

Manages new student admissions (Penerimaan Peserta Didik Baru): public-facing registration,
document submission, review/verification, and final acceptance — the only module with a meaningful
`Guest` (unauthenticated/pre-account) flow.

## Domain Model

```kotlin
data class PpdbApplication(
    val id: String,
    val applicantName: String,
    val dateOfBirth: LocalDate,
    val parentContact: ContactInfo,
    val intendedGradeLevel: Int,
    val academicYear: String,
    val documents: List<PpdbDocument>,
    val status: PpdbStatus,
    val submittedAt: Instant?,
    val reviewedBy: String?,
    val rejectionReason: String?,
)
enum class PpdbStatus {
    DraftNotSubmitted, Submitted, DocumentVerificationPending,
    DocumentRejected, UnderReview, Accepted, Rejected, EnrollmentConfirmed,
}
data class PpdbDocument(val type: DocumentType, val fileUrl: String, val verificationStatus: VerificationStatus)
enum class DocumentType { BirthCertificate, FamilyCard, PreviousReportCard, Photo, Other }
enum class VerificationStatus { Pending, Verified, Rejected }
```

## Business Rules

1. **A Guest can start and save a draft application without an account**, identified by a
   registration code + contact verification (email/phone OTP) rather than full account creation —
   account creation happens only upon `Accepted → EnrollmentConfirmed`, when the student record is
   created in `ERP/Student.md`.
2. **Status flow is strictly sequential**: `DraftNotSubmitted → Submitted → DocumentVerificationPending
   → (DocumentRejected ↔ resubmit) → UnderReview → (Accepted | Rejected) → EnrollmentConfirmed`
   (only reachable from `Accepted`). No status may be skipped programmatically.
3. **Document rejection requires a specific reason per document** (e.g. "Foto KK tidak jelas, mohon
   unggah ulang") surfaced directly to the applicant so they know exactly what to fix — never a
   generic "dokumen ditolak."
4. **Acceptance capacity limits**: each `intendedGradeLevel`/`academicYear` combination has a
   configurable quota (`ERP/Settings.md`); the system warns Admin Staff when accepting an application
   would exceed quota, but does not hard-block (a human decision may override capacity in edge
   cases) — this is a soft warning, not a hard constraint, since real-world admissions sometimes need
   judgment calls.
5. **`EnrollmentConfirmed` triggers automatic creation of the `Student` record** (`ERP/Student.md`)
   and the initial `Invoice` for any one-time admission fee (`ERP/Finance.md` `FeeFrequency.OneTime`)
   — this hand-off is a single atomic `ConfirmEnrollmentUseCase`, never a manual multi-step process
   prone to a record being missed.

## Screens

- Public PPDB Landing (Guest) — intake info, requirements, key dates.
- Application Form (Guest, multi-step, autosaving draft) — see `13-FORM-STANDARDS.md` multi-step
  guidance, `Blueprints/` (consider a dedicated PpdbApplicationBlueprint if the flow grows complex).
- Application Status Tracker (Guest/Applicant) — `PPDBStatusTracker` component
  (`20-COMPONENT-LIBRARY.md`), stepper visualization of current status.
- Document Verification Queue (Admin Staff) — per-document approve/reject with reason.
- Application Review (Admin Staff/Principal) — full applicant view, accept/reject decision.

## Permission Matrix

| Action | Principal/VP | Admin Staff | Finance | Guest/Applicant |
|---|---|---|---|---|
| Submit application | ❌ | ❌ | ❌ | ✅ |
| Verify documents | ❌ | ✅ | ❌ | ❌ |
| Accept/Reject application | ✅ (final) | ✅ (recommend) | ❌ | ❌ |
| View own application status | ➖ | ➖ | ➖ | ✅ |
| Confirm enrollment (post-acceptance) | ➖ | ✅ | ➖ | ✅ (initiates) |

## Audit Requirements

Every status transition (especially Accept/Reject and document verification decisions) is audited
with actor, timestamp, and reason — admissions decisions carry legal/reputational weight.
