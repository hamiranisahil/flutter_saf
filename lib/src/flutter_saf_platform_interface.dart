import 'dart:typed_data';

import 'package:flutter_saf/src/models/directory_permission.dart';
import 'package:flutter_saf/src/models/document_file_model.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_saf_method_channel.dart';

abstract class FlutterSafPlatform extends PlatformInterface {
  /// Constructs a FlutterSafPlatform.
  FlutterSafPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterSafPlatform _instance = MethodChannelFlutterSaf();

  /// The default instance of [FlutterSafPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterSaf].
  static FlutterSafPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterSafPlatform] when
  /// they register themselves.
  static set instance(FlutterSafPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool?> requestDirectoryAccess({
    required Uri uri,
    bool allowWriteAccess = true,
  }) {
    throw UnimplementedError(
      'requestDirectoryAccess() has not been implemented.',
    );
  }

  Future<List<DirectoryPermission>?> getPersistedDirectoryPermissions() {
    throw UnimplementedError(
      'getPersistedDirectoryPermissions() has not been implemented.',
    );
  }

  Future<bool?> hasPersistedDirectoryPermission({required Uri uri}) {
    throw UnimplementedError(
      'hasPersistedDirectoryPermission() has not been implemented.',
    );
  }

  Future<void> releasePersistedDirectoryPermission({required Uri uri}) {
    throw UnimplementedError(
      'releasePersistedDirectoryPermission() has not been implemented.',
    );
  }

  Future<Uint8List?> getContent({required Uri uri}) {
    throw UnimplementedError('getContent() has not been implemented.');
  }

  Stream<Uint8List?> getContentStream({required Uri uri}) {
    throw UnimplementedError('getContentStream() has not been implemented.');
  }

  Future<List<DocumentFileModel>?> getFilesUri({required Uri uri}) {
    throw UnimplementedError('getFilesUri() has not been implemented.');
  }

  Future<Uint8List?> getThumbnail({required Uri uri, int? width, int? height}) {
    throw UnimplementedError('getThumbnail() has not been implemented.');
  }

  Future<String?> cache({required Uri uri}) {
    throw UnimplementedError('cache() has not been implemented.');
  }

  Future<bool?> clearCache() {
    throw UnimplementedError('clearCache() has not been implemented.');
  }
}
