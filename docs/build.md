# Build

This app keeps the Android build setup intentionally small because it is a single-module QR and barcode scanner app built through GitHub Actions.

## Gradle

The project keeps the simple root `build.gradle` and `settings.gradle` layout because the repository has only one app module.

The root `build.gradle` defines the Android and Kotlin build plugins plus the shared repositories. The app module owns the Android app settings, version, dependencies, signing config, and release build type. The project uses Android Gradle Plugin `8.7.3`, which stays aligned with Material Components `1.13.0` while the release workflow downloads Gradle `8.9`.

The scanner dependency baseline uses CameraX `1.5.3` consistently across `camera-core`, `camera-camera2`, `camera-lifecycle`, and `camera-view`, plus bundled on-device ML Kit barcode scanning `17.3.0`.

The repository does not commit Gradle wrapper files. The GitHub Actions release workflow downloads Gradle 8.9 and generates the wrapper during each run, matching the current ThinApps utility app pattern.

## Release bundle

`.github/workflows/android-release.yml` builds a signed release AAB through a manual `workflow_dispatch` run.

The workflow lets `android-actions/setup-android` install only `platform-tools`, then installs the Android 35 platform and build tools explicitly. It avoids the legacy SDK `tools` package because that can pull emulator packages that are not needed for a release bundle build.

Release signing is configured through repository secrets used by the workflow.

## R8 and ProGuard

R8 and ProGuard rules are not currently needed because release minification and resource shrinking are disabled in `app/build.gradle`.

The app is small, has no reflection-heavy third-party SDKs beyond the current scanner framework, and does not need custom keep rules right now. Keeping an empty `proguard-rules.pro` file only adds confusion, so the file is intentionally absent.

If release minification is enabled later, add a new `app/proguard-rules.pro` file only when there are real keep rules to maintain.
