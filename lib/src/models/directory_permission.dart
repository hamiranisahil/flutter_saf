/// Represents a directory permission granted by the user.
class DirectoryPermission {
  /// Creates a new [DirectoryPermission].
  const DirectoryPermission({
    required this.uri,
    required this.isReadPermission,
    required this.isWritePermission,
    required this.persistedTime,
  });

  /// Creates a [DirectoryPermission] from a map.
  factory DirectoryPermission.fromMap(Map<String, dynamic> map) {
    return DirectoryPermission(
      uri: Uri.parse(map['uri'] as String),
      isReadPermission: map['isReadPermission'] as bool,
      isWritePermission: map['isWritePermission'] as bool,
      persistedTime: map['persistedTime'] as int,
    );
  }

  /// The URI of the directory.
  final Uri uri;

  /// Whether the directory has read permission.
  final bool isReadPermission;

  /// Whether the directory has write permission.
  final bool isWritePermission;

  /// The time the permission was persisted in milliseconds since epoch.
  final int persistedTime;

  /// Converts this [DirectoryPermission] to a map.
  Map<String, dynamic> toMap() {
    return {
      'uri': uri.toString(),
      'isReadPermission': isReadPermission,
      'isWritePermission': isWritePermission,
      'persistedTime': persistedTime,
    };
  }

  @override
  String toString() {
    return 'DirectoryPermission(uri: $uri, isReadPermission: $isReadPermission, isWritePermission: $isWritePermission, persistedTime: $persistedTime)';
  }
}
