# GitHub Actions — CI/CD Setup

## Workflows

| File | Trigger | What it does |
|------|---------|--------------|
| `ci.yml` | Push/PR ke `main`, `develop` | Build Android APK, unit tests, compile iOS framework |
| `release.yml` | Tag `v*` atau manual | Build signed AAB + APK, GitHub Release |

## CI (`ci.yml`)

Otomatis jalan setiap push/PR:

1. **Android** (Ubuntu): `compileAndroidMain` → `testAndroidHostTest` → `assembleDebug`
2. **iOS** (macOS): `compileKotlinIosSimulatorArm64` → `linkDebugFrameworkIosSimulatorArm64`

Artifact: `android-debug-apk` (14 hari)

## Release (`release.yml`)

### Tanpa signing secrets

Release build tetap jalan (unsigned) — cocok untuk internal testing.

### Dengan signing (Play Store)

Tambahkan **Repository secrets** di GitHub → Settings → Secrets and variables → Actions:

| Secret | Deskripsi |
|--------|-----------|
| `ANDROID_KEYSTORE_BASE64` | File `.jks` di-encode base64: `base64 -i release.keystore \| pbcopy` |
| `ANDROID_KEYSTORE_PASSWORD` | Password keystore |
| `ANDROID_KEY_ALIAS` | Alias key (mis. `sdm3-parent`) |
| `ANDROID_KEY_PASSWORD` | Password key |

### Membuat release

```bash
git tag v1.0.0
git push origin v1.0.0
```

Workflow akan:
- Build `androidApp-release.aab` (upload ke Play Console)
- Build `androidApp-release.apk`
- Buat GitHub Release dengan kedua file tersebut

### Manual trigger

GitHub → Actions → **Release Android** → **Run workflow**

## Firebase di CI

File asli **tidak** di-commit. CI memakai placeholder:

```bash
cp androidApp/google-services.json.example androidApp/google-services.json
```

Untuk build lokal / release asli, jalankan:

```bash
./scripts/setup-firebase-config.sh
```

Push notification **tidak** ditest di CI (butuh device fisik + config asli).

## Branch protection (disarankan)

Settings → Branches → Add rule untuk `main`:
- Require status checks: `Android Build & Test`, `iOS Framework Build`
- Require PR before merge
