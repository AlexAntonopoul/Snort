package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.Window.FEATURE_NO_TITLE;


public class MainActivity extends Activity {
    EditText ipglob,iploc,sshloc,sshglob,portedit,portloc,commanda;
    TextView changernow,iptext,log1a,log1b,log2a,log2b;
    ImageView log1image,log2image;
    String [] LogTable;
    Dialog loadingd;


    SSHcls ssHcls = new SSHcls();

    protected String ip="192.168.1.25",ipl,ipg;
    protected int port,por,porsshl,porsshg;
    boolean notifica,firstopen,loading=true,exit=true;
    final Handler handler = new Handler();
    final int delay = 8000;

    protected String loginame;
    protected String pass;
    protected String decryptkey;

    TextView floataki;
    Button connect,notbutton;
    String fullString = "",fullString1 = "",fullStringa = "",fullString1a = "",fullStringz = "",decodefullString="",decodefullStringz="",fullString2 = "",helpstring = "";
    int substring=0;
    int newerros=0;
    boolean nulan=false;
    boolean logacces=false;
    /*
    ip epilogh automata apo wifi pantou
    termatismos sto notification
    ELENXOS mesa sto service an allaxei h ip apo data se wifi
    WAKELOCK
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mPrefs = getSharedPreferences("saves", 0);

         //Sensitive Content not Publish//
         loginame = mPrefs.getString("login", "test");
         pass = mPrefs.getString("pass", "test");
         decryptkey = mPrefs.getString("dkey", "test");


         ipl = mPrefs.getString("ipl", "192.168.1.25");
         ipg = mPrefs.getString("ipg", "85.72.252.223");
         port=mPrefs.getInt("port",4448);
         porsshl=mPrefs.getInt("porsshl",22);
         porsshg=mPrefs.getInt("porsshg",2022);
         notifica =mPrefs.getBoolean("notifica",true);
         firstopen =mPrefs.getBoolean("firstopen",false);

        log1image = findViewById(R.id.log1image);
        log2image = findViewById(R.id.log2image);
        floataki = findViewById(R.id.newer);
        connect = findViewById(R.id.connect);
        iptext= findViewById(R.id.iptext);
        notbutton = findViewById(R.id.notificate);
        log1a = findViewById(R.id.log1a);
        log1b = findViewById(R.id.log1b);

        log2a = findViewById(R.id.log2a);
        log2b = findViewById(R.id.log2b);

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            ip=ipl;
            por = porsshl;
        }
        else{
            ip=ipg;
            por = porsshg;
        }



        //IP: -\nPort: -\nSSHPort: -\nStatus: OFFLINE\nWifi: -\n3G/LTE: -

        connect.setText("Disconnected");
        connect.setBackgroundColor(Color.parseColor("#f28482"));

        if (!notifica) {
            notbutton.setText("Notification OFF");
            notbutton.setBackgroundColor(Color.parseColor("#f28482"));
        }






        try {
            refreshing();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                try {
                    refreshing();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, delay);*/






        //TODO OPEN RPIUSERNAMEPASS DIALOG
        if(!firstopen){
            //first time configure passwords and username
            final Dialog passdialog=new Dialog(this);

            passdialog.requestWindowFeature(FEATURE_NO_TITLE);
            passdialog.setContentView(R.layout.passdialog);
            passdialog.setCancelable(false);
            passdialog.show();


            final EditText usnm =(EditText) passdialog.findViewById(R.id.usname);
            final EditText passw =(EditText) passdialog.findViewById(R.id.passw);
            final EditText encrypt =(EditText) passdialog.findViewById(R.id.encrypt);
            Button okb =(Button) passdialog.findViewById(R.id.dialog_apply);



            okb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //some changes here
                    loginame= usnm.getText().toString();
                    pass= passw.getText().toString();
                    decryptkey=encrypt.getText().toString();

                    if ((!loginame.equals(null)&&!loginame.isEmpty())&&(!pass.equals(null)&&!pass.isEmpty())&&(!decryptkey.equals(null)&&!decryptkey.isEmpty()) ) {

                        firstopen = true;
                        passdialog.dismiss();

                        SharedPreferences mPrefs = getSharedPreferences("saves", 0);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        mEditor.putString("login", loginame).commit();
                        mEditor.putString("pass", pass).commit();
                        mEditor.putString("dkey", decryptkey).commit();
                        mEditor.putBoolean("firstopen",firstopen).commit();

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Please Insert Username and Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }








    public void refreshing() throws IOException, InterruptedException {

        new Thread(new Runnable() {
            public void run(){
                loading=false;
                fullString="";
                fullStringz="";
                runOnUiThread (new Runnable() {
                    public void run(){
                floataki.setText("Connecting...");}});
                String urela="http://"+ip+":"+String.valueOf(port)+"/warnfilenc.txt";
                //urela="http://192.168.1.25:4448/warnfilenc.txt";
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
                        nulan=true;

                        try {
                            while ((fullString1 = in.readLine()) != null){
                             fullString+=fullString1;
                                }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        nulan=false;


                    }

                String urela1="http://"+ip+":"+String.valueOf(port)+"/serfilenc.txt";
                //String urela1="http://192.168.1.25:4448/serfilenc.txt";
                URL yahoo1 = null;
                try {
                    yahoo1 = new URL(urela1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                BufferedReader in1 = null;
                try {
                    in1 = new BufferedReader(
                            new InputStreamReader(
                                    yahoo1.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(in1!=null) {
                    nulan=true;

                    try {
                        fullStringz = in1.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        in1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    nulan=false;


                }



                new Thread(new Runnable() {
                    public void run(){
                        if(nulan){
                            logacces=false;
                            //TODO helpstring=fullString;

                            /*MULTI Split-String Decoding*/

                            //fullString="";
                            //splitdecode(helpstring);

                            /*Single String Decoding*/
                            fullString=decode(decryptkey,fullString);
                            fullStringz=decode(decryptkey,fullStringz);


                            /*Optimaization Split of Splits Decoding*/
                            /*fullString1=decode(decryptkey,fullString.substring(0,25));
                            if(Integer.parseInt(fullString1.substring(25-4).trim())<10){
                                fullString=fullString.substring(26);
                            }else if (Integer.parseInt(fullString1.substring(25-4).trim())<100){
                                fullString=fullString.substring(27);
                            }else if (Integer.parseInt(fullString1.substring(25-4).trim())<1000) {
                                fullString = fullString.substring(28);
                            }else
                            {
                                fullString = fullString.substring(29);
                            }

                            helpstring=fullString;
                            fullString="";
                            //splitdecode(helpstring);
                            */
                        }

                        runOnUiThread (new Runnable() {
                            public void run(){

                                //Toast.makeText(MainActivity.this, fullString, Toast.LENGTH_LONG).show();
                                if(!nulan){
                                    Toast.makeText(MainActivity.this, "Not Connected to Server", Toast.LENGTH_SHORT).show();
                                    connect.setText("Disconnected");
                                    connect.setBackgroundColor(Color.parseColor("#f28482"));

                                }
                                else {
                                    connect.setText("Connected");
                                    connect.setBackgroundColor(Color.parseColor("#02c39a"));



                                    //int substring1 = Integer.parseInt(fullString.substring(0,25).substring(25-4).trim());
                                    /*int substring1 = Integer.parseInt(fullString.substring(25-4).trim());

                                    if(notifica){

                                        if (substring!=substring1) {
                                            newerros=substring1-substring;
                                            //createNotification("New Warnings : " + newerros, "System Running");
                                        }

                                        //substring = Integer.parseInt(fullString.substring(0,25).substring(25-4).trim());
                                        substring = Integer.parseInt(fullString.substring(25-4).trim());
                                    }
                                    logacces=true;
                                    //floataki.setText(fullString.substring(0,25));*/
                                    LogTable = fullString.split("nexterror");


                                    LinearLayout log1id = findViewById(R.id.log1id);
                                    LinearLayout log2id = findViewById(R.id.log2id);
                                    if (LogTable.length>2) {

                                        String[] LogTable2, LogTable2a;
                                        LogTable2 = LogTable[1].split("[ .]+");
                                        LogTable2a = LogTable[1].split("[\\[\\]*{}]+");


                                        if (Integer.parseInt(LogTable[1].substring(LogTable[1].indexOf("Priority: ") + 10).substring(0, 1)) <= 2) {
                                            log1image.setBackgroundResource(R.drawable.error);
                                            log1id.setBackgroundColor(Color.parseColor("#b56576"));
                                        } else {
                                            log1image.setBackgroundResource(R.drawable.warn);
                                            log1id.setBackgroundColor(Color.parseColor("#eeb76b"));
                                            //fad586
                                        }

                                        if (Integer.parseInt(LogTable[2].substring(LogTable[2].indexOf("Priority: ") + 10).substring(0, 1)) <= 2) {
                                            log2image.setBackgroundResource(R.drawable.error);
                                            log2id.setBackgroundColor(Color.parseColor("#b56576"));

                                        } else {
                                            log2image.setBackgroundResource(R.drawable.warn);

                                            log2id.setBackgroundColor(Color.parseColor("#eeb76b"));
                                        }


                                        String[] LogTable3, LogTable3a;
                                        LogTable3 = LogTable[2].split("[ .]+");
                                        LogTable3a = LogTable[2].split("[\\[\\]*{}]+");



                                        log1a.setText(LogTable2a[3]);
                                        log1b.setText(LogTable2[0]);

                                        log2a.setText(LogTable3a[3]);
                                        log2b.setText(LogTable3[0]);



                                    }
                                    else{
                                        log1image.setBackgroundResource(R.drawable.warn);
                                        log1id.setBackgroundColor(Color.parseColor("#495057"));
                                        log1a.setText("No New Error");
                                        log1b.setText("0 errors");
                                        log2image.setBackgroundResource(R.drawable.warn);
                                        log2id.setBackgroundColor(Color.parseColor("#495057"));
                                        log2a.setText("No New Error");
                                        log2b.setText("0 errors");

                                    }
                                    iptext.setText(LogTable[0]+"\n"+fullStringz +"\nIP:"+ip+"\nPorts:"+por+" "+port);
                                    floataki.setText(LogTable[0]);
                                }
                            loading=true;
                            }
                        });


                       /* kwstas = new Timer();
                        kwstas.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        // your code here
                                        try {
                                            if(exit){
                                            refreshing();
                                            return;}
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                30000

                        );
                        new Thread(new Runnable() {
                    public void run(){


                        }
                }).start();

                        */


                    }
                }).start();

            }
        }).start();

    }


    //TODO##################### BUTTONS ######################################


    public void logopen(View view) {

        if(logacces){
            Toast.makeText(this, "Connected...Wait a Moment", Toast.LENGTH_SHORT).show();
                   }
        else if(!nulan) {
            Toast.makeText(this, "Not Connected on Server", Toast.LENGTH_SHORT).show();
        }
        else {

            new Thread(new Runnable() {
                public void run(){
            String urela2="http://"+ip+":"+String.valueOf(port)+"/logfilenc.txt";
            //String urela2="http://192.168.1.25:4448/logfilenc.txt";
            URL yahoo2 = null;
            try {
                yahoo2 = new URL(urela2);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in2 = null;
            try {
                in2 = new BufferedReader(
                        new InputStreamReader(
                                yahoo2.openStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(in2!=null) {
                nulan=true;

                try {
                    while ((fullString1a = in2.readLine()) != null){
                        fullStringa+=fullString1a;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    in2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                nulan=false;


            }

                    if(nulan){
                    runOnUiThread (new Runnable() {
                        public void run(){

                            loadingd=new Dialog(MainActivity.this);

                            loadingd.requestWindowFeature(FEATURE_NO_TITLE);
                            loadingd.setContentView(R.layout.loadialog);
                            loadingd.setCancelable(false);
                            loadingd.show();
                            //apokriptografhsh


                        }
                    });

                    splitdecode(fullStringa);
                    fullStringa=decodefullString;
                    decodefullString="";

                        runOnUiThread (new Runnable() {
                            public void run(){
                                loadingd.dismiss();
                            }
                        });




                    Intent logara = new Intent(MainActivity.this,ViewLog.class);
                    logara.putExtra("StringLog",fullStringa);
                    startActivity(logara);
                    fullStringa="";}
                    else{
                        runOnUiThread (new Runnable() {
                            public void run(){
                        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();  }
                        });
                    }


                }




            }).start();

            }
    }


    public void refreshnow(View view) throws IOException, InterruptedException {
        refreshing();
    }

    public void Run(View view) {
        ssHcls.ssh(loginame,pass,ip,por,commanda.getText().toString());
    }






    public void reboot(View view) {

        ssHcls.ssh(loginame,pass,ip,por,"sudo killall /usr/bin/python3 snort");
        //ssh("sudo killall /usr/bin/python3");
        //ssh("sudo killall snort");
    }

    public void startserver(View view) {
        ssHcls.ssh(loginame,pass,ip,por,"python3 /home/vm/Desktop/Pyt/snortart.py");
    }

    public void settings(View view) {
        exit=false;

        Intent edit=new Intent(this,edit_activity.class);
        startActivity(edit);


    }

    public void exitstatus(View view) {
        exit=false;
        finish();
    }

    //TODO CHANGERS Ip and Ports





    //TODO******************** DECODE AREA***************************
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

    //TODO NOTIFICATION AREA************

    public void servicestart(View view) {

        if(!nulan) {
            Toast.makeText(this, "Not Connected on Server", Toast.LENGTH_SHORT).show();
        }
        else {
            if (notifica) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(MainActivity.this, ServNotificate.class);
                    serviceIntent.putExtra("inputExtra", "http://" + ip + ":" + String.valueOf(port)+"/serfilenc.txt");
                    ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
                    //finish();
                } else {
                    Intent serv = new Intent(MainActivity.this, ServNotificate.class);
                    startService(serv);
                    //finish();
                }
                finish();
                exit = false;
            } else {
                Toast.makeText(this, "Enable Notification First", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void notifybutton(View view) {
        if (notifica) {
            notifica=false;
            SharedPreferences mPrefs = getSharedPreferences("saves", 0);
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putBoolean("notifica", notifica).commit();

            notbutton.setText("Notification OFF");
            notbutton.setBackgroundColor(Color.parseColor("#f28482"));
        }
        else{
            notifica=true;
            SharedPreferences mPrefs = getSharedPreferences("saves", 0);
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putBoolean("notifica", notifica).commit();

            notbutton.setText("Notification \nON");
            notbutton.setBackgroundColor(Color.parseColor("#02c39a"));

        }
    }


    //TODO Override Methods*********************

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //kwstas.cancel();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                        refreshing();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, delay);
            }
        }, delay);
        exit=true;
        SharedPreferences mPrefs = getSharedPreferences("saves", 0);
        loginame = mPrefs.getString("login", "test");
        pass = mPrefs.getString("pass", "test");
        decryptkey = mPrefs.getString("dkey", "test");
        ipl = mPrefs.getString("ipl", "192.168.1.25");
        ipg = mPrefs.getString("ipg", "85.72.252.223");
        port=mPrefs.getInt("port",4448);
        porsshl=mPrefs.getInt("porsshl",22);
        porsshg=mPrefs.getInt("porsshg",2022);




        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            ip=ipl;
            por = porsshl;
        }
        else{
            ip=ipg;
            por = porsshg;
        }



    }


    public void log2(View view) {
        final Dialog passdialog=new Dialog(this);

        passdialog.requestWindowFeature(FEATURE_NO_TITLE);
        passdialog.setContentView(R.layout.loginfo);

        TextView okb =(TextView) passdialog.findViewById(R.id.time);
        TextView content =(TextView) passdialog.findViewById(R.id.dialog_info);
        content.setText("");
        passdialog.setCancelable(true);

        String [] LogTable2,LogTable2a;
        LogTable2=LogTable[2].split("[ .]+");
        LogTable2a=LogTable[2].split("[\\[\\]*{}]+");
        okb.setText(LogTable2[0]);
        for (int i=0;i<LogTable2a.length;i++){
            content.append(LogTable2a[i]+"\n");
        }


        passdialog.show();
    }

    public void log1(View view) {
        final Dialog passdialog=new Dialog(this);

        passdialog.requestWindowFeature(FEATURE_NO_TITLE);
        passdialog.setContentView(R.layout.loginfo);

        TextView okb =(TextView) passdialog.findViewById(R.id.time);
        TextView content =(TextView) passdialog.findViewById(R.id.dialog_info);
        content.setText("");
        passdialog.setCancelable(true);

        String [] LogTable2,LogTable2a;
        LogTable2=LogTable[1].split("[ .]+");
        LogTable2a=LogTable[1].split("[\\[\\]*{}]+");
        okb.setText(LogTable2[0]);
        for (int i=0;i<LogTable2a.length;i++){
            content.append(LogTable2a[i]+"\n");
        }


        passdialog.show();
    }
    }





/*
String text = fullString;
         String key ="test";
            String res = "";
            for (int i = 0, j = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                res += (char)((c - key.charAt(j) + 256) % 256);
                j = ++j % key.length();
            }
        fullString=res;
 */






