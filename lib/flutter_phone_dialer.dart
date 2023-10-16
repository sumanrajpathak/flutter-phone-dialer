import 'dart:async';

import 'package:flutter/services.dart';

class FlutterPhoneDialer {
  static const MethodChannel _channel = MethodChannel('flutter_phone_dialer');

  static Future<bool?> dialNumber(String number) async {
    return await _channel.invokeMethod(
      'dialNumber',
      <String, Object>{
        'number': number,
      },
    );
  }
}
