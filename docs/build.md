# Build

This app keeps the Android build setup intentionally small because it is a single-module QR and barcode scanner app built through GitHub Actions.

## Gradle

The project keeps the simple root `build.gradle` and `settings.gradle` layout because the repository has only one app module.

The root `build.gradle` defines the Android and Kotlin build plugins plus the shared repositories. The app module owns the Android app settings, version, dependencies, signing config, and release build type. The project uses Android Gradle Plugin `8.10.1`, Kotlin `2.2.21`, and Gradle `8.11.1`, which are aligned for compiling and targeting Android 16 (API 36). The Kotlin Gradle plugin and Kotlin runtime BOM use the same `2.2.21` version.

The scanner dependency baseline uses CameraX `1.5.3` consistently across `camera-core`, `camera-camera2`, `camera-lifecycle`, and `camera-view`, plus bundled on-device ML Kit barcode scanning `17.3.0`.

The repository does not commit Gradle wrapper files. The GitHub Actions release workflow downloads Gradle 8.11.1 and generates the wrapper during each run, matching the current ThinApps utility app pattern.

## Release bundle

`.github/workflows/android-release.yml` builds a signed release AAB through a manual `workflow_dispatch` run.

The workflow lets `android-actions/setup-android` install only `platform-tools`, then installs the Android 36 platform and build tools `35.0.0` explicitly. Android Gradle Plugin 8.10 uses build tools 35.0.0 as its supported default while compiling against API 36. The workflow avoids the legacy SDK `tools` package because that can pull emulator packages that are not needed for a release bundle build.

Release signing is configured through repository secrets used by the workflow.

## 16 KB page-size compatibility

The project uses Android Gradle Plugin `8.10.1`, which is newer than the `8.5.1` baseline required for correct 16 KB ZIP alignment of uncompressed native libraries in app bundles. The bundled ML Kit barcode scanner dependency is `17.3.0`, whose release added Android 16 KB page-size support.

Source configuration is not treated as final proof for a release artifact. Before production promotion, verify the exact signed AAB from GitHub Actions in Google Play's Bundle Explorer or run:

```bash
bundletool dump config --bundle=qr-code-scanner-0.11.1-release.aab | grep alignment
```

The expected result is `PAGE_ALIGNMENT_16K`. A production candidate should also complete Google Play internal testing and the pre-launch report without a 16 KB compatibility warning.

## R8 and ProGuard

R8 and ProGuard rules are not currently needed because release minification and resource shrinking are disabled in `app/build.gradle`.

The app is small, has no reflection-heavy third-party SDKs beyond the current scanner framework, and does not need custom keep rules right now. Keeping an empty `proguard-rules.pro` file only adds confusion, so the file is intentionally absent.

If release minification is enabled later, add a new `app/proguard-rules.pro` file only when there are real keep rules to maintain.
