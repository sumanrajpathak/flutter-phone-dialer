import 'package:flutter_test/flutter_test.dart';
import 'package:phone_dialer_example/main.dart';

void main() {
  testWidgets('renders number field and action buttons',
      (WidgetTester tester) async {
    await tester.pumpWidget(const MyApp());

    expect(find.text('Phone Number'), findsOneWidget);
    expect(find.text('Open Dialer'), findsOneWidget);
    expect(find.text('Call Directly'), findsOneWidget);
  });
}
