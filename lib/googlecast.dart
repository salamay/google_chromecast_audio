
import 'dart:async';
import 'dart:developer';
import 'package:flutter/services.dart';
import 'dart:io';


class GoogleChromeCast {

  static const MethodChannel _channel = MethodChannel('googlecast');
  final EventChannel _connection_state_channel= const EventChannel("com.salamay.googlecast/connectionstate");
  final EventChannel _messageEvent= const EventChannel("com.salamay.googlecast/messagestream");

  late Stream<bool> _connectionstream;
  late Stream<String?> _messageStream;

  Stream<bool> get connectionState{
    _connectionstream=_connection_state_channel.receiveBroadcastStream().map<bool>((event){
      return event;
    });
    return _connectionstream;
  }

  Stream<String?> get messageStream{
    _messageStream=_messageEvent.receiveBroadcastStream().map<String?>((event) => event);
    return _messageStream;
  }

  static Future<void>castAudio(var metadata) async {
    try{
      await _channel.invokeMethod('loadAudio',metadata);
    }catch(e){
      throw Exception(e);
    }
  }

  static Future<void>playAudio() async {
    try{
      await _channel.invokeMethod('playAudio');
    }catch(e){
      throw Exception(e);
    }
  }

  static Future<void>pauseAudio() async {
    try{
      await _channel.invokeMethod('pauseAudio');
    }catch(e){
      throw Exception(e);
    }
  }

  static Future<void>stopAudio() async {
    try{
      await _channel.invokeMethod('stopAudio');
    }catch(e){
      throw Exception(e);
    }
  }
}
