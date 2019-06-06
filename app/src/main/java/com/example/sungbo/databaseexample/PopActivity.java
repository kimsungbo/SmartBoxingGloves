package com.example.sungbo.databaseexample;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class PopActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);


        long totalMilliseconds = 8000;
        long interval = 1000;
        StartCountDownTimer(totalMilliseconds, interval);


    }

    public void StartCountDownTimer(long totalMilliseconds, long interval)
    {
        //CountDownTimer(long millisInFuture, long countDownInterval)
        new CountDownTimer(totalMilliseconds, interval)
        {
            //textview widget to display count down
            TextView tv = (TextView) findViewById(R.id.countdown_timer);
            public void onTick(long millisUntilFinished) {
                tv.setText(""+ millisUntilFinished / 1000);
            }
            public void onFinish()
            {
                //message to display when count down finished
                tv.setText("START");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }.start();
    }


}
