package com.salamay.googlecast;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.salamay.googlecast.Model.AudioData;

import io.flutter.plugin.common.EventChannel;

public class ChromeCastSession implements EventChannel.StreamHandler{
    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private SessionManagerListener<CastSession> mSessionManagerListener = new ChromeCastSessionListener();
    private String TAG="ChromeCastSessionListener";
    private RemoteMediaClient remoteMediaClient;
    private EventChannel.EventSink connectionEvent;


    public ChromeCastSession(Context context) {
        mSessionManager = CastContext.getSharedInstance(context).getSessionManager();
    }


    public void addSessionListener(){
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener, CastSession.class);
    }
    public void removeSessionListener(){
        mSessionManager.removeSessionManagerListener(mSessionManagerListener, CastSession.class);
        mCastSession = null;
    }

    public void loadMedia(AudioData audioData){
        Log.i(TAG,"LOAD MEDIA");
        mCastSession = mSessionManager.getCurrentCastSession();
        Log.i(TAG,audioData.getAudioUrl());
        Log.i(TAG,"mCastSession:"+mCastSession);

        if(mCastSession!=null){
            MediaMetadata audioMetaData = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
            audioMetaData.putString(MediaMetadata.KEY_TITLE, audioData.getTitle());
            audioMetaData.putString(MediaMetadata.KEY_SUBTITLE, audioData.getSubtitle());
            audioMetaData.addImage(new WebImage(Uri.parse(audioData.getImgUrl())));
            MediaInfo mediaInfo = new MediaInfo.Builder(audioData.getAudioUrl())
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("audio/mp3")
                    .setMetadata(audioMetaData)
                    .build();
            remoteMediaClient = mCastSession.getRemoteMediaClient();
            assert remoteMediaClient != null;
            remoteMediaClient.load(new MediaLoadRequestData.Builder().setMediaInfo(mediaInfo).build());
            playMedia();
        }
    }

    private void playMedia(){
        Log.i(TAG,"PLAY MEDIA");

        if(mCastSession!=null){
            remoteMediaClient = mCastSession.getRemoteMediaClient();
            if (remoteMediaClient != null) {
                remoteMediaClient.play();
            }
        }

    }
    private void pauseMedia(){
        Log.i(TAG,"PAUSE MEDIA");
        if(mCastSession!=null){
            remoteMediaClient = mCastSession.getRemoteMediaClient();
            if (remoteMediaClient != null) {
                remoteMediaClient.pause();
            }
        }

    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        connectionEvent=eventSink;
    }

    @Override
    public void onCancel(Object o) {
        connectionEvent=null;
    }

    private class ChromeCastSessionListener implements SessionManagerListener<CastSession>{
        @Override
        public void onSessionResumed(@NonNull CastSession castSession, boolean b) {
            Log.i(TAG,"SESSION RESUMED");
            mCastSession.getCastDevice();
        }

        @Override
        public void onSessionResuming(@NonNull  CastSession castSession, @NonNull String s) {

        }

        @Override
        public void onSessionStartFailed(@NonNull CastSession castSession, int i) {

        }


        @Override
        public void onSessionStarted(@NonNull CastSession castSession, @NonNull String s) {
            Log.i(TAG,"SESSION STARTED");
            mCastSession = castSession;


        }

        @Override
        public void onSessionStarting(@NonNull CastSession castSession) {

        }

        @Override
        public void onSessionSuspended(@NonNull CastSession castSession, int i) {
            Log.i(TAG,"SESSION SUSPENDED");
        }

        @Override
        public void onSessionEnded(@NonNull CastSession castSession, int i) {
            Log.i(TAG,"SESSION ENDED");
        }

        @Override
        public void onSessionEnding(@NonNull CastSession castSession) {

        }

        @Override
        public void onSessionResumeFailed(@NonNull CastSession castSession, int i) {

        }
    }
    
}
