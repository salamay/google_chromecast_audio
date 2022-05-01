package com.salamay.googlecast;
import androidx.annotation.RequiresApi;
import androidx.mediarouter.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.ContextThemeWrapper;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;

import io.flutter.plugin.platform.PlatformView;

import java.util.Map;
import java.util.zip.Inflater;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class ChromeCastNativeView implements PlatformView {
    
    @NonNull private final MediaRouteButton chromecastbutton;
    public ChromeCastNativeView(@NonNull Context context, int id, @Nullable Map<String, Object> creationParams) {
        context.setTheme(R.style.Theme_AppCompat);
        chromecastbutton = new MediaRouteButton(context);
        CastButtonFactory.setUpMediaRouteButton(context, chromecastbutton);
    }


    @NonNull
    @Override
    public View getView() {
        return chromecastbutton;
    }

    @Override
    public void dispose() {

    }
    
}
