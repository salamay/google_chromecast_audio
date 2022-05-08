package com.salamay.googlecast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.annotation.NonNull;
import com.salamay.googlecast.ChromeCastViewFactory;
import com.salamay.googlecast.Model.AudioData;
import com.google.android.gms.cast.framework.CastContext;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/** GooglecastPlugin */
public class GooglecastPlugin implements FlutterPlugin, MethodCallHandler,ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;
  private String TAG="GooglecastPlugin";
  private ChromeCastSession chromeCastSession;
  private ChromeCastSreamHandler chromeCastSreamHandler;
  private static final String chromecastconnectionstate="com.salamay.googlecast/connectionstate";
  private static final String chromecastmediamessage="com.salamay.googlecast/messagestream";
  private EventChannel connectionstatechanenel;
  private EventChannel messagestatechannel;
  private BroadcastReceiver br;
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "googlecast");
    channel.setMethodCallHandler(this);
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory("ChromeCastButton",new ChromeCastViewFactory());
    connectionstatechanenel=new EventChannel(flutterPluginBinding.getBinaryMessenger(),chromecastconnectionstate);
    messagestatechannel=new EventChannel(flutterPluginBinding.getBinaryMessenger(), chromecastmediamessage);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if(call.method.equals("loadAudio")){
      if(call.hasArgument("url")){
        loadAudio(call);
      }
    }else if(call.method.equals("playAudio")){
      playAudio();
    }else if(call.method.equals("pauseAudio")){
      pauseAudio();
    }else if(call.method.equals("stopAudio")){
      stopMedia();
    }
    else {
      result.notImplemented();
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

  }

  @Override
  public void  onAttachedToActivity(@NonNull ActivityPluginBinding  binding) {
    setupResources(binding);
  }
  public void setupResources(@NonNull ActivityPluginBinding binding){
    Log.i(TAG,"ON ATTACHED TO ACTIVITY");
    activity = binding.getActivity();
    ChromeCastViewFactory.activity=activity;
    chromeCastSession=new ChromeCastSession(activity);
    chromeCastSreamHandler= new ChromeCastSreamHandler();
    connectionstatechanenel.setStreamHandler(chromeCastSession);
    messagestatechannel.setStreamHandler(chromeCastSreamHandler);
    br = chromeCastSreamHandler;
    IntentFilter filter = new IntentFilter(ChromeCastSession.ACTION);
    activity.getApplicationContext().registerReceiver(br, filter);
  }
  public void freeResources(){
    Log.i(TAG,"ON DETACHED TO ACTIVITY");
    activity.unregisterReceiver(br);
    channel.setMethodCallHandler(null);
    connectionstatechanenel.setStreamHandler(null);
    messagestatechannel.setStreamHandler(null);
    chromeCastSession.removeSessionListener();
    activity = null;
  }
  @Override
  public void onDetachedFromActivityForConfigChanges() {
    freeResources();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding  binding) {
    Log.i(TAG,"ON RE-ATTACHED TO ACTIVITY");
    setupResources(binding);
  }

  @Override
  public void onDetachedFromActivity() {
   freeResources();
  }

  private void loadAudio(MethodCall call){
    String url=call.argument("url");
    String title=call.argument("title");
    String subtitle=call.argument("subtitle");
    String imgUrl=call.argument("imgUrl");
    Log.i(TAG,"url: "+url);
    Log.i(TAG,"title: "+title);
    Log.i(TAG,"subtitle: "+subtitle);
    Log.i(TAG,"imgUrl: "+imgUrl);
    AudioData audioData=new AudioData();
    audioData.setTitle(title);
    audioData.setSubtitle(subtitle);
    audioData.setImgUrl(imgUrl);
    audioData.setAudioUrl(url);
    chromeCastSession.loadMedia(audioData);
  }
  
  private void playAudio(){
    chromeCastSession.playMedia();
  }
  private void pauseAudio(){
    chromeCastSession.pauseMedia();
  }
  private void stopMedia(){
    chromeCastSession.stopMedia();
  }


  public static class ChromeCastSreamHandler extends BroadcastReceiver implements EventChannel.StreamHandler {
    private EventChannel.EventSink eventSink;
    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
      this.eventSink=eventSink;
      
    }

    @Override
    public void onCancel(Object o) {
      eventSink=null;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
      if(intent!=null){
        String data=intent.getStringExtra("message");
        if(data!=null&&eventSink!=null){
          eventSink.success(data);
        }
      }
    }
  }
}
