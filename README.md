# Flutter Phone Dialer

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/devSumanrazz/flutter-phone-dialer/blob/main/LICENSE)
[![pub package](https://img.shields.io/pub/v/flutter-phone-dialer.svg)](https://pub.dev/packages/flutter-phone-dialer)
[![package publisher](https://img.shields.io/pub/publisher/flutter-phone-dialer.svg)](https://pub.dev/publishers/sumanrajpathak.com.np/packages)

Calls Made Simple: Integrate Phone Number Dialing into Your Flutter App Effortlessly!

## Usage

Add dependency to pubspec.yaml file

```yaml
flutter_phone_dialer: ^0.0.1
```

### Android

No need any additional configuration.

### iOS

Add this to your ```info.plist``` under ```dict```

```android
<key>LSApplicationQueriesSchemes</key>
<array>
  <string>tel</string>
</array>
```

## Example

```dart
import 'package:flutter/material.dart';
import 'package:flutter_phone_dialer/flutter_phone_dialer.dart';

void main() {
  runApp(Scaffold(
    body: Center(
      child: RaisedButton(
        onPressed: _callNumber,
        child: Text('Dial Number'),
      ),
    ),
  ));
}

_callNumber() async{
  const number = '0123456789'; //enter your number here
  bool res = await FlutterPhoneDialer.dialNumber(number);
}
```
