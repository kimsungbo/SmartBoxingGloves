package com.example.sungbo.databaseexample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.sungbo.databaseexample.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private EditText height_input;
    private EditText weight_input;
    private EditText armlength_input;
    private RadioGroup stance_input;
    private RadioButton traditional;
    private RadioButton nontraditional;
    private RadioGroup sex_input;
    private RadioButton male;
    private RadioButton female;
    private Button submitButton;

    private List<String> readDatabase = new ArrayList<>();

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mDatabase;

    private double height;
    private double weight;
    private double armlength;
    private String stance;
    private String sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final String uid = mFirebaseUser.getUid();

        submitButton = findViewById(R.id.submit);

        height_input = findViewById(R.id.height_input);
        weight_input = findViewById(R.id.weight_input);
        armlength_input = findViewById(R.id.armlength_input);
        stance_input = findViewById(R.id.radiogroup_stance);
        traditional = findViewById(R.id.stance_traditional);
        nontraditional = findViewById(R.id.stance_nontraditional);
        sex_input = findViewById(R.id.radiogroup_sex);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

            mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    height_input.setText(dataSnapshot.child("height").getValue().toString());
                    weight_input.setText(dataSnapshot.child("weight").getValue().toString());
                    armlength_input.setText(dataSnapshot.child("armlength").getValue().toString());
                    String stance_temp = dataSnapshot.child("stance").getValue().toString();
                    if(stance_temp.equals("traditional")){
                        traditional.setChecked(true);
                    }
                    else if(stance_temp.equals("nontraditional")){
                        nontraditional.setChecked(true);
                    }

                    String sex_temp = dataSnapshot.child("sex").getValue().toString();
                    if(sex_temp.equals("male")){
                        male.setChecked(true);
                    }
                    else if(sex_temp.equals("female")){
                        female.setChecked(true);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        stance_input.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Button radioButton = stance_input.findViewById(checkedId) ;
                int index = stance_input.indexOfChild(radioButton);

                if(index == 0){
                    stance = "traditional";
                }
                else if(index == 1){
                    stance="nontraditional";
                }
                Log.d("STANCE CHECKED ID", stance);

            }
        });

        sex_input.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Button radioButton = sex_input.findViewById(checkedId);
                int index = sex_input.indexOfChild(radioButton);

                if(index == 0){
                    sex = "male";
                }
                else if(index == 1){
                    sex = "female";
                }
                Log.d("SEX CHECKED ID", sex);
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                height = Double.parseDouble(height_input.getText().toString());
                weight = Double.parseDouble(weight_input.getText().toString());
                if(armlength_input.getText().toString() == null || armlength_input.getText().toString().equals("0")){
                    armlength = 0;
                }
                else{
                    armlength = Double.parseDouble(armlength_input.getText().toString());
                }

                mDatabase.child("users").child(uid).child("height").setValue(height);
                mDatabase.child("users").child(uid).child("weight").setValue(weight);
                mDatabase.child("users").child(uid).child("armlength").setValue(armlength);
                mDatabase.child("users").child(uid).child("stance").setValue(stance);
                mDatabase.child("users").child(uid).child("sex").setValue(sex);

                startActivity(new Intent(SettingsActivity.this, ModeActivity.class));
                finish();

            }
        });

    }
}
