import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';

class FaceScan extends StatefulWidget {
  const FaceScan({super.key, required this.title});

  final String title;

  @override
  State<FaceScan> createState() => _FaceScanState();
}

class _FaceScanState extends State<FaceScan> {
  final String _viewType = 'LivenessCameraView';
  final Map<String, dynamic> _creationParams = <String, dynamic>{};
  String data = "";

  Widget livenessCameraView() {
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return AndroidView(
            viewType: _viewType,
            layoutDirection: TextDirection.ltr,
            creationParams: _creationParams,
            creationParamsCodec: const StandardMessageCodec());
      case TargetPlatform.iOS:
        return UiKitView(
            viewType: _viewType,
            layoutDirection: TextDirection.ltr,
            creationParams: _creationParams,
            creationParamsCodec: const StandardMessageCodec());
      default:
        throw UnsupportedError('Unsupported platform view');
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: Text(widget.title),
          backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        ),
        body: Container(
          width: double.infinity,
          height: double.infinity,
          color: Colors.grey,
          child: Stack(children: [
            const Positioned(
              top: 100,
              left: 0,
              right: 0,
              child: Text(
                "Loading...",
                style: TextStyle(
                  color: Color(0xff757575),
                  fontWeight: FontWeight.w700,
                  fontFamily: "Roboto",
                  fontStyle: FontStyle.normal,
                  fontSize: 24.0,
                ),
                textAlign: TextAlign.center,
              ),
            ),
            SizedBox(width: double.infinity, child: livenessCameraView()),
          ]),
        ),
        floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      ),
    );
  }
}
