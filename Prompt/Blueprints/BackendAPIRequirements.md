# Backend API Requirements — SDM3 Parent Portal

> Document prepared for Laravel backend developer.
> Frontend (KMP / Compose Multiplatform) has been fully implemented with DTOs, Repository interfaces, API service classes, and 25 screens. All screens display shimmer skeletons while loading and show proper empty/error states. This document describes what the backend must deliver.

---

## Base Information

| Item | Value |
|---|---|
| Base URL | `https://admin.sdm3.sch.id` |
| Auth | Laravel Sanctum (Bearer token via `POST /api/sanctum/token`) |
| Response Envelope | `{ success: bool, data: T|null, message: string|null, code: int|null }` |
| Date Format | ISO 8601 (`2025-01-15` or `2025-01-15T08:30:00Z`) |
| Image URLs | Absolute URLs to `storage/` or `public/` paths |

---

## Authentication Flow

### Login (Sanctum Token)
```
POST /api/sanctum/token
Body: { email: string, password: string, device_name: "mobile" }
Response: { user: UserDto, token: string }
```

`UserDto`: `{ id: string, name: string, email: string, phone: string|null, avatar: string|null }`

- After login the frontend stores the Bearer token in encrypted KVault storage.
- All subsequent requests include `Authorization: Bearer {token}`.
- Token expiry returns `419 Session Expired` → frontend clears storage and redirects to login.

### Get Authenticated User
```
GET /api/user
Headers: Authorization: Bearer {token}
Response: UserDto
```

### Forgot Password / OTP
```
POST /api/forgot-password
Body: { email: string }
Response: { message: string }

POST /api/verify-otp
Body: { email: string, otp: string }
Response: { message: string, token: string|null }

POST /api/reset-password
Body: { email: string, otp: string, password: string, password_confirmation: string }
Response: { message: string }
```

---

## Parent Portal Endpoints (Sanctum Auth Required)

All endpoints below require `Authorization: Bearer {token}` and return the standard `ApiResponse<T>` envelope.

### 1. Students (used by: PilihAnakScreen, HomeScreen, ProfilAkunScreen)

```
GET /api/parent/students
→ ApiResponse<List<StudentDto>>

GET /api/parent/students/{id}
→ ApiResponse<StudentDto>
```

`StudentDto`: `{ id: string, name: string, nisn: string, nis: string|null, gender: string, birth_place: string, birth_date: string, photo: string|null, class_name: string|null, spp: int|null, dpp: int|null }`

**Business logic:**
- A parent can have 1+ children registered.
- `photo` is a URL to the student's profile photo.
- `spp` and `dpp` are monthly SPP/DPP amounts in IDR (integer).

### 2. Dashboard (used by: HomeScreen)

```
GET /api/parent/dashboard?student_id={id}
→ ApiResponse<DashboardDto>
```

`DashboardDto`:
```json
{
  "student": StudentDto,
  "attendance_summary": { "hadir": int, "sakit": int, "izin": int, "alpa": int } | null,
  "recent_grades": [ GradeDto ] | null,
  "active_fees": [ StudentFeeDto ] | null,
  "announcements": [ ArticleDto ] | null
}
```

**Query params:** `student_id` (required) — the currently selected student.

**Business logic:**
- `student_id` is selected by the parent on the PilihAnakScreen and is persisted locally.
- Each section is independently nullable so a slow section doesn't block the whole screen.

### 3. Grades (used by: NilaiRaporScreen, DetailNilaiMapelScreen)

```
GET /api/parent/grades?student_id={id}&semester={semester}
→ ApiResponse<List<GradeDto>>

GET /api/parent/grade-components?student_id={id}&subject_id={subjectId}
→ ApiResponse<List<GradeComponentDto>>
```

`GradeDto`: `{ id: string, subject_id: string, subject_name: string, score: double|null, predicate: string|null, narrative: string|null, semester: string }`

`GradeComponentDto`: `{ id: string, subject_id: string, subject_name: string, component_type: string, component_subtype: string|null, score: double|null, tp_name: string|null, tp_number: int|null }`

**Business logic:**
- The frontend splits grades into 3 tabs: Sumatif, Formatif, Projek. These are differentiated by `component_type` in GradeComponentDto. The backend should either return grades pre-categorized or the frontend will filter by `component_type`.
- `subject_id` in `grade-components` is used to drill into `DetailNilaiMapelScreen` showing per-TP breakdown.
- `semester` is optional; if omitted, return the latest/current semester.
- `narrative` is the teacher's written assessment for the subject.

### 4. Attendance (used by: KehadiranSiswaScreen)

```
GET /api/parent/attendances?student_id={id}&month={month}&year={year}
→ ApiResponse<List<AttendanceDto>>

GET /api/parent/attendance-summary?student_id={id}
→ ApiResponse<AttendanceSummaryDto>
```

`AttendanceDto`: `{ id: string, date: string, status: string, notes: string|null }`
- `status` enum: `hadir`, `sakit`, `izin`, `alpa`, `terlambat`

`AttendanceSummaryDto`: `{ hadir: int, sakit: int, izin: int, alpa: int }`

**Business logic:**
- Month/year params default to current month if omitted.
- The frontend displays a calendar view where each day is color-coded by status.
- Summary stats are displayed at the top of the screen.
- A detailed log list below the calendar shows each record.

### 5. Payment & Fees (used by: PembayaranSppScreen, PilihMetodeBayarScreen, ProsesPembayaranScreen, PembayaranBerhasilScreen, DetailBuktiBayarScreen)

```
GET /api/parent/student-fees?student_id={id}
→ ApiResponse<List<StudentFeeDto>>

GET /api/parent/payments?student_id={id}&status={status}
→ ApiResponse<List<PaymentDto>>
```

`StudentFeeDto`: `{ id: string, payment_title_id: string, payment_title_name: string, amount: double, due_date: string|null, status: string }`
- `status` enum: `unpaid`, `paid`, `overdue`, `partial`

`PaymentDto`: `{ id: string, order_id: string, gross_amount: double|null, payment_type: string|null, status: string, va_number: string|null, paid_at: string|null, created_at: string|null }`
- `status` enum: `pending`, `success`, `failed`, `expired`

**Midtrans integration:**
```
POST /dashboard/midtrans/snap-token/{paymentId}
Body: {}
→ ApiResponse<SnapTokenResponse>

GET /dashboard/midtrans/status/{chargeId}
→ ApiResponse<PaymentDto>
```

`SnapTokenResponse`: `{ snap_token: string, redirect_url: string|null }`

**Business logic:**
- **Midtrans flow:** Frontend gets `snap_token`, opens Midtrans SDK/redirect → user pays → Midtrans webhook → backend updates payment status → frontend polls `checkPaymentStatus`.
- `StatusSppScreen` shows active fees with status chips.
- `PilihMetodeBayarScreen` shows fee summary before payment.
- `ProsesPembayaranScreen` shows VA number/instructions after payment is initiated.
- `PembayaranBerhasilScreen` shows success confirmation card.
- `DetailBuktiBayarScreen` shows full receipt with payment details.

### 6. Report Cards / Rapor (used by: HalamanRaporScreen, PreviewRaporPdfScreen, VerifikasiQrRaporScreen)

```
GET /api/parent/rapor-instances?student_id={id}
→ ApiResponse<List<RaporInstanceDto>>

GET /api/parent/rapor/{id}/download
→ ApiResponse<{ url: string }>   (redirect or presigned URL to PDF)

POST /api/parent/rapor/verify
Body: { qr_data: string }
→ ApiResponse<RaporVerifyResponse>
```

`RaporInstanceDto`: `{ id: string, student_id: string, semester: string, academic_year: string, status: string, pdf_url: string|null }`
- `status` enum: `draft`, `published`, `archived`

`RaporVerifyResponse`: `{ valid: bool, message: string, student_name: string|null, nisn: string|null }`

**Business logic:**
- `HalamanRaporScreen` lists all rapor instances (grouped by academic year).
- `PreviewRaporPdfScreen` loads the PDF from `pdf_url`.
- `VerifikasiQrRaporScreen` scans a QR code on the printed rapor and sends the raw QR data to verify.

### 7. Articles / Pengumuman (used by: PengumumanSekolahScreen, DetailPengumumanScreen)

```
GET /api/parent/articles
→ ApiResponse<List<ArticleDto>>
```

`ArticleDto`: `{ id: string, title: string, content: string|null, image: string|null, category: string|null, published_at: string|null }`

**Business logic:**
- The list shows thumbnail (`image`), title, and publication date.
- Tapping opens the detail screen showing full `content` (can be HTML or plaintext).
- `category` can be `pengumuman`, `prestasi`, `kegiatan`, etc. The frontend has a horizontal category chip filter.

### 8. Notifications (used by: NotifikasiScreen)

```
GET /api/parent/notifications
→ ApiResponse<List<NotificationDto>>

PATCH /api/parent/notifications/{id}/read
Body: {}
→ ApiResponse<{ message: string }>
```

`NotificationDto`: `{ id: string, type: string, title: string, message: string, data: map<string,string>|null, read_at: string|null, created_at: string|null }`

**Business logic:**
- `type` determines the icon shown (e.g., `payment`, `grade`, `attendance`, `announcement`).
- `data` contains navigation params (e.g., `{ "screen": "PembayaranSpp", "student_id": "..." }`).
- `read_at` being null means unread → shown with a blue dot indicator.
- The `PATCH` endpoint marks a single notification as read.
- The home screen badge count shows the number of unread notifications.

### 9. Extracurricular (used by: KegiatanProgramScreen)

```
GET /api/parent/extracurriculars?student_id={id}
→ ApiResponse<List<ExtracurricularDto>>
```

`ExtracurricularDto`: `{ id: string, name: string, description: string|null, schedule: string|null, teacher_name: string|null }`

**Business logic:**
- The screen has 2 tabs: "Ekstrakurikuler" (this data) and "Program Unggulan" (hardcoded school info for now).

### 10. Profile (used by: ProfilAkunScreen, PengaturanNotifikasiScreen)

```
GET /api/parent/profile
→ ApiResponse<ProfileDto>

PATCH /api/parent/profile
Body: { name: string|null, phone: string|null }
→ ApiResponse<ProfileDto>
```

`ProfileDto`: `{ id: string, name: string, email: string, phone: string|null, avatar: string|null }`

**Notification settings screen:**
- No specific notification-settings API needed yet. The frontend uses local toggles (push/email/SMS) that are user-preference only.
- Account deletion is shown as a confirmation dialog on the profile screen (no API endpoint consumed yet).

---

## Midtrans Payment Flow (Critical)

```
Parent selects fee → Frontend calls POST /dashboard/midtrans/snap-token/{paymentId}
                      → Backend creates Midtrans transaction, returns snap_token
                      → Frontend opens Midtrans SDK/redirect URL
                      → Parent pays via Midtrans (VA, QRIS, etc.)
                      → Midtrans sends webhook to backend
                      → Backend updates payment status in DB
                      → Frontend polls GET /dashboard/midtrans/status/{chargeId}
                      → Shows success/failure screen
```

**Backend responsibilities:**
1. Create Midtrans transaction and return `snap_token`.
2. Handle Midtrans webhook (payment notification) and update local payment status.
3. Expose status endpoint for the frontend to poll after payment.

---

## Push Notifications (FCM)

The frontend uses Firebase Cloud Messaging for push notifications.

- The FCM token is sent to the backend (not yet implemented — needs a `POST /api/parent/fcm-token` endpoint).
- The backend should store the token per device and send push notifications for:
  - New payment created
  - Payment confirmed
  - New grade published
  - New attendance record
  - New announcement/article
  - New notification
- Notification payload should include a `data` map with navigation params.

---

## Query Parameter Convention

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `student_id` | string | Usually yes | Currently selected student ID |
| `month` | int (1-12) | No | Month filter, defaults to current |
| `year` | int | No | Year filter, defaults to current |
| `semester` | string | No | Semester filter (e.g., "Ganjil 2024/2025") |
| `status` | string | No | Status filter for payments |

---

## Data Relationships

```
Parent (1)
  ├─ Student (1..N)
  │    ├─ AttendanceRecord (N)     — per day per student
  │    ├─ Grade (N per semester)   — per subject per semester
  │    ├─ GradeComponent (N)       — per TP/sub-component
  │    ├─ StudentFee (N)           — monthly/periodic fees
  │    ├─ Payment (N)              — payment transactions
  │    ├─ RaporInstance (per semester)
  │    ├─ ExtracurricularEnrollment (N)
  │    └─ DashboardDto (aggregate)
  │
  ├─ Notification (N)
  └─ Profile (1)
```

---

## Response Format Standards

### Success
```json
{
  "success": true,
  "data": { ... },
  "message": null,
  "code": 200
}
```

### Error
```json
{
  "success": false,
  "data": null,
  "message": "Human-readable error message",
  "code": 422
}
```

### HTTP Status Codes Used

| Code | Meaning | Frontend Behavior |
|------|---------|-------------------|
| 200 | OK | Parse and display data |
| 201 | Created | Notification creation success |
| 401 | Unauthorized | Redirect to login |
| 403 | Forbidden | Show "tidak memiliki akses" state |
| 404 | Not Found | Show empty state |
| 419 | Session Expired | Clear all secure data, redirect to login |
| 422 | Validation Error | Show validation message |
| 429 | Rate Limited | Show "terlalu banyak permintaan" |
| 500 | Server Error | Show generic error with retry button |

---

## Security Requirements

1. **All parent endpoints** must verify that the authenticated user is the parent of the requested `student_id`.
2. Token expiry handling: return `419` with `message: "Session expired"` when Sanctum token is invalid/expired.
3. Rate limiting on login endpoint (max 5 attempts per minute).
4. SSL/TLS required for all production endpoints.
5. CORS: allow requests from mobile apps (or use `*` for dev).

---

## Endpoint Summary Table

| Method | Endpoint | Feature | Screen(s) |
|--------|----------|---------|-----------|
| POST | `/api/sanctum/token` | Login | LoginScreen |
| GET | `/api/user` | Auth user | SplashScreen |
| POST | `/api/forgot-password` | Request OTP | - |
| POST | `/api/verify-otp` | Verify OTP | - |
| POST | `/api/reset-password` | Reset password | - |
| GET | `/api/parent/students` | List children | PilihAnakScreen |
| GET | `/api/parent/students/{id}` | Student detail | DetailInfoAnakScreen |
| GET | `/api/parent/dashboard` | Home aggregate | HomeScreen |
| GET | `/api/parent/grades` | Grades list | NilaiRaporScreen |
| GET | `/api/parent/grade-components` | Grade breakdown | DetailNilaiMapelScreen |
| GET | `/api/parent/attendances` | Attendance log | KehadiranSiswaScreen |
| GET | `/api/parent/attendance-summary` | Attendance stats | KehadiranSiswaScreen |
| GET | `/api/parent/student-fees` | Active fees | PembayaranSppScreen |
| GET | `/api/parent/payments` | Payment history | PembayaranSppScreen, DetailBuktiBayarScreen |
| POST | `/dashboard/midtrans/snap-token/{payment}` | Snap token | ProsesPembayaranScreen |
| GET | `/dashboard/midtrans/status/{chargeId}` | Payment status | PembayaranBerhasilScreen |
| GET | `/api/parent/rapor-instances` | Rapor list | HalamanRaporScreen |
| GET | `/api/parent/rapor/{id}/download` | PDF URL | PreviewRaporPdfScreen |
| POST | `/api/parent/rapor/verify` | QR verify | VerifikasiQrRaporScreen |
| GET | `/api/parent/articles` | Announcements | PengumumanSekolahScreen |
| GET | `/api/parent/notifications` | Notifications | NotifikasiScreen |
| PATCH | `/api/parent/notifications/{id}/read` | Mark read | NotifikasiScreen |
| GET | `/api/parent/extracurriculars` | Extracurriculars | KegiatanProgramScreen |
| GET | `/api/parent/profile` | Parent profile | ProfilAkunScreen |
| PATCH | `/api/parent/profile` | Update profile | ProfilAkunScreen |
| POST | `/api/parent/fcm-token` | Register FCM token | (background) |
| POST | `/api/parent/logout` | Logout | ProfilAkunScreen |

---

## Implementation Priority

1. **Authentication** — Sanctum token login + user endpoint (blocks everything)
2. **Students & Dashboard** — needed for the home screen
3. **Payments & Midtrans** — financial feature, high business value
4. **Grades** — academic feature
5. **Attendance** — monitoring feature
6. **Report Cards (Rapor)** — PDF generation + QR verification
7. **Notifications & Articles** — communication feature
8. **Profile & Extracurriculars** — supplementary features
9. **Push Notifications (FCM)** — engagement feature
