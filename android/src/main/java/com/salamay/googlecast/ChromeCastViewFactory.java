package com.salamay.googlecast;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;
import java.util.Map;

public class ChromeCastViewFactory extends PlatformViewFactory  {
    public static Activity activity;

    public ChromeCastViewFactory(){
        super(StandardMessageCodec.INSTANCE);
    }

    @NonNull
    public ChromeCastNativeView create(@NonNull Context context, int id, @Nullable Object args) {
        final Map<String, Object> creationParams = (Map<String, Object>) args;
        return new ChromeCastNativeView(activity, id, creationParams);
    }
}
