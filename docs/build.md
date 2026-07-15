# Build

This app keeps the Android build setup intentionally small because it is a single-module QR and barcode scanner app built through GitHub Actions.

## Gradle

The project keeps the simple root `build.gradle` and `settings.gradle` layout because the repository has only one app module.

The root `build.gradle` defines the Android and Kotlin build plugins plus the shared repositories. The app module owns the Android app settings, version, dependencies, signing config, and release build type. The project uses Android Gradle Plugin `8.7.3`, which stays aligned with Material Components `1.13.0` while the workflows download Gradle `8.9`.

The scanner dependency baseline uses CameraX `1.5.3` consistently across `camera-core`, `camera-camera2`, `camera-lifecycle`, and `camera-view`, plus bundled on-device ML Kit barcode scanning `17.3.0`.

The repository does not commit Gradle wrapper files. The GitHub Actions workflows download Gradle 8.9 and generate the wrapper during each run, matching the current ThinApps utility app pattern.

## Release bundle

`.github/workflows/android-release.yml` builds a signed release AAB through a manual `workflow_dispatch` run.

The workflow lets `android-actions/setup-android` install only `platform-tools`, then installs the Android 35 platform and build tools explicitly. It avoids the legacy SDK `tools` package because that can pull emulator packages that are not needed for a release bundle build.

Release signing is configured through repository secrets used by the workflow.

## Manual Android lint

`.github/workflows/android-lint.yml` runs `lintRelease` only when started manually through GitHub Actions. It does not run on pushes, does not build or sign an AAB, and does not use the release keystore secrets.

The workflow creates a short-lived valid JKS keystore only so Gradle can configure the existing release build type before lint starts. The keystore is stored in the GitHub runner's temporary directory, expires after one day, and is never used to produce a signed package.

The lint job has a 30-minute timeout and uses plain Gradle console output so the Actions log stays readable. Lint findings appear in the `Run release lint` step log. The workflow also uploads `app/build/reports/lint-results-release.*` as the `android-lint-report` artifact whether lint passes or fails. Reports are retained for seven days so detailed output remains available when the console log is incomplete without creating long-term artifact clutter.

## R8 and ProGuard

R8 and ProGuard rules are not currently needed because release minification and resource shrinking are disabled in `app/build.gradle`.

The app is small, has no reflection-heavy third-party SDKs beyond the current scanner framework, and does not need custom keep rules right now. Keeping an empty `proguard-rules.pro` file only adds confusion, so the file is intentionally absent.

If release minification is enabled later, add a new `app/proguard-rules.pro` file only when there are real keep rules to maintain.
