package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewLog extends Activity {
    TextView floataki;
    String fullString = "";
    String [] LogTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);
        floataki = findViewById(R.id.textView2);
        Bundle extras = getIntent().getExtras();
        fullString=extras.getString("StringLog");
        refreshing();
    }


    public void refreshing()  {


        LogTable = fullString.split("nexterror");


        @SuppressLint("ResourceType") LinearLayout Basic = (LinearLayout) findViewById(R.id.view1); // Root ViewGroup in which you want to add textviews
        View view = getLayoutInflater().inflate(R.layout.logbracketmain, null);
        TextView text1 = view.findViewById(R.id.textView3);
        TextView text2 = view.findViewById(R.id.textView2);
        text2.setText("Error Log Files");
        text1.setText(LogTable[0].substring(0, 25)); //27
        Basic.addView(view);
        for (int i = LogTable.length-1; i >=1; i--) {
            View view2 = getLayoutInflater().inflate(R.layout.logbracketer, null);
            View view3 = getLayoutInflater().inflate(R.layout.logbracketwar, null);

            if (Integer.parseInt(LogTable[i].substring(LogTable[i].indexOf("Priority: ") + 10).substring(0,1))<=2)
            {
                TextView text12 = view2.findViewById(R.id.textView3);
                TextView text22 = view2.findViewById(R.id.textView2);
                text12.setText(LogTable[i].substring(0, 14));
                text22.setText(LogTable[i]);


                Basic.addView(view2);
            }
            else{
                TextView text12 = view3.findViewById(R.id.textView3);
                TextView text22 = view3.findViewById(R.id.textView2);
                text12.setText(LogTable[i].substring(1, 14));
                text22.setText(LogTable[i]);


                Basic.addView(view3);
            }




        }
        }

    }


