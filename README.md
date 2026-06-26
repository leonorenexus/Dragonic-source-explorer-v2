# Dragonic Source Explorer

**Leonore Tech Team** — native Android client for triggering and monitoring a public website-analysis GitHub Actions workflow.

Dragonic Source Explorer lets you submit a public website URL from your phone, fires a `workflow_dispatch` event on GitHub Actions, then watches the run, shows logs/jobs, and downloads the resulting report ZIP straight into your Downloads folder — all from a 100% native Kotlin app (no WebView, no Cordova/Capacitor/Flutter).

---

## Tech stack

| Layer            | Choice |
|-------------------|--------|
| Language          | Kotlin (100%) |
| UI                 | Jetpack Compose + Material 3 |
| Architecture       | MVVM + Repository Pattern |
| DI                 | Hilt |
| Networking         | Retrofit2 + OkHttp3 + Gson |
| Background work    | WorkManager (+ Hilt Worker) |
| Local persistence  | Room (scan history) + DataStore (settings) |
| Secure storage     | `androidx.security` EncryptedSharedPreferences (GitHub PAT) |
| Remote API         | GitHub REST API (Actions: workflow dispatch, runs, jobs, logs, artifacts) |
| CI/CD              | GitHub Actions (APK build + the website-analysis workflow) |

## Screens

`Home` · `New Scan` · `Workflow Status` · `Workflow Detail` (jobs/logs/artifacts) · `Reports` · `Downloads` · `History` · `Settings`

Dark-mode-only UI using the Leonore Tech Team signature palette: near-black background (`#050810`), neon cyan (`#22D3EE`) and magenta (`#E344F0`) accents, rounded cyberpunk cards.

---

## How it works

1. **New Scan** — you type a public URL. The app validates it's a well-formed `http(s)` address, then calls:
   `POST /repos/{owner}/{repo}/actions/workflows/{workflowFile}/dispatches` with `{"ref": "<branch>", "inputs": {"website_url": "<url>"}}`.
2. **Workflow Status / Detail** — polls `GET .../actions/runs` and `.../runs/{id}/jobs` to show live status, and `.../jobs/{id}/logs` to display plain-text logs.
3. **Downloads** — resolves `GET .../actions/artifacts/{id}/zip` (GitHub returns a 302 to a pre-signed, short-lived URL) and hands that URL to Android's `DownloadManager`, which performs the actual transfer into the public Downloads folder.
4. **Background Status Checks** — a periodic `WorkManager` job (interval configurable in Settings, 15 min minimum per Android policy) checks the latest run and fires a notification when it completes.
5. **History** — every scan you trigger is also recorded locally in a Room database, so your history works even offline or if the GitHub token expires.

### Security

- Only ever issues standard, unauthenticated-equivalent HTTP GET/POST requests to GitHub's public REST API and to whatever URL you submit — exactly what a browser would request.
- The companion `website-analysis.yml` workflow (in `.github/workflows/`) only performs **passive** analysis of the target site: a single GET request, plus checking for the public `/robots.txt` and `/sitemap.xml` files. It never attempts to bypass authentication, scan ports, brute-force paths, or touch private resources.
- The GitHub PAT is stored with AES-256 `EncryptedSharedPreferences` and is excluded from Android backups (`data_extraction_rules.xml`). `AuthInterceptor` attaches it only to requests whose host is `api.github.com`; OkHttp automatically strips the `Authorization` header on any cross-host redirect (e.g. to artifact/log storage), so the token can never leak to a third-party URL.

---

## Project structure

```
app/src/main/java/com/leonoretech/dragonicexplorer/
├── DragonicApplication.kt        # @HiltAndroidApp, WorkManager Configuration.Provider
├── MainActivity.kt
├── di/                           # Hilt modules (Network, Repository, Database)
├── data/
│   ├── model/                    # GitHub API + Room models
│   ├── remote/                   # GitHubApiService (Retrofit), AuthInterceptor
│   ├── local/                    # Room DAO/DB, DataStore settings, SecureTokenStore
│   └── repository/               # GitHubRepository (interface + impl)
├── worker/                       # WorkflowStatusWorker, WorkScheduler
├── notification/                 # NotificationHelper
├── download/                     # DownloadManagerHelper
├── util/                         # Resource<T>, UrlValidator, DateUtils
└── ui/
    ├── theme/                    # Color/Type/Shape/Theme (dark neon palette)
    ├── navigation/                # NavGraph + bottom navigation bar
    ├── components/                # Shared Composables (StatCard, StatusBadge, ...)
    └── home/ newscan/ workflowstatus/ reports/ downloads/ history/ settings/
        # Each package = one Screen.kt + one ViewModel.kt

.github/workflows/
├── android-build.yml             # Builds & uploads the debug APK on every push
└── website-analysis.yml          # workflow_dispatch target the app triggers
scripts/analyze_website.py        # Passive, public-resource-only analyzer used by the workflow above
```

---

## Setup

1. **Create/choose a GitHub repo** to host `website-analysis.yml` (this project, or any repo you control) and push this whole project to it.
2. **Generate a GitHub Personal Access Token** (fine-grained or classic) with `repo` + `workflow` scopes.
3. Open the app → **Settings**:
   - Paste your token under *GitHub Authentication* → Save Token.
   - Set *Repo Owner*, *Repo Name*, *Workflow File* (`website-analysis.yml`), and *Branch* (`main`).
4. Go to **New Scan**, type a public URL, tap **TRIGGER WORKFLOW**.
5. Watch progress in **Workflow Status** → tap a run for job logs and artifacts, or check **Downloads** to grab the report ZIP.

## Building the APK

- **Locally (Android Studio):** open the project root — Android Studio will regenerate the Gradle wrapper jar automatically on first sync.
- **Via CI (recommended on mobile-only workflows):** push to `main`, or run the `Android CI Build` workflow manually from the Actions tab. It uses `gradle/actions/setup-gradle` to build `assembleDebug` and uploads the APK as a downloadable artifact — no local Android SDK needed.

### Build notes / known limitations

- `gradle/wrapper/gradle-wrapper.jar` (a binary file) is **not** included, since it can't be generated in a text-only environment. CI works around this with the official Gradle GitHub Action; for local builds, let Android Studio regenerate the wrapper, or run `gradle wrapper` once with any local Gradle install.
- `ui/theme/Type.kt` uses system fonts (SansSerif/Monospace) by default. To match the full Leonore Tech Team brand fonts (Orbitron / JetBrains Mono), drop the `.ttf` files into `res/font/` and swap the `FontFamily` references in `Type.kt`.
- The adaptive app icon is a placeholder cyan/magenta hex badge — swap `drawable/ic_launcher_foreground.xml` for the actual Leonore lion mark when you have the asset file.
- `Reports` summarizes completed workflow runs (status/duration); deep parsing of the `report.json` artifact contents directly in-app is a natural next enhancement (currently the full JSON/HTML report is just downloaded as part of the ZIP artifact).

---

Built for the **Leonore Tech Team** — Pai Leonore.
