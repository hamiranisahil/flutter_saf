import 'dart:typed_data';

import 'package:flutter_saf/src/flutter_saf_platform_interface.dart';
import 'package:flutter_saf/src/models/directory_permission.dart';
import 'package:flutter_saf/src/models/document_file_model.dart';
import 'package:flutter_saf/src/util/utils.dart';

/// A helper class to manage directory access and file operations
/// using the Storage Access Framework (SAF) in Flutter.
class FlutterSaf {
  /// Creates a [FlutterSaf] instance for the given [directory].
  FlutterSaf({required String directory, bool isTreeUri = false})
    : _directory = directory {
    _directoryUriString = makeUriString(path: _directory, isTreeUri: isTreeUri);
  }

  String? _directoryUriString;
  final String _directory;

  /// Returns the selected directory path.
  String getDirectory() {
    return _directory;
  }

  /// Returns the directory URI as a string.
  String? getUriString() {
    return _directoryUriString;
  }

  /// Returns the directory URI as a [Uri] object.
  Uri? getUri() {
    if (_directoryUriString == null) return null;
    return Uri.parse(_directoryUriString!);
  }

  /// Requests access to the directory.
  Future<bool?> requestDirectoryAccess({bool allowWriteAccess = true}) async {
    final granted = await hasPersistedDirectoryPermission();
    if (granted ?? false) return true;

    final uri = getUri();
    if (uri == null) return null;

    return FlutterSafPlatform.instance.requestDirectoryAccess(
      uri: uri,
      allowWriteAccess: allowWriteAccess,
    );
  }

  /// Returns a list of persisted directory permissions.
  Future<List<DirectoryPermission>?> getPersistedDirectoryPermissions() async {
    return FlutterSafPlatform.instance.getPersistedDirectoryPermissions();
  }

  /// Checks if the app has persisted permission to access the directory.
  Future<bool?> hasPersistedDirectoryPermission() async {
    final uri = getUri();
    if (uri == null) return null;
    return FlutterSafPlatform.instance.hasPersistedDirectoryPermission(
      uri: uri,
    );
  }

  /// Releases the persisted directory permission.
  Future<void> releasePersistedDirectoryPermission() async {
    final uri = getUri();
    if (uri == null) return;
    return FlutterSafPlatform.instance.releasePersistedDirectoryPermission(
      uri: uri,
    );
  }

  /// Reads and returns file content as bytes from the uri.
  Future<Uint8List?> getContent(Uri fileUri) async {
    return FlutterSafPlatform.instance.getContent(uri: fileUri);
  }

  /// Returns a stream of file content as bytes from the [uri].
  Stream<Uint8List?> getContentStream(Uri fileUri) {
    return FlutterSafPlatform.instance.getContentStream(uri: fileUri);
  }

  /// Returns a list of file URIs inside the selected directory.
  Future<List<DocumentFileModel>?> getFilesUri() async {
    final uri = getUri();
    if (uri == null) return null;

    return FlutterSafPlatform.instance.getFilesUri(uri: uri);
  }

  /// Returns a thumbnail (as bytes) for the given file URI.
  Future<Uint8List?> getThumbnail(
    Uri fileUri, {
    int? width,
    int? height,
  }) async {
    return FlutterSafPlatform.instance.getThumbnail(
      uri: fileUri,
      width: width,
      height: height,
    );
  }

  /// Caches the file at the given [uri] and returns its cached path.
  Future<String?> cache(Uri fileUri) {
    return FlutterSafPlatform.instance.cache(uri: fileUri);
  }

  /// Clears the entire cache directory for this platform.
  Future<bool?> clearCache() {
    return FlutterSafPlatform.instance.clearCache();
  }

  @override
  String toString() {
    return 'FlutterSaf(directory: $_directory, uri: $_directoryUriString)';
  }
}
