#import "GooglecastPlugin.h"
#if __has_include(<googlecast/googlecast-Swift.h>)
#import <googlecast/googlecast-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "googlecast-Swift.h"
#endif

@implementation GooglecastPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftGooglecastPlugin registerWithRegistrar:registrar];
}
@end
