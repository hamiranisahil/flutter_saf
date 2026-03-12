import 'package:flutter_saf/src/flutter_saf_platform_interface.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_saf/src/flutter_saf_method_channel.dart';

void main() {
  final FlutterSafPlatform initialPlatform = FlutterSafPlatform.instance;

  test('\$MethodChannelFlutterSaf is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterSaf>());
  });
}
