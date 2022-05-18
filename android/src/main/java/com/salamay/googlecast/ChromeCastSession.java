package com.salamay.googlecast;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.WebImage;
import com.salamay.googlecast.Model.AudioData;

import io.flutter.plugin.common.EventChannel;

public class ChromeCastSession implements EventChannel.StreamHandler{
    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private String TAG="ChromeCastSessionListener";
    public static String ACTION="com.salamay.googlecast.mediamessage";
    private RemoteMediaClient remoteMediaClient;
    private EventChannel.EventSink connectionEvent;
    private Context context;
    private static final int UNKNOWN=0;
    private static final int IDLE=1;
    private static final int PLAYING=2;
    private static final int PAUSED=3;
    private static final int BUFFERING=4;
    private static final int LOADING=5;
    private int PLAYBACKSTATE=1;

    public ChromeCastSession(Context context){
        this.context=context;
        mSessionManager = CastContext.getSharedInstance(context).getSessionManager();
        mSessionManagerListener = new ChromeCastSessionListener();
        mSessionManager.addSessionManagerListener(mSessionManagerListener, CastSession.class);
        mCastSession=mSessionManager.getCurrentCastSession();
        if(mCastSession!=null){
            updateStatus();
            startSession(mCastSession);
        }
    }
    public void endSession(){
        if(mSessionManager!=null){
            if(remoteMediaClient!=null){
                remoteMediaClient.stop();
            }
            mSessionManager.endCurrentSession(true);
            mCastSession=null;
        }
    }
    public void removeSessionListener(){
        mSessionManager.removeSessionManagerListener(mSessionManagerListener, CastSession.class);
        mCastSession = null;
    }

    public void loadMedia(AudioData audioData){
        Log.i(TAG,"LOAD MEDIA");
        Log.i(TAG,audioData.getAudioUrl());
        if(mCastSession!=null){
            MediaMetadata audioMetaData = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
            audioMetaData.putString(MediaMetadata.KEY_TITLE, audioData.getTitle());
            if(audioData.getSubtitle()!=null){
                audioMetaData.putString(MediaMetadata.KEY_SUBTITLE, audioData.getSubtitle());
            }
            if(audioData.getImgUrl()!=null){
                audioMetaData.addImage(new WebImage(Uri.parse(audioData.getImgUrl())));
            }
            MediaInfo mediaInfo = new MediaInfo.Builder(audioData.getAudioUrl())
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("audio/mp3")
                    .setMetadata(audioMetaData)
                    .build();
            PendingResult<RemoteMediaClient.MediaChannelResult> s=remoteMediaClient.load(new MediaLoadRequestData.Builder().setMediaInfo(mediaInfo).setAutoplay(true).build());
            s.addStatusListener(new PendingResult.StatusListener() {
                @Override
                public void onComplete(@NonNull Status status) {
                    System.out.println(status.getStatus());
                    int STATUS_CODE=status.getStatusCode();
                    System.out.println(status.getStatusCode());
                   if(STATUS_CODE==2100||STATUS_CODE==2001||STATUS_CODE==2104||STATUS_CODE==7){
                       Intent intent=new Intent();
                       intent.setAction(ACTION);
                       intent.putExtra("message","ERROR");
                       Log.i(TAG,"SENDING BROADCAST: STATUS ERROR");
                       context.sendBroadcast(intent);
                   }
                }
            });
        }
    }

    public void playMedia(){
        Log.i(TAG,"PLAY MEDIA");
        if(mCastSession!=null){
            if (remoteMediaClient != null) {
                if(PLAYBACKSTATE!=BUFFERING||PLAYBACKSTATE!=LOADING){
                    remoteMediaClient.play();
                }
            }
        }
    }
    
    public void pauseMedia(){
        Log.i(TAG,"PAUSE MEDIA");
        if(mCastSession!=null){
            if (remoteMediaClient != null) {
                if(PLAYBACKSTATE!=BUFFERING||PLAYBACKSTATE!=LOADING){
                    remoteMediaClient.pause();
                    Log.i(TAG,"PAUSED");
                }

            }
        }
    }
    public void stopMedia(){
        Log.i(TAG,"STOP MEDIA");
        if(mCastSession!=null){
            if (remoteMediaClient != null) {
                remoteMediaClient.stop();
            }
        }
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        this.connectionEvent=eventSink;
        if(mCastSession!=null){
            if(connectionEvent!=null){
                connectionEvent.success(true);
            }
        }
        Log.i(TAG,"LISTENING TO CHROMECAST STREAM");
    }

    @Override
    public void onCancel(Object o) {
        connectionEvent=null;
    }
    public void startSession(CastSession castSession){
        mCastSession = castSession;
        remoteMediaClient = castSession.getRemoteMediaClient();
        assert remoteMediaClient != null;
        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                super.onStatusUpdated();
                updateStatus();
            }
        });
        if(connectionEvent!=null){
            if (mCastSession.isConnected()){

                connectionEvent.success(true);
            }else{
                connectionEvent.success(false);
            }
        }
    }
    public void updateStatus(){
        int playerState=remoteMediaClient.getPlayerState();
        PLAYBACKSTATE=playerState;
        System.out.println(PLAYBACKSTATE);
        Intent intent=new Intent();
        intent.setAction(ACTION);
        if(playerState==IDLE){
            intent.putExtra("message","IDLE");
            Log.i(TAG,"SENDING BROADCAST: STATUS IDLE");
        }else if(playerState==PLAYING){
            intent.putExtra("message","PLAYING");
            Log.i(TAG,"SENDING BROADCAST: STATUS PLAYING");
        }else if(playerState==PAUSED){
            intent.putExtra("message","PAUSED");
            Log.i(TAG,"SENDING BROADCAST: STATUS PAUSED");
        }else if(playerState==BUFFERING){
            intent.putExtra("message","BUFFERING");
            Log.i(TAG,"SENDING BROADCAST: STATUS BUFFERING");
        }else if(playerState==LOADING){
            intent.putExtra("message","LOADING");
            Log.i(TAG,"SENDING BROADCAST: STATUS LOADING");
        }else if(playerState==UNKNOWN){
            intent.putExtra("message","UNKNOWN");
            Log.i(TAG,"SENDING BROADCAST: STATUS UNKNOWN");
        }
        context.sendBroadcast(intent);
    }
    private class ChromeCastSessionListener implements SessionManagerListener<CastSession>{

        @Override
        public void onSessionResumed(@NonNull CastSession castSession, boolean b) {
            Log.i(TAG,"SESSION RESUMED");
            startSession(castSession);
        }

        @Override
        public void onSessionResuming(@NonNull  CastSession castSession, @NonNull String s) {
            mCastSession = castSession;
        }

        @Override
        public void onSessionStartFailed(@NonNull CastSession castSession, int i) {
            Log.i(TAG,"SESSION START FAILED "+String.valueOf(i));
        }


        @Override
        public void onSessionStarted(@NonNull CastSession castSession, @NonNull String s) {
            Log.i(TAG,"SESSION STARTED "+s);
            startSession(castSession);
        }

        @Override
        public void onSessionStarting(@NonNull CastSession castSession) {
            Log.i(TAG,"SESSION STARTING");
        }

        @Override
        public void onSessionSuspended(@NonNull CastSession castSession, int i) {
            Log.i(TAG,"SESSION SUSPENDED");
        }

        @Override
        public void onSessionEnded(@NonNull CastSession castSession, int i) {
            Log.i(TAG,"SESSION ENDED");
            mCastSession=castSession;
            remoteMediaClient=mCastSession.getRemoteMediaClient();
            if(connectionEvent!=null){
                connectionEvent.success(false);
            }
        }

        @Override
        public void onSessionEnding(@NonNull CastSession castSession) {

        }

        @Override
        public void onSessionResumeFailed(@NonNull CastSession castSession, int i) {
            if(connectionEvent!=null){
                connectionEvent.success(false);
            }
        }
    }
}
