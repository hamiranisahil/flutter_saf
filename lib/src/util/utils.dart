/// Convert Directory path to URI String
String makeUriString({String path = '', bool isTreeUri = false}) {
  var uri = '';
  const base =
      'content://com.android.externalstorage.documents/tree/primary%3A';
  final documentUri =
      '/document/primary%3A${path.replaceAll('/', '%2F').replaceAll(' ', '%20')}';
  if (isTreeUri) {
    uri = base + path.replaceAll('/', '%2F').replaceAll(' ', '%20');
  } else {
    final pathSegments = path.split('/');
    final fileName = pathSegments[pathSegments.length - 1];
    final directory = path.split('/$fileName')[0];
    uri =
        base +
        directory.replaceAll('/', '%2F').replaceAll(' ', '%20') +
        documentUri;
  }
  return uri;
}
