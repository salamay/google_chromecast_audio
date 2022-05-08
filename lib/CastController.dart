import 'package:googlecast/googlecast.dart';

class CastController{
  late Map? _mediaData;
  GoogleChromeCast googleChromeCast=GoogleChromeCast();
  Future<void> setMedia({required String url, String? title,String? subtitle, String? imgUrl})async{
    _mediaData={
      "url":url,
      "title":title,
      "subtitle":subtitle,
      "imgUrl":imgUrl
    };
  }

  Future<void> loadAudio()async {
    print(_mediaData);
    try{
      if(_mediaData!=null){
        await GoogleChromeCast.castAudio(_mediaData);
      }else{
        throw Exception("Audio metadata is null");
      }
    }catch(e){
      throw Exception(e);
    }
  }

  Future<void> play()async{
    await GoogleChromeCast.playAudio();
  }

  Future<void> pause()async {
    await GoogleChromeCast.pauseAudio();
  }

  Future<void> stop()async {
    await GoogleChromeCast.stopAudio();
  }

}