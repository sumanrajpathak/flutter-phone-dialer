# Flutter Phone Dialer

[![CI](https://github.com/sumanrajpathak/flutter-phone-dialer/actions/workflows/ci.yml/badge.svg)](https://github.com/sumanrajpathak/flutter-phone-dialer/actions/workflows/ci.yml)
[![GitHub license](https://img.shields.io/badge/License-BSD_2--Clause-orange.svg)](https://github.com/sumanrajpathak/flutter-phone-dialer/blob/main/LICENSE)
[![pub package](https://img.shields.io/pub/v/flutter_phone_dialer.svg)](https://pub.dev/packages/flutter_phone_dialer)
[![package publisher](https://img.shields.io/pub/publisher/flutter_phone_dialer.svg)](https://pub.dev/publishers/sumanrajpathak.com.np/packages)

Open the phone dialer or place calls from your Flutter app on Android and iOS.

- **`dialNumber`** opens the system dialer pre-filled with the number. The user
  presses the call button — **no permission required**.
- **`callNumber`** places the call directly (Android only; requires the
  `CALL_PHONE` permission).

## Getting started

```yaml
dependencies:
  flutter_phone_dialer: ^0.1.0
```

```dart
import 'package:flutter_phone_dialer/flutter_phone_dialer.dart';

// Open the dialer with the number pre-filled (no permission needed).
await FlutterPhoneDialer.dialNumber('+441234567890');

// Place the call directly (see platform setup below).
await FlutterPhoneDialer.callNumber('+441234567890');
```

Both methods return a `Future<bool>` indicating whether the dialer/call was
started. USSD-style codes work too, e.g. `dialNumber('*400#')`.

## Platform setup

### Android

`dialNumber` works out of the box — no configuration or permission needed.

`callNumber` requires the `CALL_PHONE` permission. Add it to your app's
`android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CALL_PHONE" />
```

The plugin requests the runtime permission on first use; if the user denies
it, `callNumber` returns `false`. Note that Google Play restricts apps that
use `CALL_PHONE`, so prefer `dialNumber` unless you truly need direct calls.

### iOS

iOS never allows placing a call without user interaction, so both methods
show the system call prompt. Add the `tel` scheme to your `Info.plist` (the
plugin uses `canOpenURL` to check that calling is available):

```xml
<key>LSApplicationQueriesSchemes</key>
<array>
  <string>tel</string>
</array>
```

## Complete example

```dart
import 'package:flutter/material.dart';
import 'package:flutter_phone_dialer/flutter_phone_dialer.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: ElevatedButton(
            onPressed: () => FlutterPhoneDialer.dialNumber('+441234567890'),
            child: const Text('Dial Number'),
          ),
        ),
      ),
    );
  }
}
```

See the [example](example) directory for a runnable app.

## License

[BSD 2-Clause](LICENSE)
