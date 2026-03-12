# 📁 Flutter SAF (Storage Access Framework) Helper

`FlutterSaf` is a helper class designed to simplify directory access and file operations using Android’s **Storage Access Framework (SAF)** in Flutter.
It allows developers to easily request directory access, manage permissions, read file contents, and more — all through a unified API.

---

## 🚀 Features

- ✅ Request directory access
- ✅ Check and release persisted permissions
- ✅ Read file contents as bytes
- ✅ Fetch list of files in a directory
- ✅ Generate thumbnails for media files

---

## 📦 Installation

Add this package and its dependencies:

```yaml
dependencies:
  flutter_saf: 
```

Then run:

```bash
flutter pub get
```

---

## 🧩 Usage

### Step 1: Import the package

```dart
import 'package:flutter_saf/flutter_saf.dart';
```

### Step 2: Initialize with a directory path

```dart
final flutterSaf = FlutterSaf(directory: '/storage/emulated/0/Download');
```

---

## 📚 API Reference & Examples

---

### 📂 `getDirectory()`

Returns the directory path provided during initialization.

```dart
print('Selected directory: ${flutterSaf.getDirectory()}');
```

---

### 🌐 `getUriString()` and `getUri()`

Returns the directory URI in string or `Uri` form.

```dart
print('Directory URI string: ${flutterSaf.getUriString()}');

final uri = flutterSaf.getUri();
print('Directory URI object: $uri');
```

---

### 🔐 `requestDirectoryAccess()`

Requests access to the directory.
If permission already exists, returns `true`; otherwise opens the picker.

```dart
final granted = await flutterSaf.requestDirectoryAccess(allowWriteAccess: true);
if (granted == true) {
  print('Directory access granted!');
} else {
  print('Access denied or cancelled.');
}
```

---

### 🗝️ `getPersistedDirectoryPermissions()`

Retrieves all persisted directory permissions as a list.

```dart
final permissions = await flutterSaf.getPersistedDirectoryPermissions();
permissions?.forEach((p) {
  print('Persisted URI: ${p.uri}');
  print('Read: ${p.isReadPermission}, Write: ${p.isWritePermission}');
});
```

---

### 🔎 `hasPersistedDirectoryPermission()`

Checks whether your app already has access to the initialized directory.

```dart
final hasPermission = await flutterSaf.hasPersistedDirectoryPermission();
print('Has permission: $hasPermission');
```

---

### 🚫 `releasePersistedDirectoryPermission()`

Releases the directory permission previously granted by the user.

```dart
await flutterSaf.releasePersistedDirectoryPermission();
print('Permission released successfully.');
```

---

### 📄 `getContent()`

Reads and returns file content from the current directory as bytes.

```dart
final bytes = await flutterSaf.getContent();
if (bytes != null) {
  print('File size: ${bytes.lengthInBytes} bytes');
}
```

> 💡 Useful when reading files like PDFs, images, or documents.

---

### 🧾 `getFilesUri()`

Lists all files inside the selected directory.

```dart
final files = await flutterSaf.getFilesUri();
if (files != null && files.isNotEmpty) {
  for (final file in files) {
    print('File name: ${file.name}');
    print('URI: ${file.uri}');
  }
} else {
  print('No files found.');
}
```

---

### 🖼️ `getThumbnail(Uri fileUri, {int width, int height})`

Generates a thumbnail for the specified file (commonly images or videos).

```dart
final uri = Uri.parse('content://com.android.providers.media.documents/document/image%3A12345');
final thumbnailBytes = await flutterSaf.getThumbnail(uri, width: 300, height: 300);

if (thumbnailBytes != null) {
  print('Thumbnail generated successfully (${thumbnailBytes.length} bytes)');
}
```

---

## 🧠 Example Use Case

```dart
void main() async {
  final saf = FlutterSaf(directory: '/storage/emulated/0/Documents');

  final accessGranted = await saf.requestDirectoryAccess();
  if (accessGranted == true) {
    final files = await saf.getFilesUri();
    print('Found ${files?.length ?? 0} files in directory.');
  }
}
```

---

## 🧩 Models

### `DirectoryPermission`

Represents a persisted permission entry.

| Property            | Type   | Description                          |
| ------------------- | ------ | ------------------------------------ |
| `uri`               | String | Directory URI                        |
| `isReadPermission`  | bool   | Whether read access is granted       |
| `isWritePermission` | bool   | Whether write access is granted      |
| `persistedTime`     | int    | Time when the permission was granted |

### `DocumentFileModel`

Represents a document/file entry.

| Property | Type   | Description |
| -------- | ------ | ----------- |
| `name`   | String | File name   |
| `uri`    | String | File URI    |
| `type`   | String | MIME type   |
| `size`   | int    | File size   |

---

## 🧾 Logging

You can easily print the class details:

```dart
print(flutterSaf.toString());
// Output: FlutterSaf(directory: /storage/emulated/0/Download, uri: content://...)
```

---


## 🧑‍💻 Contributing

Pull requests and suggestions are welcome!
If you encounter any issues, please open an issue on GitHub.
>>>>>>> 167d86b (chore: initial commit)
