# googlecast

A flutter plugin to connect to audio only chrome cast e.g Google Nest Mini e.t.c.

## Getting Started
1) Add the google cast sdk dependencies to your build.gradle file.
    implementation 'com.google.android.gms:play-services-cast-framework:21.0.1'
    implementation 'com.google.android.exoplayer:extension-cast:2.11.5'

2) Open your android folder and create a CastOptionsProvider class in your android project.

   import android.content.Context;
   import com.google.android.gms.cast.framework.CastOptions;
   import com.google.android.gms.cast.framework.OptionsProvider;
   import com.google.android.gms.cast.framework.SessionProvider;   
   import java.util.ArrayList;
   import java.util.List;
   
   public class CastOptionsProvider implements OptionsProvider {
       private String app_id="CC1AD845";
       public static final String CUSTOM_NAMESPACE = "urn:x-cast:radio_fm_am";
   
       @Override
       public CastOptions getCastOptions(Context context) {
           List<String> supportedNamespaces = new ArrayList<>();
           supportedNamespaces.add(CUSTOM_NAMESPACE);
           CastOptions castOptions = new CastOptions.Builder()
                   .setReceiverApplicationId(app_id)
   
                   //.setSupportedNamespaces(supportedNamespaces)
                   .build();
           return castOptions;
       }
       @Override
       public List<SessionProvider> getAdditionalSessionProviders(Context context) {
           return null;
       }
   }

Note: "CC1AD845" application id is the default Media Receiver Application id. If you want to get a custom application id you can checkout google cast sdk documentation to set it up.

3) Go to MainActivity in your project and import this.
    import com.google.android.gms.cast.framework.CastContext;
    import io.flutter.embedding.android.FlutterFragmentActivity;
4) Make your MainActivity extends FlutterFragmentActivity.
5) Add this to your MainActivity 
        @Override
        public void configureFlutterEngine(FlutterEngine flutterEngine){
            CastContext.getSharedInstance(getApplicationContext());
        }
6) Add the CastOptionsProvider to the metadata in your AndroidManifest.xml
     <meta-data
                android:name=
                    "com.google.android.gms.cast.framework.OPTIONS_PROVIDER_CLASS_NAME"
                android:value="com.radiofm.freeradio.CastOptionsProvider" />
   Note: Change the value to the CastOptionsProvider location.
   
##Usage
1) import 'package:googlecast/CastController.dart';
2) Create Cast controller
      CastController castController=new CastController();
      
3) Set media item
          await castController.setMedia(url: music_url,title: title,subtitle: subtitle);
          Note: Subtitle can be null
          
4) Load media on chromecast
    await castController.loadAudio();
    
5) Play media on chromecast 
    await castController.play();
    
6) Pause media on chromecast
          await castController.pause();
          
7) Stop media on chromecast
        await castController.stop();
        
8) Add ChromeCast Button Widget.

      GoogleCastButton(size: 35);
      
9) Listen to chromecast connection state
     GoogleChromeCast().connectionState.listen((event)async{
          if(event!=null){
            print("$event");
            if(event){
             
            }else{
              
            }
          }
        });
   Note: Return true if connected otherwise false.
   
10) Listen to chromcast state.
     GoogleChromeCast().messageStream.listen((message){
          if(message!=null){
            print("$message");
            if(message!=null){
              if(message=="PLAYING"){
                
              }else if(message=="IDLE"){
               
              } else if(message=="BUFFERING"){
                
              }else if(message=="LOADING"){
                
              }else if(message=="PAUSED"){
                
              }else if(message=="UNKNOWN"){
              
              }else if(message=="ERROR"){
                
              }
            }
          }
        });
  