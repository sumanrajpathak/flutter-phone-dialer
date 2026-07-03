import 'package:flutter/material.dart';
import 'package:flutter_phone_dialer/flutter_phone_dialer.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(home: DialerPage());
  }
}

class DialerPage extends StatefulWidget {
  const DialerPage({super.key});

  @override
  State<DialerPage> createState() => _DialerPageState();
}

class _DialerPageState extends State<DialerPage> {
  final TextEditingController _numberCtrl =
      TextEditingController(text: '*400#');
  String _status = '';

  @override
  void dispose() {
    _numberCtrl.dispose();
    super.dispose();
  }

  Future<void> _dial() async {
    final bool ok = await FlutterPhoneDialer.dialNumber(_numberCtrl.text);
    setState(() => _status = ok ? 'Dialer opened' : 'Could not open dialer');
  }

  Future<void> _call() async {
    final bool ok = await FlutterPhoneDialer.callNumber(_numberCtrl.text);
    setState(() => _status = ok ? 'Call started' : 'Could not start call');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Dialer Example')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: <Widget>[
            TextField(
              controller: _numberCtrl,
              decoration: const InputDecoration(labelText: 'Phone Number'),
              keyboardType: TextInputType.phone,
            ),
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                ElevatedButton(
                  onPressed: _dial,
                  child: const Text('Open Dialer'),
                ),
                const SizedBox(width: 16),
                ElevatedButton(
                  onPressed: _call,
                  child: const Text('Call Directly'),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Text(_status),
          ],
        ),
      ),
    );
  }
}
