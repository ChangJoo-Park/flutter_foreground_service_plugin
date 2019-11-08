#import "FlutterForegroundPlugin.h"
#import <flutter_foreground_plugin/flutter_foreground_plugin-Swift.h>

@implementation FlutterForegroundPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterForegroundPlugin registerWithRegistrar:registrar];
}
@end
