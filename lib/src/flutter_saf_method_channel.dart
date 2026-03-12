import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_saf/src/models/directory_permission.dart';
import 'package:flutter_saf/src/models/document_file_model.dart';

import 'flutter_saf_platform_interface.dart';

/// An implementation of [FlutterSafPlatform] that uses method channels.
class MethodChannelFlutterSaf extends FlutterSafPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_saf/methods');

  /// The event channel used to interact with the native platform.
  @visibleForTesting
  final eventChannel = const EventChannel('flutter_saf/events');

  @override
  Future<bool?> requestDirectoryAccess({
    required Uri uri,
    bool allowWriteAccess = true,
  }) async {
    return methodChannel.invokeMethod<bool?>('requestDirectoryAccess', {
      'uri': uri.toString(),
      'allowWriteAccess': allowWriteAccess,
      'useDynamicPicker': false,
    });
  }

  @override
  Future<List<DirectoryPermission>?> getPersistedDirectoryPermissions() async {
    final permissions = await methodChannel.invokeMethod<List<dynamic>?>(
      'getPersistedDirectoryPermissions',
    );
    if (permissions == null) return null;
    return permissions
        .cast<Map<Object?, Object?>>()
        .map((e) => DirectoryPermission.fromMap(e.cast<String, dynamic>()))
        .toList();
  }

  @override
  Future<bool?> hasPersistedDirectoryPermission({required Uri uri}) async {
    return methodChannel.invokeMethod<bool?>(
      'hasPersistedDirectoryPermission',
      {'uri': uri.toString()},
    );
  }

  @override
  Future<void> releasePersistedDirectoryPermission({required Uri uri}) async {
    return methodChannel.invokeMethod<void>(
      'releasePersistedDirectoryPermission',
      {'uri': uri.toString()},
    );
  }

  @override
  Future<Uint8List?> getContent({required Uri uri}) async {
    return methodChannel.invokeMethod<Uint8List?>('getContent', {
      'uri': uri.toString(),
    });
  }

  @override
  Stream<Uint8List?> getContentStream({required Uri uri}) {
    final args = {'method': 'getContentStream', 'uri': uri.toString()};
    return eventChannel.receiveBroadcastStream(args).cast<Uint8List?>();
  }

  @override
  Future<List<DocumentFileModel>?> getFilesUri({required Uri uri}) async {
    try {
      final filesUri = await methodChannel.invokeMethod<List<dynamic>?>(
        'getFilesUri',
        {'uri': uri.toString()},
      );
      if (filesUri == null) return null;
      return filesUri
          .cast<Map<Object?, Object?>>()
          .map((e) => DocumentFileModel.fromMap(e.cast<String, dynamic>()))
          .toList();
    } catch (e, stackTrace) {
      throw Exception('Failed to load files: $e $stackTrace');
    }
  }

  @override
  Future<Uint8List?> getThumbnail({
    required Uri uri,
    int? width,
    int? height,
  }) async {
    try {
      return methodChannel.invokeMethod<Uint8List?>('getThumbnail', {
        'uri': uri.toString(),
        'width': width,
        'height': height,
      });
    } catch (e, stack) {
      throw Exception('Failed to generate thumbnail: $e $stack');
    }
  }

  @override
  Future<String?> cache({required Uri uri}) {
    return methodChannel.invokeMethod<String>('cache', {'uri': uri.toString()});
  }

  @override
  Future<bool?> clearCache() {
    return methodChannel.invokeMethod<bool>('clearCache');
  }
}
