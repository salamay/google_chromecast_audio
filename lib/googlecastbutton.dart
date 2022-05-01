import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';
class GoogleCastButton extends StatelessWidget {
  final double size;
  const GoogleCastButton({Key? key, required this.size}) : super(key: key);
  @override
  Widget build(BuildContext context) {
   return SizedBox(
     width: size,
     height: size,
     child: _buildView(),
   );
  }

  Widget _buildView(){
    // This is used in the platform side to register the view.
    const String viewType = 'ChromeCastButton';
    // Pass parameters to the platform side.
    const Map<String, dynamic> creationParams = <String, dynamic>{};

    if(Platform.isAndroid){
      return const AndroidView(
        viewType: viewType,
        layoutDirection: TextDirection.ltr,
        creationParams: creationParams,
        creationParamsCodec: StandardMessageCodec(),
      );
    }else if(Platform.isIOS){
      return const UiKitView(
        viewType: viewType,
        layoutDirection: TextDirection.ltr,
        creationParams: creationParams,
        creationParamsCodec:  StandardMessageCodec(),
      );
    }else{
      throw UnsupportedError('Unsupported platform view');
    }
  }
}

