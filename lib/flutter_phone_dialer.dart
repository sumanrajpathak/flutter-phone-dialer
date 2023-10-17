import 'dart:async';

import 'package:flutter/services.dart';

/// Example:
/// ```dart
/// void main() async {
///   await FlutterPhoneDialer.dialNumber(+441234567890);
/// }
///
class FlutterPhoneDialer {
  static const MethodChannel _channel = MethodChannel('flutter_phone_dialer');

  static Future<bool?> dialNumber(String number) async {
    /// Invokes a platform-specific method 'dialNumber' with the param 'number'.
    return await _channel.invokeMethod(
      /// The name of the platform-specific method to call.
      'dialNumber',
      <String, Object>{
        /// Pass the phone number as a parameter.
        'number': number,
      },
    );
  }
}
