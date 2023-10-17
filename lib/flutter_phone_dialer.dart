import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPhoneDialer {
  static const MethodChannel _channel = MethodChannel('flutter_phone_dialer');

// Define a static asynchronous function for making phone calls.
  static Future<bool?> dialNumber(String number) async {
    // Use the Flutter _channel to invoke a platform-specific method.
    return await _channel.invokeMethod(
      'dialNumber', // The name of the platform-specific method to call.
      <String, Object>{
        'number': number, // Pass the phone number as a parameter.
      },
    );
  }
}
