package changjoopark.com.flutter_foreground_plugin;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import java.util.concurrent.ScheduledExecutorService;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/**
 * FlutterForegroundPlugin
 */
public class FlutterForegroundPlugin implements FlutterPlugin, MethodCallHandler {
    public final static String START_FOREGROUND_ACTION = "com.changjoopark.flutter_foreground_plugin.action.startforeground";
    public final static String STOP_FOREGROUND_ACTION = "com.changjoopark.flutter_foreground_plugin.action.stopforeground";

    private static FlutterForegroundPlugin instance;

    private Context context;
    private MethodChannel callbackChannel;
    private BinaryMessenger messenger;
    private int methodInterval = -1;
    private long dartServiceMethodHandle = -1;
    private boolean serviceStarted = false;
    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());

    private FlutterForegroundPlugin() {}

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        System.out.println("onAttachedToEnginem (flutter binding) called!!");
        onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
    }

    public void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
        System.out.println("onAttachedToEngine called!!");
        this.messenger = messenger;
        this.context = applicationContext;
        final MethodChannel channel = new MethodChannel(this.messenger, "com.changjoopark.flutter_foreground_plugin/main");
        channel.setMethodCallHandler(this);
        callbackChannel = new MethodChannel(messenger, "com.changjoopark.flutter_foreground_plugin/callback");
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        System.out.println("onDetachedFromEngine called!!");
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        if (instance == null) {
            instance = new FlutterForegroundPlugin();
        }
        instance.onAttachedToEngine(registrar.context(), registrar.messenger());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "startForegroundService":
                final Boolean holdWakeLock = call.argument("holdWakeLock");
                final String icon = call.argument("icon");
                final String title = call.argument("title");
                final String content = call.argument("content");
                final String subtext = call.argument("subtext");
                final Boolean chronometer = call.argument("chronometer");

                launchForegroundService(icon, title, content, subtext, chronometer);
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

    private void launchForegroundService(String icon, String title, String content, String subtext, Boolean chronometer) {
        Intent intent = new Intent(this.context, FlutterForegroundService.class);
        intent.setAction(START_FOREGROUND_ACTION);
        intent.putExtra("icon", icon);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("subtext", subtext);
        intent.putExtra("chronometer", chronometer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

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

        Intent intent = new Intent(context, FlutterForegroundService.class);
        intent.setAction(STOP_FOREGROUND_ACTION);
        context.stopService(intent);

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
