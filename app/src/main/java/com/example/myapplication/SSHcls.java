package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class SSHcls {

    //TODO SSH AREA************
    @SuppressLint("StaticFieldLeak")
    public void ssh(String loginame,String pass,String ip,int por,String command) {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {
                    executeRemoteCommand(loginame, pass, ip, por, command);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                return null;
            }
        }.execute(1);
    }


    public static void executeRemoteCommand(String username, String password, String hostname, int port, String command)
            throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        // Execute command
        channelssh.setCommand(command);
        channelssh.connect();
        channelssh.disconnect();

        baos.toString();
    }
}
