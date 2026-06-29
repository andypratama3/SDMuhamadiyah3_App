# ERP — Announcement Module

## Purpose

One-way broadcast communication from school staff to targeted audiences (school-wide, a grade level,
a class, or a specific role) — distinct from `ERP/Messaging` which is two-way/conversational.

## Domain Model

```kotlin
data class Announcement(
    val id: String,
    val title: String,
    val body: String,
    val audience: AnnouncementAudience,
    val priority: AnnouncementPriority,
    val attachments: List<String>,     // file URLs
    val publishedBy: String,
    val publishedAt: Instant?,
    val scheduledFor: Instant?,
    val status: AnnouncementStatus,
)
sealed interface AnnouncementAudience {
    data object SchoolWide : AnnouncementAudience
    data class GradeLevel(val grade: Int) : AnnouncementAudience
    data class ClassSpecific(val classId: String) : AnnouncementAudience
    data class RoleSpecific(val role: UserRole) : AnnouncementAudience
}
enum class AnnouncementPriority { Normal, Important, Urgent }
enum class AnnouncementStatus { Draft, Scheduled, Published, Archived }
```

## Business Rules

1. **Audience determines visibility strictly server-side** — a Parent only ever receives
   announcements matching `SchoolWide`, their child's `ClassSpecific`/`GradeLevel`, or
   `RoleSpecific(Parent)`.
2. **`Urgent` priority triggers an immediate push notification** (see `ERP/Notification.md`)
   regardless of the recipient's notification preferences for `Normal`/`Important` (urgent
   school-safety-relevant announcements must not be silenceable) — `Normal`/`Important` respect user
   notification settings.
3. **Scheduled announcements** publish automatically at `scheduledFor` via a background job; the
   client never relies on the app being open to trigger publication.
4. **Read receipts** are tracked per recipient (for Admin/Principal visibility into reach), but never
   exposed to other recipients ("seen by" is an internal analytics view, not a social feature).

## Screens

- Compose Announcement (Principal/VP/Teacher/Admin, scoped by role to allowed audiences — a Teacher
  may only target their own classes, not school-wide) — see `13-FORM-STANDARDS.md`.
- Announcement Feed (all roles) — chronological, filterable, unread-first.
- Announcement Detail — full content, attachments, read-receipt summary (sender-visible only).

## Permission Matrix

| Action | Principal/VP | Admin | Teacher/Homeroom | Finance | Student/Parent |
|---|---|---|---|---|---|
| Publish school-wide | ✅ | ✅ | ❌ | ❌ | ❌ |
| Publish to own class(es) | ✅ | ➖ | ✅ | ❌ | ❌ |
| Publish to grade level | ✅ | ✅ | ❌ | ❌ | ❌ |
| View targeted announcements | ✅ | ✅ | ✅ | ✅ | ✅ |
| View read-receipt summary (own announcement) | ✅ | ✅ | ✅ | ❌ | ❌ |

## Audit Requirements

Publication actions are logged (actor, audience, timestamp) — standard retention; not financial-
grade rigor unless the content concerns safety/legal matters, in which case standard retention still
applies but content review by Principal before publication is a school-policy decision outside this
kit's enforcement scope.
