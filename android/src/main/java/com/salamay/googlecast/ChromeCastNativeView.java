package com.salamay.googlecast;
import androidx.mediarouter.app.MediaRouteButton;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.ContextThemeWrapper;

import com.google.android.gms.cast.framework.CastButtonFactory;

import io.flutter.plugin.platform.PlatformView;

import java.util.Map;


public class ChromeCastNativeView implements PlatformView {
    
    @NonNull private final MediaRouteButton chromecastbutton;

    public ChromeCastNativeView(@NonNull Context context, int id, @Nullable Map<String, Object> creationParams) {
        chromecastbutton = new MediaRouteButton(new ContextThemeWrapper(context, R.style.Theme_AppCompat_NoActionBar));
        chromecastbutton.setVisibility(View.VISIBLE);
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
