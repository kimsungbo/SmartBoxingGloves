package com.example.sungbo.databaseexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TrainingDoneActivity extends AppCompatActivity {

    private Button okButton, viewHistoryButton;

    private TextView time, straight, jab, hook, uppercut, textViewSpeed, textViewForce;

    private int duration, count_straight, count_jab, count_hook, count_uppercut;
    private double averageSpeed, averageForce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_done);


        okButton = findViewById(R.id.ok);
        viewHistoryButton = findViewById(R.id.showHistory);

        time = findViewById(R.id.duration);
        straight = findViewById(R.id.straight);
        jab = findViewById(R.id.jab);
        hook = findViewById(R.id.hook);
        uppercut = findViewById(R.id.uppercut);
        textViewForce = findViewById(R.id.force);
        textViewSpeed = findViewById(R.id.speed);


        Intent intent = getIntent();
        duration = intent.getIntExtra("duration", 0);
        count_straight = intent.getIntExtra("straight", 0);
        count_jab = intent.getIntExtra("jab", 0);
        count_hook = intent.getIntExtra("hook", 0);
        count_uppercut = intent.getIntExtra("uppercut", 0);
        averageSpeed = intent.getDoubleExtra("averageSpeed", 0);
        averageForce = intent.getDoubleExtra("averageForce", 0);

        time.setText(toClockFormat(duration));
        straight.setText(Integer.toString(count_straight));
        jab.setText(Integer.toString(count_jab));
        hook.setText(Integer.toString(count_hook));
        uppercut.setText(Integer.toString(count_uppercut));
        textViewSpeed.setText(String.format("%.2f", averageSpeed));
        textViewForce.setText(String.format("%.2f", averageForce));

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TrainingDoneActivity.this, HistoryActivity.class));
                finish();
            }
        });

    }

    public String toClockFormat(int seconds){
        String clockformat;
        clockformat = (seconds / 60) + " : " + (seconds % 60);
        return clockformat;
    }
}
