import 'package:flutter/services.dart';
import 'package:flutter_phone_dialer/flutter_phone_dialer.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const MethodChannel channel = MethodChannel('flutter_phone_dialer');
  final List<MethodCall> log = <MethodCall>[];

  void setMockResponse(Object? response) {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall call) async {
      log.add(call);
      return response;
    });
  }

  setUp(log.clear);

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  group('dialNumber', () {
    test('invokes the platform method with the number', () async {
      setMockResponse(true);

      final bool result = await FlutterPhoneDialer.dialNumber('+441234567890');

      expect(result, isTrue);
      expect(log, hasLength(1));
      expect(log.single.method, 'dialNumber');
      expect(
        log.single.arguments,
        <String, Object>{'number': '+441234567890'},
      );
    });

    test('returns false when the platform returns false', () async {
      setMockResponse(false);

      expect(await FlutterPhoneDialer.dialNumber('123'), isFalse);
    });

    test('returns false when the platform returns null', () async {
      setMockResponse(null);

      expect(await FlutterPhoneDialer.dialNumber('123'), isFalse);
    });
  });

  group('callNumber', () {
    test('invokes the platform method with the number', () async {
      setMockResponse(true);

      final bool result = await FlutterPhoneDialer.callNumber('*400#');

      expect(result, isTrue);
      expect(log, hasLength(1));
      expect(log.single.method, 'callNumber');
      expect(log.single.arguments, <String, Object>{'number': '*400#'});
    });

    test('propagates platform exceptions', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall call) async {
        throw PlatformException(code: 'no_activity');
      });

      expect(
        () => FlutterPhoneDialer.callNumber('123'),
        throwsA(isA<PlatformException>()),
      );
    });
  });
}
