import 'dart:async';

import 'package:flutter/services.dart';

/// Opens the phone dialer or places calls from a Flutter app.
///
/// Use [dialNumber] to open the system dialer pre-filled with a number
/// (no runtime permission required), or [callNumber] to place a call
/// directly (requires the `CALL_PHONE` permission on Android).
///
/// Example:
/// ```dart
/// // Open the dialer with the number pre-filled.
/// await FlutterPhoneDialer.dialNumber('+441234567890');
///
/// // Place the call directly (Android needs CALL_PHONE permission).
/// await FlutterPhoneDialer.callNumber('+441234567890');
/// ```
class FlutterPhoneDialer {
  FlutterPhoneDialer._();

  static const MethodChannel _channel = MethodChannel('flutter_phone_dialer');

  /// Opens the system phone dialer pre-filled with [number].
  ///
  /// The user still has to press the call button themselves, so no
  /// runtime permission is required on Android. On iOS the system
  /// shows a call confirmation prompt instead.
  ///
  /// [number] may contain digits, `+`, `*`, `#`, `,` and `;`.
  ///
  /// Returns `true` if the dialer was opened successfully.
  static Future<bool> dialNumber(String number) async {
    final bool? result = await _channel.invokeMethod<bool>(
      'dialNumber',
      <String, Object>{'number': number},
    );
    return result ?? false;
  }

  /// Places a call to [number] directly, without user interaction.
  ///
  /// On Android this requires the `CALL_PHONE` permission: declare
  /// `<uses-permission android:name="android.permission.CALL_PHONE"/>`
  /// in your app's `AndroidManifest.xml`. The plugin requests the runtime
  /// permission on first use; if the user denies it, this returns `false`.
  /// On devices without telephony the number is opened with a generic
  /// intent instead. On iOS, direct dialing is not allowed by the system,
  /// so this behaves like [dialNumber] (a confirmation prompt is shown).
  ///
  /// Returns `true` if the call was initiated successfully.
  static Future<bool> callNumber(String number) async {
    final bool? result = await _channel.invokeMethod<bool>(
      'callNumber',
      <String, Object>{'number': number},
    );
    return result ?? false;
  }
}
