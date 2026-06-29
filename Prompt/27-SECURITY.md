# 27 — Security Standards

## Context

EduOcto handles personal data of minors (students), family financial data (SPP/payments/payroll),
and institutional records. Security failures here have real legal, financial, and child-safety
implications — treat this document as load-bearing, not boilerplate.

## Authentication

- Credentials are never stored in plaintext anywhere (client or transmitted). Use platform secure
  storage (`expect/actual SecureStorage` per `21-KMP-STANDARDS.md` — Android Keystore-backed
  EncryptedSharedPreferences equivalent, iOS Keychain) for tokens, never `SQLDelight`/plain files.
- Session tokens are short-lived access tokens + refresh token pattern; refresh happens
  transparently via an authenticated Ktor `HttpClient` plugin, with automatic logout on refresh
  failure (expired/revoked).
- Biometric unlock (where available) may gate *app re-entry* but never replaces the underlying
  token-based session — biometrics is a local convenience layer, not the auth mechanism itself.
- Failed login attempts are rate-limited server-side; the client surfaces a generic "username atau
  kata sandi salah" message (never reveals whether the username/email exists) per
  `19-ERROR-STATES.md` tone.

## Authorization (Role-Based)

- Every screen and every action checks the current user's role/permission **at the point of
  rendering and at the point of action**, not just by hiding a nav entry. A Finance action endpoint
  must reject a Teacher-role token server-side regardless of what the client displays — client-side
  role hiding is UX, not the security boundary.
- Permission checks reference a single shared `Permission` enum/sealed set defined in `core/common`,
  consumed identically by every feature module's ViewModel — no ad hoc `if (role == "admin")` string
  comparisons scattered through the codebase.
- See each `ERP/*.md` file for the per-module permission matrix.

## Input Validation

- All user input is validated both client-side (UX, `13-FORM-STANDARDS.md`) **and** server-side
  (security boundary) — never trust client validation alone for anything written to the database.
- File uploads (PPDB documents, profile photos) are validated for type and size client-side before
  upload and re-validated server-side; uploaded files are scanned/sandboxed per backend policy
  (out of scope for this client-side kit, but the client must never assume server-side trust of a
  client-declared MIME type).

## Sensitive Data Protection

- PII (student NISN, parent contact info, financial records) is never logged in plaintext, including
  in crash reports/analytics — use the `AppError` model (`23-CLEAN-ARCHITECTURE.md`) to log
  structured, PII-scrubbed error context only.
- Data at rest in the local SQLDelight database is treated as sensitive; consider field-level
  encryption for the most sensitive columns (payment account details) at the database driver layer,
  per platform capability — to be finalized with the backend security review, not assumed safe by
  default.
- Screenshots/screen recording are disabled (`FLAG_SECURE` equivalent) on screens displaying full
  payment account numbers or sensitive documents.

## Transport

- TLS only; no fallback to plaintext HTTP, including in development builds pointed at a staging
  backend (use a trusted self-signed cert + explicit pinning override for dev, never a blanket
  "disable SSL verification" flag that could leak into a release build).
- Certificate pinning is configured for the production API host in the Ktor client engine config
  per platform.

## Audit Logging

- Every action affecting financial records (payment recorded, invoice voided, payroll adjusted) and
  every approval/rejection (PPDB, leave requests) writes an audit log entry: who, what, when, what
  changed (before/after where feasible) — see `ERP/*.md` "Audit" sections and
  `Checklists/RELEASE_CHECKLIST.md`.
- Audit logs are append-only from the client's perspective — no client-exposed "delete audit log"
  capability exists for any role.

## Destructive/High-Risk Actions

- Per `16-DIALOG-STANDARDS.md`, destructive actions on financial records require typed confirmation,
  not just a tap-to-confirm dialog.
- Bulk operations (mass SPP write-off, bulk PPDB rejection) require elevated role confirmation and
  always produce a single consolidated audit entry referencing every affected record.

## Anti-Patterns

- Storing an API key or backend secret in client source code/resources.
- Trusting a role flag passed from the client as authoritative for any server-side decision.
- Logging full request/response bodies that include financial or student PII at any log level
  reachable in a production build.
