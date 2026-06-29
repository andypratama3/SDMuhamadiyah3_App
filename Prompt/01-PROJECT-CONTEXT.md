# 01 — Project Context

## What EduOcto Is

EduOcto is an enterprise School ERP platform built with Kotlin Multiplatform. It replaces fragmented
spreadsheets, WhatsApp groups, and paper processes with a single coherent system covering academics,
finance, attendance, and communication for a school (initial deployment context: a private Islamic
school, SD Muhammadiyah-type institution in Indonesia — primary language **Bahasa Indonesia**,
secondary **English**).

EduOcto is **not** a generic CRUD admin panel. It is designed to feel like a premium SaaS product —
closer to Linear, Stripe Dashboard, or Notion in execution quality than to a typical school portal.

## Technology Stack

| Layer | Choice |
|---|---|
| Language | Kotlin (Multiplatform) |
| UI | Compose Multiplatform, Material 3 |
| Networking | Ktor Client |
| Local persistence | SQLDelight |
| Backend (assumed) | Ktor Server or REST/GraphQL API consumed by the client — defined by `shared` module contracts |
| DI | Koin or manual DI per `23-CLEAN-ARCHITECTURE.md` |
| Build | Gradle Kotlin DSL |

## Target Platforms

- Android (primary, ships first)
- iOS (parity target, not an afterthought)
- Desktop (admin/back-office usage)
- Web (future — architecture must not block it)

## Current Repository Shape

```
SDMuhammadiyah3Samarinda/
├── androidApp/
├── iosApp/
├── shared/
│   └── src/
│       ├── commonMain/   (kotlin, sqldelight, composeResources)
│       ├── androidMain/
│       ├── iosMain/
│       ├── commonTest/
│       ├── androidHostTest/
│       └── iosTest/
├── build.gradle.kts
├── settings.gradle.kts
├── PROJECT.md
└── docs/
```

`shared/src/commonMain/kotlin` is where almost all business logic, state, and UI for Compose
Multiplatform lives. Platform-specific code stays minimal and isolated in `androidMain` / `iosMain`.

This `AI-EDUOCTO-MASTER` kit is intended to live at the project root (or under `docs/ai-kit/`) and be
read by the AI coding agent before it touches `shared/`.

## Status

Enterprise Production Development — this is a real system being built for real school operations,
not a prototype. Treat data correctness (especially Finance/SPP/Payroll) as load-bearing: mistakes
have real financial and reputational consequences for the school.

## Users (high level — see `ERP/*.md` for full role detail)

- **Principal / Vice Principal** — oversight, approvals, school-wide analytics.
- **Teacher / Homeroom Teacher** — attendance, grading, report cards, class communication.
- **Administrative Staff** — student records, documents, PPDB (admissions), scheduling.
- **Finance Staff** — SPP billing, payments, payroll, financial reporting.
- **Student** — schedule, grades, attendance, announcements (read-mostly).
- **Parent** — child's academic/financial status, payments, communication with teachers.
- **Guest** — public-facing PPDB (admission) info and registration only.

## Languages & Locale

- Default locale: `id-ID` (Bahasa Indonesia). All user-facing strings must be localizable
  (`stringResource`/Compose Resources), never hardcoded inline.
- Currency: Indonesian Rupiah (`Rp`), formatted with thousands separators, no decimal places for IDR.
- Dates: `dd MMMM yyyy` for display (e.g. `27 Juni 2026`), ISO-8601 for storage/transport.

## Non-Goals (explicitly out of scope unless re-scoped)

- Government regulatory reporting integrations (e.g. Dapodik) — may be added later, not assumed now.
- Multi-school SaaS tenancy — designed for *eventually*, not required for current production target.
- Native push notification infrastructure beyond what's defined in `ERP/Notification.md`.
