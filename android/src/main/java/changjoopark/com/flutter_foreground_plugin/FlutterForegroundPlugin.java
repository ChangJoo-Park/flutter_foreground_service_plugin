package changjoopark.com.flutter_foreground_plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import java.util.concurrent.ScheduledExecutorService;

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
    private MethodChannel callbackChannel;
    private final BinaryMessenger messenger;
    private int methodInterval = -1;
    private long dartServiceMethodHandle = -1;
    private boolean serviceStarted = false;
    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());

    private FlutterForegroundPlugin(Activity activity, BinaryMessenger messenger) {
        this.activity = activity;
        this.messenger = messenger;
        callbackChannel = new MethodChannel(messenger, "com.changjoopark.flutter_foreground_plugin/callback");
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
                switch (call.method) {
                    case "startForegroundService":
                        final Boolean holdWakeLock = call.argument("holdWakeLock");
                        final String icon = call.argument("icon");
                        final int color = call.argument("color");
                        final String title = call.argument("title");
                        final String content = call.argument("content");
                        final String subtext = call.argument("subtext");
                        final Boolean chronometer = call.argument("chronometer");
                        final Boolean stopAction = call.argument("stop_action");
                        final String stopIcon = call.argument("stop_icon");
                        final String stopText = call.argument("stop_text");

                        launchForegroundService(icon, color, title, content, subtext, chronometer, stopAction, stopIcon, stopText);
                        result.success("startForegroundService");
                        break;
                    case "stopForegroundService":
                        stopForegroundService();
                        result.success("stopForegroundService");
                        break;
                    case "setServiceMethodInterval":
                if (call.argument("seconds") == null) {
                    result.notImplemented();
                    break;
                }

                int seconds = call.argument("seconds");
                methodInterval = seconds;
                result.success("setServiceMethodInterval");
                break;
            case "setServiceMethodHandle":
                if (call.argument("serviceMethodHandle") == null) {
                    result.notImplemented();
                    break;
                }

                long methodHandle = call.argument("serviceMethodHandle");
                dartServiceMethodHandle = methodHandle;

                result.success("setServiceMethodHandle");
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void launchForegroundService(String icon, int color, String title, String content, String subtext,
                                         Boolean chronometer, Boolean stopAction, String stopIcon,
                                         String stopText) {
        Intent intent = new Intent(activity, FlutterForegroundService.class);
        intent.setAction(START_FOREGROUND_ACTION);
        intent.putExtra("icon", icon);
        intent.putExtra("color", color);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("subtext", subtext);
        intent.putExtra("chronometer", chronometer);
        intent.putExtra("stop_action", stopAction);
        intent.putExtra("stop_icon", stopIcon);
        intent.putExtra("stop_text", stopText);

        activity.startService(intent);
        serviceStarted = true;
        startServiceLoop();

        callbackChannel.invokeMethod("onStarted", null);
    }

    /**
     *
     */
    private void stopForegroundService() {
        serviceStarted = false;

        if (handler != null) {
            handler.removeCallbacks(runnable);
        }

        dartServiceMethodHandle = -1;
        methodInterval = -1;

        Intent intent = new Intent(activity, FlutterForegroundService.class);
        intent.setAction(STOP_FOREGROUND_ACTION);
        activity.stopService(intent);

        callbackChannel.invokeMethod("onStopped", null);
    }

    /**
     *
     */
    private void startServiceLoop() {
        if (dartServiceMethodHandle == -1 || methodInterval == -1) {
            return;
        }

        final int interval = methodInterval * 1000;

        if (runnable == null) {
            runnable = new Runnable() {
                public void run() {
                    if (!serviceStarted) {
                        return;
                    }
                    try {
                        callbackChannel.invokeMethod("onServiceMethodCallback", dartServiceMethodHandle);
                    } catch (Error e) {
                        System.out.println(e);
                    }
                    handler.postDelayed(this, interval);
                }
            };
        }


        if (handler != null) {
            handler.removeCallbacks(runnable);
        }

        handler = new Handler();
        handler.postDelayed(runnable, interval);
    }
}
