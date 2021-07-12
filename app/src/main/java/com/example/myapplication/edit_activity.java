package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import static android.view.Window.FEATURE_NO_TITLE;

public class edit_activity extends Activity {

    EditText ipglob,iploc,sshloc,sshglob,portedit,portloc,commanda;
    TextView changernow;
    protected String ip,ipl,ipg;
    protected int port,por,porsshl,porsshg;
    protected String loginame;
    protected String pass;
    protected String decryptkey;
    SSHcls ssh = new SSHcls();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);
        SharedPreferences mPrefs = getSharedPreferences("saves", 0);
        loginame = mPrefs.getString("login", "test");
        pass = mPrefs.getString("pass", "test");
        decryptkey = mPrefs.getString("dkey", "test");

        ipl = mPrefs.getString("ipl", "192.168.1.25");
        ipg = mPrefs.getString("ipg", "85.72.252.223");
        port=mPrefs.getInt("port",4448);
        porsshl=mPrefs.getInt("porsshl",22);
        porsshg=mPrefs.getInt("porsshg",2022);

        commanda = (EditText) findViewById(R.id.sshcommand);
        changernow = (TextView) findViewById(R.id.now);
        ipglob = (EditText) findViewById(R.id.ipglob);
        iploc = (EditText) findViewById(R.id.iploc);
        sshglob = (EditText) findViewById(R.id.sshglob);
        sshloc = (EditText) findViewById(R.id.sshloc);
        portedit = (EditText) findViewById(R.id.portglob);
        portloc = (EditText) findViewById(R.id.portloc);

        ipglob.setText(ipg);
        iploc.setText(ipl);
        String portasshl= String.valueOf(porsshl);
        String portasshg= String.valueOf(porsshg);
        sshglob.setText(portasshg);
        sshloc.setText(portasshl);
        portloc.setText("-");

        String portag= String.valueOf(port);
        portedit.setText(portag);

    }


    public void BackBACK(View view) {
        ipg=ipglob.getText().toString();
        ipl=iploc.getText().toString();
        port=Integer.parseInt(portedit.getText().toString());
        porsshl=Integer.parseInt(sshloc.getText().toString());
        porsshg=Integer.parseInt(sshglob.getText().toString());

        SharedPreferences mPrefs = getSharedPreferences("saves", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("ipl", ipl).commit();
        mEditor.putString("ipg", ipg).commit();
        mEditor.putInt("port", port).commit();
        mEditor.putInt("porsshl", porsshl).commit();
        mEditor.putInt("porsshg", porsshg).commit();

        /*try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

         */

        finish();
    }





    public void helptut(View view) {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialoghelp);
        dialog.show();



        Button okb =(Button) dialog.findViewById(R.id.dialog_ok);


        okb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void changeIP(View view) {
            if (ip == ipl) {
                ip = ipg;
                iploc.setTextColor(Color.parseColor("#f0efeb"));
                ipglob.setTextColor(Color.parseColor("#02c39a"));


            } else {
                ip = ipl;
                ipglob.setTextColor(Color.parseColor("#f0efeb"));
                iploc.setTextColor(Color.parseColor("#02c39a"));
            }
        }


    public void changessh(View view) {
            if (por == porsshl) {
                por = porsshg;

                sshglob.setTextColor(Color.parseColor("#02c39a"));
                sshloc.setTextColor(Color.parseColor("#f0efeb"));


            } else {
                por = porsshl;

                sshglob.setTextColor(Color.parseColor("#f0efeb"));
                sshloc.setTextColor(Color.parseColor("#02c39a"));
            }
        }



    public void editpass(View view) {
        //first time configure passwords and username
        final Dialog passdialog=new Dialog(this);

        passdialog.requestWindowFeature(FEATURE_NO_TITLE);
        passdialog.setContentView(R.layout.passdialog);
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

                    passdialog.dismiss();

                    SharedPreferences mPrefs = getSharedPreferences("saves", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putString("login", loginame).commit();
                    mEditor.putString("pass", pass).commit();
                    mEditor.putString("dkey", decryptkey).commit();

                }
                else{
                    Toast.makeText(edit_activity.this, "Please Insert Username and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void Run(View view) {
        ssh.ssh(loginame,pass,ipl,porsshl,commanda.getText().toString());
        Toast.makeText(this, "Excecuting...", Toast.LENGTH_SHORT).show();

    }
}