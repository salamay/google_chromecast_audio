package com.salamay.googlecast;

import android.app.Activity;
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
  private ChromeCastViewFactory chromeCastViewFactory;
  private static final String chromecastconnectionstate="com.salamay.googlecast/connectionstate";
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "googlecast");
    channel.setMethodCallHandler(this);
    chromeCastViewFactory=new ChromeCastViewFactory();
    flutterPluginBinding.getPlatformViewRegistry()
            .registerViewFactory("ChromeCastButton",chromeCastViewFactory);
    new EventChannel(flutterPluginBinding.getBinaryMessenger(),chromecastconnectionstate).setStreamHandler(chromeCastSession);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if(call.method.equals("loadAudio")){
      if(call.hasArgument("url")){
        loadAudio(call);
      }
    }
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }




  @Override
  public void  onAttachedToActivity(@NonNull ActivityPluginBinding  binding) {
    Log.i(TAG,"ON ATTACHED TO ACTIVITY");
    activity = binding.getActivity();
    ChromeCastViewFactory.activity=activity;
    System.out.println("INITIALIZING GOOGLE CAST CONTEXT");
    chromeCastSession=new ChromeCastSession(activity.getApplicationContext());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding  binding) {
    Log.i(TAG,"ON RE-ATTACHED TO ACTIVITY");

    activity = binding.getActivity();
    chromeCastSession.addSessionListener();
  }

  @Override
  public void onDetachedFromActivity() {
    Log.i(TAG,"ON DETACHED TO ACTIVITY");
    activity = null;
    chromeCastSession.removeSessionListener();
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
}
