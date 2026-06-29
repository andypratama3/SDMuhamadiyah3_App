# 23 — Clean Architecture

## Layers

```
presentation/   →  domain/   →   data/
(Compose UI,        (UseCases,     (Repositories impl,
 ViewModels,         Models,        remote DataSource (Ktor),
 UiState)            Repository     local DataSource (SQLDelight),
                     interfaces)    Mappers)
```

**Dependency rule:** dependencies point inward only. `presentation` depends on `domain`. `data`
depends on `domain`. `domain` depends on nothing else in the app (pure Kotlin, no Compose, no Ktor,
no SQLDelight imports). This is what makes business logic unit-testable without a UI or network.

## Feature-First Package Structure

```
com.eduocto/
├── core/
│   ├── designsystem/        (07–20 docs realized in code)
│   ├── network/             (Ktor client config, interceptors)
│   ├── database/            (SQLDelight setup, common mappers)
│   └── common/              (Result type, dispatchers, date utils)
├── feature/
│   ├── auth/
│   │   ├── presentation/
│   │   ├── domain/
│   │   └── data/
│   ├── student/
│   │   ├── presentation/
│   │   ├── domain/
│   │   └── data/
│   ├── attendance/
│   ├── finance/
│   ├── payment/
│   ├── reportcard/
│   ├── ppdb/
│   ├── announcement/
│   └── ... one package per ERP module in `ERP/`
└── app/
    └── navigation, theme entry point, DI graph assembly
```

Each `feature/*` module is internally layered (`presentation/domain/data`) and depends on `core/*`
only — features never depend directly on each other's `data`/`presentation`; cross-feature
communication goes through `domain`-level shared models in `core/common` or via navigation
arguments (IDs), never direct imports of another feature's internals.

## Repository Pattern Contract

```kotlin
// domain layer — interface only, no implementation detail leaks
interface StudentRepository {
    fun observeStudents(classId: String): Flow<List<Student>>
    suspend fun getStudent(id: String): Student?
    suspend fun refreshStudents(classId: String): Result<Unit>
    suspend fun createStudent(input: NewStudentInput): Result<Student>
}

// data layer — implementation, the only place that knows about Ktor + SQLDelight
class StudentRepositoryImpl(
    private val api: StudentApi,
    private val db: StudentQueries,
    private val mapper: StudentMapper,
) : StudentRepository {
    override fun observeStudents(classId: String): Flow<List<Student>> =
        db.selectByClass(classId).asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map(mapper::toDomain)
        }

    override suspend fun refreshStudents(classId: String): Result<Unit> = runCatching {
        val remote = api.fetchStudents(classId)
        db.transaction { remote.forEach { db.upsert(mapper.toEntity(it)) } }
    }
    // ...
}
```

## UseCases

UseCases encapsulate a single business operation when logic is non-trivial (more than a pass-through
repository call) — e.g. `CalculateOutstandingSppUseCase`, `ApprovePpdbApplicationUseCase`. Trivial
single-repository-call screens may call the repository directly from the ViewModel; introduce a
UseCase the moment logic involves more than one repository or a non-trivial business rule (keeps
with YAGNI from `00-AI-CONSTITUTION.md`/project mission).

```kotlin
class CalculateOutstandingSppUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val paymentRepository: PaymentRepository,
) {
    suspend operator fun invoke(studentId: String): OutstandingSppSummary {
        val invoices = invoiceRepository.getInvoices(studentId)
        val payments = paymentRepository.getPayments(studentId)
        // business rule lives here, once, testable in isolation
        return computeSummary(invoices, payments)
    }
}
```

## Result/Error Modeling

```kotlin
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

sealed interface AppError {
    data object NoConnection : AppError
    data class Server(val code: Int, val message: String) : AppError
    data class Validation(val fieldErrors: Map<String, String>) : AppError
    data object Unauthorized : AppError
    data object NotFound : AppError
    data class Unknown(val cause: Throwable) : AppError
}
```

Repositories/UseCases return `AppResult`/`Result<T>` rather than throwing across layer boundaries;
ViewModels map `AppError` to the appropriate `19-ERROR-STATES.md` presentation.

## Dependency Injection

DI graph (Koin or equivalent KMP-compatible DI) is assembled once in `app/`, modularized by feature
(`studentModule`, `financeModule`, ...), each declaring its own `presentation`/`domain`/`data`
bindings. No manual `new`-ing of repositories inside a ViewModel.

## Anti-Patterns

- A ViewModel importing `Ktor` or `SQLDelight` types directly.
- Domain models with platform-specific types (`android.os.Parcelable`) — domain models are pure
  Kotlin data classes, serializable via `kotlinx.serialization` if needed for navigation/caching.
- A UseCase that just forwards one call to one repository method with zero added logic (delete it,
  call the repository directly).
