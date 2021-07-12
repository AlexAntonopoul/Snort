package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.SystemClock.sleep;


public class ServNotificate extends Service {

    private static final String PACKAGE_NAME =
            "com.example.myapplication";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    protected String ip;
    protected String decryptkey;
    int substring=0;
    int newerros=0;

    String fullString,decodefullString="";
    int k=0;
    final Handler handler = new Handler();
    Timer timer;
    final int delay = 10000;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            onDestroy();
        }


        createNotificationChannel();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ip = intent.getStringExtra("inputExtra");

            Intent intenta = new Intent(this, ServNotificate.class);

            intenta.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

            PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intenta,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, "channelid")
                    .setContentTitle("Notification Service")
                    .setContentText(ip)
                    .addAction(R.drawable.pig,"Exit", servicePendingIntent)
                    .setSmallIcon(R.drawable.pig)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

            //do heavy work on a background thread
            //stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences mPrefs = getSharedPreferences("saves", 0);

        //Sensitive Content not Publish//
        decryptkey = mPrefs.getString("dkey", "test");
        int delayl = 1500;
// delay for 0 sec.
        int period = 10000;

// repeat every 10 sec.
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() { public void run() {
            try {
                refreshing();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//Call function
        } }, delayl, period);



    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "channelid",
                    "Float Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
        timer.cancel();
        //handler.removeCallbacksAndMessages(null);
    }



    //TODO NOTIFICATION AREA************
    private NotificationManager notifManager;
    public void createNotification(String aMessage, String bMessage) {
        final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setLightColor(Color.GREEN);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(R.drawable.pig) // required
                    .setContentText(bMessage)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(this);

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)                           // required
                    .setSmallIcon(R.drawable.pig) // required
                    .setContentText(bMessage)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }


    public void refreshing() throws IOException, InterruptedException {
                //sleep(500);
                String urela=ip;
                URL yahoo = null;
                try {
                    yahoo = new URL(urela);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                BufferedReader in = null;
                try {
                    in = new BufferedReader(
                            new InputStreamReader(

                                    yahoo.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(in!=null) {

                    try {
                        fullString = in.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }




                            //fullString=decode(decryptkey,fullString.substring(0,25));
                            fullString=decode(decryptkey,fullString);



                                    //int substring1 = Integer.parseInt(fullString.substring(0,25).substring(25-4).trim());
                                    int substring1 = Integer.parseInt(fullString.substring(32-4).trim());



                                        if (substring!=substring1) {
                                            newerros=substring1-substring;
                                            createNotification("New Warnings : " + newerros, "Total Serious Warning :" + substring1);
                                        }

                                        //substring = Integer.parseInt(fullString.substring(0,25).substring(25-4).trim());
                                        substring = Integer.parseInt(fullString.substring(32-4).trim());


    }

    public String decode (String key,String text){
        String res = "";
        for (int i = 0, j = 0; i < text.length(); i++) {
            res += (char)((text.charAt(i) - key.charAt(j) + 256) % 256);
            j = ++j % key.length();
        }
        return res;
    }

    public void splitdecode(String s){
        while (s.length()>40){
            final int mid = s.length() / 2; //get the middle of the String
            String[] parts = {s.substring(0, mid),s.substring(mid)};
            splitdecode(parts[0]); //first part
            splitdecode(parts[1]); //second part
            return; }
        s=decode("test",s);
        decodefullString+=s;

    }


}