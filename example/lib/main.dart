import 'package:flutter/material.dart';
import 'package:flutter_saf/flutter_saf.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(home: HomePage());
  }
}

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String? _platformName;
  late FlutterSaf _flutterSaf;

  @override
  void initState() {
    _flutterSaf = FlutterSaf(
      directory: 'Android/media/com.whatsapp/WhatsApp/Media/.Statuses',
    );
    _flutterSaf.hasPersistedDirectoryPermission().then(print);
    // saf.getDirectoryPermission(isDynamic: true);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('FlutterSaf Example')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (_platformName == null)
              const SizedBox.shrink()
            else
              Text(
                'Platform Name: $_platformName',
                style: Theme.of(context).textTheme.headlineSmall,
              ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () async {
                if (!context.mounted) return;
                try {
                  final has = await _flutterSaf
                      .hasPersistedDirectoryPermission();
                  print(has);

                  final permissions = await _flutterSaf
                      .getPersistedDirectoryPermissions();
                  print(permissions);
                  // final result = await _flutterSaf.getFilesUri();
                  // setState(() => _platformName = result?.length.toString());
                  // final result = await getPlatformName();
                  // setState(() => _platformName = result);
                } on Exception catch (error) {
                  if (!context.mounted) return;
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      backgroundColor: Theme.of(context).primaryColor,
                      content: Text('$error'),
                    ),
                  );
                }
              },
              child: const Text('Get Platform Name'),
            ),
          ],
        ),
      ),
    );
  }
}
