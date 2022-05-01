
import 'dart:async';
import 'package:flutter/services.dart';
import 'dart:io';


class GoogleChromeCast {

  static const MethodChannel _channel = MethodChannel('googlecast');
  final EventChannel connection_state_channel= const EventChannel("com.salamay.googlecast/connectionstate");
  Stream<bool> connectionstream=const Stream.empty();

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Stream<bool> connectionState(){
    connectionstream=connection_state_channel.receiveBroadcastStream().map<bool>((event) => event);
    return connectionstream;
  }

  static Future<void>castAudio(var metadata) async {
    final String? version = await _channel.invokeMethod('loadAudio',metadata);
  }

}
