package changjoopark.com.flutter_foreground_plugin;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterForegroundPlugin
 */
public class FlutterForegroundPlugin implements MethodCallHandler {
    public final static String START_FOREGROUND_ACTION = "com.changjoopark.flutter_foreground_plugin.action.startforeground";
    public final static String STOP_FOREGROUND_ACTION = "com.changjoopark.flutter_foreground_plugin.action.stopforeground";

    private final Activity activity;
    private final BinaryMessenger messenger;

    private FlutterForegroundPlugin(Activity activity, BinaryMessenger messenger) {
        this.activity = activity;
        this.messenger = messenger;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "com.changjoopark.flutter_foreground_plugin/main");
        channel.setMethodCallHandler(new FlutterForegroundPlugin(registrar.activity(), registrar.messenger()));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("startForegroundService")) {
            launchForegroundService();
            result.success("startForegroundService");
        } else if (call.method.equals("stopForegroundService")) {
            stopForegroundService();
            result.success("stopForegroundService");
        } else if (call.method.equals("setServiceMethodHandle")) {
            result.success("setServiceMethodHandle");
        } else {
            result.notImplemented();
        }
    }

    private void launchForegroundService() {
        Intent intent = new Intent(activity, FlutterForegroundService.class);
        intent.setAction(START_FOREGROUND_ACTION);
        intent.putExtra("title", "TEST");
        intent.putExtra("text", "TEST");
        intent.putExtra("subText", "TEST");
//        intent.putExtra("title", (String)call.argument("title"));
//        intent.putExtra("text", (String)call.argument("text"));
//        intent.putExtra("subText", (String)call.argument("subText"));
//        intent.putExtra("ticker", (String)call.argument("ticker"));
        activity.startService(intent);
    }

    private void stopForegroundService() {
        Intent intent = new Intent(activity, FlutterForegroundService.class);
        intent.setAction(STOP_FOREGROUND_ACTION);
        activity.stopService(intent);
    }
}
