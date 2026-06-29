# Login Example

A worked example showing the full pattern stack (Compose, State Management, Architecture, Design
System) applied end-to-end to one real, complete screen.

## UI State / Intent / Effect

```kotlin
@Immutable
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val fieldErrors: ImmutableMap<String, String> = persistentMapOf(),
    val error: AppError? = null,
)

sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    data object Submit : LoginIntent
}

sealed interface LoginEffect {
    data object NavigateToDashboard : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
```

## ViewModel

```kotlin
class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect: Flow<LoginEffect> = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> _uiState.update { it.copy(email = intent.value, fieldErrors = it.fieldErrors - "email") }
            is LoginIntent.PasswordChanged -> _uiState.update { it.copy(password = intent.value, fieldErrors = it.fieldErrors - "password") }
            is LoginIntent.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is LoginIntent.Submit -> submit()
        }
    }

    private fun submit() {
        val state = _uiState.value
        val errors = validate(state.email, state.password)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = errors.toImmutableMap()) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            when (val result = authRepository.login(state.email, state.password)) {
                is AppResult.Success -> _effect.send(LoginEffect.NavigateToDashboard)
                is AppResult.Failure -> _uiState.update { it.copy(isSubmitting = false, error = result.error) }
            }
        }
    }

    private fun validate(email: String, password: String): Map<String, String> = buildMap {
        if (email.isBlank()) put("email", "Email tidak boleh kosong")
        else if (!email.contains("@")) put("email", "Format email tidak valid")
        if (password.isBlank()) put("password", "Kata sandi tidak boleh kosong")
    }
}
```

## Composable (Content — pure UI)

```kotlin
@Composable
fun LoginContent(
    state: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(EduOctoTheme.colors.primary)
            .padding(EduOctoTheme.spacing.lg),
        verticalArrangement = Arrangement.Center,
    ) {
        GlassSurface(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(EduOctoTheme.spacing.lg)) {
                Text("Masuk ke EduOcto", style = EduOctoTheme.typography.titleLarge, color = EduOctoTheme.colors.onSurface)
                Spacer(Modifier.height(EduOctoTheme.spacing.lg))

                EduOctoTextField(
                    value = state.email,
                    onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
                    label = "Email",
                    errorText = state.fieldErrors["email"],
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(EduOctoTheme.spacing.md))

                EduOctoTextField(
                    value = state.password,
                    onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                    label = "Kata Sandi",
                    isPassword = true,
                    isPasswordVisible = state.isPasswordVisible,
                    onToggleVisibility = { onIntent(LoginIntent.TogglePasswordVisibility) },
                    errorText = state.fieldErrors["password"],
                    modifier = Modifier.fillMaxWidth(),
                )

                state.error?.let {
                    Spacer(Modifier.height(EduOctoTheme.spacing.sm))
                    Text(it.toUserMessage(), color = EduOctoTheme.colors.danger, style = EduOctoTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(EduOctoTheme.spacing.lg))
                EduOctoButton(
                    text = "Masuk",
                    onClick = { onIntent(LoginIntent.Submit) },
                    isLoading = state.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
```

## Screen (wiring)

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel(), onLoggedIn: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToDashboard -> onLoggedIn()
                is LoginEffect.ShowSnackbar -> { /* show via local SnackbarHostState */ }
            }
        }
    }
    LoginContent(state = state, onIntent = viewModel::onIntent)
}
```

## Previews

```kotlin
@Preview
@Composable
private fun LoginContentDefaultPreview() = EduOctoTheme { LoginContent(LoginUiState(), {}) }

@Preview
@Composable
private fun LoginContentLoadingPreview() = EduOctoTheme {
    LoginContent(LoginUiState(email = "budi@eduocto.id", password = "••••••", isSubmitting = true), {})
}

@Preview
@Composable
private fun LoginContentErrorPreview() = EduOctoTheme {
    LoginContent(LoginUiState(error = AppError.Unauthorized), {})
}

@Preview
@Composable
private fun LoginContentFieldErrorPreview() = EduOctoTheme {
    LoginContent(LoginUiState(fieldErrors = persistentMapOf("email" to "Format email tidak valid")), {})
}
```

This example demonstrates: token-only styling (`08-GLASSMORPHISM.md` auth-screen usage),
`24-STATE-MANAGEMENT.md`'s Channel-based effect pattern, `13-FORM-STANDARDS.md` on-submit
validation, and the four-preview minimum from `Checklists/SCREEN_CHECKLIST.md`.
