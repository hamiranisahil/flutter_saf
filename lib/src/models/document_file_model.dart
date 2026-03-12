/// Represents a file or directory from Android's DocumentFile API.
class DocumentFileModel {
  /// Creates a new [DocumentFileModel] instance.
  const DocumentFileModel({
    required this.uri,
    this.name,
    this.type,
    this.length,
    this.lastModified,
  });

  /// Creates a [DocumentFileModel] from a [Map] received from Android.
  factory DocumentFileModel.fromMap(Map<String, dynamic> map) {
    return DocumentFileModel(
      name: map['name'] as String?,
      uri: map['uri'] as String,
      type: map['type'] as String?,
      length: (map['length'] is int)
          ? map['length'] as int
          : (map['length'] as num?)?.toInt(),
      lastModified: (map['lastModified'] is int)
          ? map['lastModified'] as int
          : (map['lastModified'] as num?)?.toInt(),
    );
  }

  /// File or directory name.
  final String? name;

  /// Content URI of the file or directory.
  final String uri;

  /// MIME type of the file, null for directories.
  final String? type;

  /// File size in bytes, null for directories.
  final int? length;

  /// Last modified timestamp in milliseconds since epoch.
  final int? lastModified;

  /// Converts this object back to a [Map] (for sending to Android if needed).
  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'uri': uri,
      'type': type,
      'length': length,
      'lastModified': lastModified,
    };
  }

  @override
  String toString() {
    return 'DocumentFileModel(name: $name, uri: $uri, '
        'type: $type, length: $length, lastModified: $lastModified)';
  }
}
