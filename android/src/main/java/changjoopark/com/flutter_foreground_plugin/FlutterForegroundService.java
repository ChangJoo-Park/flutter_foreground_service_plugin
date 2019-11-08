package changjoopark.com.flutter_foreground_plugin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;


public class FlutterForegroundService extends Service {
    public static int ONGOING_NOTIFICATION_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "CHANNEL_ID";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == null) {
            return START_STICKY;
        }

        final String action = intent.getAction();

        switch (action) {
            case FlutterForegroundPlugin.START_FOREGROUND_ACTION:
                System.out.println(intent.toString());
                System.out.println(flags);
                System.out.println(startId);
                PackageManager pm = getApplicationContext().getPackageManager();
                Intent notificationIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                Bundle bundle = intent.getExtras();

                NotificationCompat.Builder builder;

                if (Build.VERSION.SDK_INT >= 26) {
                    String CHANNEL_ID = "snwodeer_service_channel";
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            "알람채널",
                            NotificationManager.IMPORTANCE_DEFAULT);

                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                            .createNotificationChannel(channel);

                    builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("CONTENT TITLE")
                            .setContentText("컨텐트 텍스트")
                            .setSubText("SUBTEXT ANDROID O");
                } else {
                    builder = new NotificationCompat.Builder(this)
                            .setContentTitle("CONTENT TITLE")
                            .setContentText("컨텐트 텍스트")
                            .setSubText("SUBTEXT ANDROID NOT O");
                }

                startForeground(ONGOING_NOTIFICATION_ID, builder.build());
                break;
            case FlutterForegroundPlugin.STOP_FOREGROUND_ACTION :
                System.out.println("STOP FOREGROUND ACTION");
                stopForeground(Service.STOP_FOREGROUND_DETACH);
                break;
            default:
                break;
        }

//        if (ForegroundServicePlugin.STARTFOREGROUND_ACTION.equals(intent.getAction())) {
//            PackageManager pm = getApplicationContext().getPackageManager();
//            Intent notificationIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                    notificationIntent, 0);
//
//            Bundle bundle = intent.getExtras();
//
//            Notification notification =
//                    new NotificationCompat.Builder(this)
//                            .setOngoing(true)
//                            .setPriority(NotificationCompat.PRIORITY_MAX)
//                            .setContentTitle(bundle.getString("title"))
//                            .setContentText(bundle.getString("text"))
//                            .setSubText(bundle.getString("subText"))
//                            .setTicker(bundle.getString("ticker"))
//                            .setSmallIcon(android.R.drawable.ic_dialog_info)
//                            .setContentIntent(pendingIntent)
//                            .build();
//
//            startForeground(ONGOING_NOTIFICATION_ID, notification);
//
//        } else if (ForegroundServicePlugin.STOPFOREGROUND_ACTION.equals(intent.getAction())) {
//            stopForeground(true);
//            stopSelf();
//        }
//        return START_STICKY;
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
