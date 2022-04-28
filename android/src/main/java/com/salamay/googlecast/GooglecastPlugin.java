package com.salamay.googlecast;

import android.app.Activity;
import android.app.MediaRouteButton;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.salamay.googlecast.ChromeCastViewFactory;
import com.google.android.gms.cast.framework.CastContext;
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
  private ChromeCastSession ChromeCastSession;
  private static final String chromecastconnectionstate="com.salamay.googlecast/connectionstate";
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "googlecast");
    channel.setMethodCallHandler(this);
    flutterPluginBinding.getPlatformViewRegistry()
            .registerViewFactory("ChromeCastButton",new ChromeCastViewFactory());
    new EventChannel(flutterPluginBinding.getBinaryMessenger(),chromecastconnectionstate).setStreamHandler(ChromeCastSession);
    CastContext castContext = CastContext.getSharedInstance(flutterPluginBinding.getApplicationContext());

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
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
    ChromeCastSession=new ChromeCastSession(activity.getApplicationContext());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding  binding) {
    Log.i(TAG,"ON RE-ATTACHED TO ACTIVITY");

    activity = binding.getActivity();
    ChromeCastSession.addSessionListener();
  }

  @Override
  public void onDetachedFromActivity() {
    Log.i(TAG,"ON DETACHED TO ACTIVITY");

    activity = null;
    ChromeCastSession.removeSessionListener();
  }


}
