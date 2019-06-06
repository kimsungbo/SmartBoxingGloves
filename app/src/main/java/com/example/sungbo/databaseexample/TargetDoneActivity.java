package com.example.sungbo.databaseexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TargetDoneActivity extends AppCompatActivity {

    private int straight_thrown, jab_thrown, hook_thrown, uppercut_thrown;
    private int straight_count, jab_count, hook_count, uppercut_count;
    private int straight_completion, jab_completion, hook_completion, uppercut_completion;
    private double totalforce, totalspeed;
    private int time;
    private int completion;

    private TextView straightPunch, jabPunch, hookPunch, uppercutPunch;
    private TextView straightPercent, jabPercent, hookPercent, uppercutPercent;

    private ProgressBar straightProgress, jabProgress, hookProgress, uppercutProgress;
    private TextView duration, averageForce, averageSpeed, averageCompletion;
    private Button okButton, historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_done);

        straightPunch = findViewById(R.id.straightCompletion);
        jabPunch = findViewById(R.id.jabCompletion);
        hookPunch = findViewById(R.id.hookCompletion);
        uppercutPunch = findViewById(R.id.uppercutCompletion);

        straightPercent = findViewById(R.id.straightCompletionPercent);
        jabPercent = findViewById(R.id.jabCompletionPercent);
        hookPercent = findViewById(R.id.hookCompletionPercent);
        uppercutPercent = findViewById(R.id.uppercutCompletionPercent);

        straightProgress = findViewById(R.id.progressbar_s);
        jabProgress = findViewById(R.id.progressbar_j);
        hookProgress = findViewById(R.id.progressbar_h);
        uppercutProgress = findViewById(R.id.progressbar_u);

        averageForce = findViewById(R.id.averageForce);
        averageSpeed = findViewById(R.id.averageSpeed);
        averageCompletion = findViewById(R.id.averageCompletion);

        duration = findViewById(R.id.textViewDuration);
        okButton = findViewById(R.id.okButton);
        historyButton = findViewById(R.id.viewHistory);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TargetDoneActivity.this, HistoryActivity.class));
                finish();
            }
        });

        Intent intent = getIntent();
        straight_thrown = intent.getIntExtra("straight_thrown", 0);
        straight_count = intent.getIntExtra("straight_count", 0);
        jab_thrown = intent.getIntExtra("jab_thrown", 0);
        jab_count = intent.getIntExtra("jab_count", 0);
        hook_thrown = intent.getIntExtra("hook_thrown", 0);
        hook_count = intent.getIntExtra("hook_count", 0);
        uppercut_thrown = intent.getIntExtra("uppercut_thrown", 0);
        uppercut_count = intent.getIntExtra("uppercut_count", 0);
        time = intent.getIntExtra("duration", 0);
        completion = intent.getIntExtra("completion", 0);
        totalforce  = intent.getDoubleExtra("totalForce", 0);
        totalspeed  = intent.getDoubleExtra("totalSpeed", 0);

        duration.setText(toClockFormat(time));
        straightPunch.setText(straight_thrown + "/" + straight_count);
        jabPunch.setText(jab_thrown + "/" + jab_count);
        hookPunch.setText(hook_thrown + "/" + hook_count);
        uppercutPunch.setText(uppercut_thrown + "/" + uppercut_count);

        int tempPercentStraight = calculatePercent(straight_thrown, straight_count);
        int tempPercentJab = calculatePercent(jab_thrown , jab_count);
        int tempPercentHook = calculatePercent(hook_thrown , hook_count);
        int tempPercentUppercut = calculatePercent(uppercut_thrown , uppercut_count);

        straightPercent.setText(String.valueOf(tempPercentStraight) + "%");
        jabPercent.setText(String.valueOf(tempPercentJab) + "%");
        hookPercent.setText(String.valueOf(tempPercentHook) + "%");
        uppercutPercent.setText(String.valueOf(tempPercentUppercut) + "%");

        straightProgress.setProgress(tempPercentStraight);
        jabProgress.setProgress(tempPercentJab);
        hookProgress.setProgress(tempPercentHook);
        uppercutProgress.setProgress(tempPercentUppercut);

        double tempAverageForce = totalforce / (straight_thrown + jab_thrown + hook_thrown + uppercut_thrown);
        double tempAverageSpeed = totalspeed / (straight_thrown + jab_thrown + hook_thrown + uppercut_thrown);

        averageForce.setText(String.format("%.2f", tempAverageForce));
        averageSpeed.setText(String.format("%.2f", tempAverageSpeed));

        int tempAverageCompletion= (int)((double)(tempPercentStraight + tempPercentJab + tempPercentHook + tempPercentUppercut) / (double)4);
        averageCompletion.setText(String.valueOf(tempAverageCompletion));
    }

    public int calculatePercent(int thrown, int target){
        if(thrown == 0 && target == 0){
            return 100;
        }else if(thrown == 0 && target != 0 ){
            return 0;
        }
        else{
            int temp =  (int)((double)thrown / (double)target * 100);
            if( temp > 100 ){
                return 100;
            }
            else{
                return temp;
            }
        }
    }


    public String toClockFormat(int seconds){
        String clockformat;
        clockformat = (seconds / 60) + " : " + (seconds % 60);
        return clockformat;
    }
}
