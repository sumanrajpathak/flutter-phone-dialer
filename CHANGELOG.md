## 0.1.0

* **New:** `callNumber()` places a call directly (Android: requires the
  `CALL_PHONE` permission declared by the app).
* **Behavior change:** `dialNumber()` now opens the system dialer with the
  number pre-filled (`ACTION_DIAL`) instead of placing the call, so it no
  longer needs any permission.
* **Breaking:** the plugin no longer injects the `CALL_PHONE` permission into
  consuming apps. Declare it yourself if you use `callNumber()`.
* `dialNumber()`/`callNumber()` now return a non-nullable `Future<bool>`.
* Android: fixed an Activity leak and stale permission-listener registration
  across engine/activity detach; permission denial now resolves the call
  instead of hanging; numbers are properly URI-encoded.
* Android: migrated to `namespace` (AGP 8+), compileSdk 36, minSdk 21,
  Kotlin 2.1.
* iOS: rewritten in Swift using `open(_:options:completionHandler:)`; added a
  privacy manifest (`PrivacyInfo.xcprivacy`); minimum iOS version is now 13.0;
  fixed placeholder podspec metadata.
* Requires Dart 3.1+ / Flutter 3.13+.
* Added unit tests and CI; fixed pubspec URLs and added pub.dev topics.

## 0.0.5

* Updated documentation.

## 0.0.1

* Initial release.
