# Build

This app keeps the Android build setup intentionally small because it is a single-module QR scanner app built through GitHub Actions.

## Gradle

The project keeps the simple root `build.gradle` and `settings.gradle` layout because the repository has only one app module.

The root `build.gradle` defines the Android and Kotlin build plugins plus the shared repositories. The app module owns the Android app settings, version, dependencies, signing config, and release build type.

The repository does not commit Gradle wrapper files. The release workflow downloads Gradle 8.9 and generates the wrapper during the build, matching the current ThinApps utility app pattern.

## Release bundle

`.github/workflows/android-release.yml` builds a signed release AAB through a manual `workflow_dispatch` run.

The workflow lets `android-actions/setup-android` install only `platform-tools`, then installs the Android 35 platform and build tools explicitly. It avoids the legacy SDK `tools` package because that can pull emulator packages that are not needed for a release bundle build.

The workflow expects these repository secrets:

- `RELEASE_KEYSTORE`
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

## R8 and ProGuard

R8 and ProGuard rules are not currently needed because release minification and resource shrinking are disabled in `app/build.gradle`.

The app is small, has no reflection-heavy third-party SDKs beyond the current scanner framework, and does not need custom keep rules right now. Keeping an empty `proguard-rules.pro` file only adds confusion, so the file is intentionally absent.

If release minification is enabled later, add a new `app/proguard-rules.pro` file only when there are real keep rules to maintain.
