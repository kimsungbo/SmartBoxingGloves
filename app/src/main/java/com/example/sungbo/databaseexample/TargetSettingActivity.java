package com.example.sungbo.databaseexample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TimePicker;

public class TargetSettingActivity extends AppCompatActivity {

    private static final int MAX = 200;
    private static final int MIN = 0;

    private TimePicker timePicker;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;
    private NumberPicker straightPicker;
    private NumberPicker jabPicker;
    private NumberPicker hookPicker;
    private NumberPicker uppercutPicekr;
    private Button submitButton;

    int count_straight;
    int count_jab;
    int count_hook;
    int count_uppercut;
    int duration;
    int minute;
    int second;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_setting);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        //timePicker = findViewById(R.id.timePicker);
        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);
        straightPicker = findViewById(R.id.straightPicker);
        jabPicker = findViewById(R.id.jabPicker);
        hookPicker = findViewById(R.id.hookPicker);
        uppercutPicekr = findViewById(R.id.uppercutPicker);
        submitButton = findViewById(R.id.submitButton);


        minutePicker.setMaxValue(MAX);
        minutePicker.setMinValue(MIN);
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minute = picker.getValue();
                duration = (minute * 60) + second;
            }
        });

        secondPicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                second = picker.getValue();
                duration = minute + second;
            }
        });

        straightPicker.setMaxValue(MAX);
        straightPicker.setMinValue(MIN);
        straightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                count_straight = picker.getValue();
            }
        });

        jabPicker.setMaxValue(MAX);
        jabPicker.setMinValue(MIN);
        jabPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                count_jab = picker.getValue();
            }
        });

        hookPicker.setMaxValue(MAX);
        hookPicker.setMinValue(MIN);
        hookPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                count_hook = picker.getValue();
            }
        });

        uppercutPicekr.setMaxValue(MAX);
        uppercutPicekr.setMinValue(MIN);
        uppercutPicekr.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                count_uppercut = picker.getValue();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TargetSettingActivity.this, TargetActivity.class);
                intent.putExtra("straight", count_straight);
                intent.putExtra("jab", count_jab);
                intent.putExtra("hook", count_hook);
                intent.putExtra("uppercut", count_uppercut);
                intent.putExtra("duration", duration);
                startActivity(intent);
                finish();
            }
        });


    }
}
