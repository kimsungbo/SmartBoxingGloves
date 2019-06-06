package com.example.sungbo.databaseexample;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sungbo.databaseexample.Model.TargetHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HistoryActivity extends FragmentActivity {

    private Button btnYearMonthPicker;
    private int selectedMonth;
    private int selectedYear;
    private TextView selectedDate;
    private Spinner mode_spinner;
    private String[] months = {"Jan", "Feb", "Mar", "April", "May", "Jun", "July", "August", "Sept", "Nov", "Oct", "Dec"};

    private TextView totalPunches, totalSessions, averageSpeed, averageForce;

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private String uid;

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
            selectedYear = year;
            selectedMonth = monthOfYear;
            selectedDate.setText(months[selectedMonth-1] +", " + year);

            if(mode_spinner.getSelectedItemPosition() == 0){



                Fragment fr = new TrainingFragment() ;
                Bundle args = new Bundle();
                args.putInt("selectedMonth", selectedMonth);
                args.putInt("selectedYear", selectedYear);
                fr.setArguments(args);

                FragmentManager mFragmentManager = getSupportFragmentManager();
                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.fragmentSelect, fr);
                mFragmentTransaction.commit();

                mDatabase.child("users").child(uid).child("TrainingHistorySummary").child(String.valueOf(selectedYear))
                        .child(String.valueOf(selectedMonth)).addValueEventListener(new ValueEventListener()  {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if(dataSnapshot.child("totalPunches").exists()) {
                            int tempTotalPunches = Integer.parseInt(dataSnapshot.child("totalPunches").getValue().toString());
                            totalPunches.setText(dataSnapshot.child("totalPunches").getValue().toString());
                            totalSessions.setText(dataSnapshot.child("totalSessions").getValue().toString());
                            if(tempTotalPunches == 0){
                                averageSpeed.setText("0");
                                averageForce.setText("0");

                            }
                            else{
                                double tempAverageSpeed = Double.parseDouble(dataSnapshot.child("totalSpeed").getValue().toString()) / tempTotalPunches;
                                averageSpeed.setText(String.format("%.2f", tempAverageSpeed));
                                double tempAverageForce = Double.parseDouble(dataSnapshot.child("totalForce").getValue().toString()) / tempTotalPunches;
                                averageForce.setText(String.format("%.2f", tempAverageForce));
                            }

                        }
                        else{
                            totalPunches.setText("0");
                            totalSessions.setText("0");
                            averageSpeed.setText("0");
                            averageForce.setText("0");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else if(mode_spinner.getSelectedItemPosition() == 1){

                Bundle args = new Bundle();
                args.putInt("selectedMonth", selectedMonth);
                args.putInt("selectedYear", selectedYear);
                Fragment fr = new TargetFragment() ;
                fr.setArguments(args);
                FragmentManager mFragmentManager = getSupportFragmentManager();
                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.fragmentSelect, fr);
                mFragmentTransaction.commit();

                mDatabase.child("users").child(uid).child("TargetHistorySummary").child(String.valueOf(selectedYear))
                        .child(String.valueOf(selectedMonth)).addValueEventListener(new ValueEventListener()  {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child("totalPunches").exists()) {
                            int tempTotalPunches = Integer.parseInt(dataSnapshot.child("totalPunches").getValue().toString());
                            totalPunches.setText(dataSnapshot.child("totalPunches").getValue().toString());
                            totalSessions.setText(dataSnapshot.child("totalSessions").getValue().toString());
                            if(tempTotalPunches == 0){
                                averageSpeed.setText("0");
                                averageForce.setText("0");

                            }
                            else{
                                float tempAverageSpeed = (float)(Double.parseDouble(dataSnapshot.child("totalForce").getValue().toString()) / tempTotalPunches);
                                averageSpeed.setText(String.format("%.2f", tempAverageSpeed));
                                float tempAverageForce = (float)(Double.parseDouble(dataSnapshot.child("totalSpeed").getValue().toString()) / tempTotalPunches);
                                averageForce.setText(String.format("%.2f", tempAverageForce));
                            }

                        }
                        else{
                            totalPunches.setText("0");
                            totalSessions.setText("0");
                            averageSpeed.setText("0");
                            averageForce.setText("0");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnYearMonthPicker = findViewById(R.id.btn_year_month_picker);
        selectedDate = findViewById(R.id.selectedDate);

        Log.d("HISTORY_ACTIVITY", String.valueOf(Calendar.getInstance().get(Calendar.MONTH)));
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        String tempdate = months[Calendar.getInstance().get(Calendar.MONTH)] + ", " + selectedYear;

        selectedDate.setText(tempdate);

        btnYearMonthPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MonthPickerDialog pd = new MonthPickerDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });

        addItemsOnSpinner();

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fragmentSelect, new TrainingFragment());
        mFragmentTransaction.commit();

        averageForce = findViewById(R.id.averageForce);
        averageSpeed = findViewById(R.id.averageSpeed);
        totalPunches = findViewById(R.id.totalPunches);
        totalSessions = findViewById(R.id.totalSessions);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        uid = mFirebaseUser.getUid();


        mode_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    mDatabase.child("users").child(uid).child("TrainingHistorySummary").child(String.valueOf(selectedYear))
                            .child(String.valueOf(selectedMonth)).addValueEventListener(new ValueEventListener()  {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if(dataSnapshot.child("totalPunches").exists()) {
                                int tempTotalPunches = Integer.parseInt(dataSnapshot.child("totalPunches").getValue().toString());
                                totalPunches.setText(dataSnapshot.child("totalPunches").getValue().toString());
                                totalSessions.setText(dataSnapshot.child("totalSessions").getValue().toString());
                                if(tempTotalPunches == 0){
                                    averageSpeed.setText("0");
                                    averageForce.setText("0");

                                }
                                else{
                                    double tempAverageSpeed = Double.parseDouble(dataSnapshot.child("totalSpeed").getValue().toString()) / tempTotalPunches;
                                    averageSpeed.setText(String.format("%.2f", tempAverageSpeed));
                                    double tempAverageForce = Double.parseDouble(dataSnapshot.child("totalForce").getValue().toString()) / tempTotalPunches;
                                    averageForce.setText(String.format("%.2f", tempAverageForce));
                                }

                            }
                            else{
                                totalPunches.setText("0");
                                totalSessions.setText("0");
                                averageSpeed.setText("0");
                                averageForce.setText("0");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Fragment fr = new TrainingFragment() ;

                    Bundle args = new Bundle();
                    args.putInt("selectedMonth", selectedMonth);
                    args.putInt("selectedYear", selectedYear);

                    fr.setArguments(args);


                    FragmentManager mFragmentManager = getSupportFragmentManager();
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.fragmentSelect, fr);
                    mFragmentTransaction.commit();
                }
                else if(position == 1){
                    mDatabase.child("users").child(uid).child("TargetHistorySummary").child(String.valueOf(selectedYear))
                            .child(String.valueOf(selectedMonth)).addValueEventListener(new ValueEventListener()  {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("totalPunches").exists()) {
                                int tempTotalPunches = Integer.parseInt(dataSnapshot.child("totalPunches").getValue().toString());
                                totalPunches.setText(dataSnapshot.child("totalPunches").getValue().toString());
                                totalSessions.setText(dataSnapshot.child("totalSessions").getValue().toString());
                                if(tempTotalPunches == 0){
                                    averageSpeed.setText("0");
                                    averageForce.setText("0");

                                }
                                else{
                                    float tempAverageSpeed = (float)(Double.parseDouble(dataSnapshot.child("totalSpeed").getValue().toString()) / tempTotalPunches);
                                    averageSpeed.setText(String.format("%.2f", tempAverageSpeed));
                                    float tempAverageForce = (float)(Double.parseDouble(dataSnapshot.child("totalForce").getValue().toString()) / tempTotalPunches);
                                    averageForce.setText(String.format("%.2f", tempAverageForce));
                                }

                            }
                            else{
                                totalPunches.setText("0");
                                totalSessions.setText("0");
                                averageSpeed.setText("0");
                                averageForce.setText("0");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    Bundle args = new Bundle();
                    args.putInt("selectedMonth", selectedMonth);
                    args.putInt("selectedYear", selectedYear);


                    Fragment fr = new TargetFragment() ;

                    fr.setArguments(args);

                    FragmentManager mFragmentManager = getSupportFragmentManager();
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                    mFragmentTransaction.replace(R.id.fragmentSelect, fr);
                    mFragmentTransaction.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void addItemsOnSpinner() {

        mode_spinner = (Spinner) findViewById(R.id.mode_spinner);
        List<String> list = new ArrayList<String>();
        list.add("Training Mode");
        list.add("Target Mode");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode_spinner.setAdapter(dataAdapter);
    }

    public void updateSummaryField(int position){
        if(position == 0){
            mDatabase.child("users").child(uid).child("TrainingHistorySummary").child(String.valueOf(selectedYear))
                    .child(String.valueOf(selectedMonth)).addValueEventListener(new ValueEventListener()  {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if(dataSnapshot.child("totalPunches").exists()) {
                        int tempTotalPunches = Integer.parseInt(dataSnapshot.child("totalPunches").getValue().toString());
                        totalPunches.setText(dataSnapshot.child("totalPunches").getValue().toString());
                        totalSessions.setText(dataSnapshot.child("totalSessions").getValue().toString());
                        if(tempTotalPunches == 0){
                            averageSpeed.setText("0");
                            averageForce.setText("0");

                        }
                        else{
                            double tempAverageSpeed = Double.parseDouble(dataSnapshot.child("totalSpeed").getValue().toString()) / tempTotalPunches;
                            averageSpeed.setText(String.format("%.2f", tempAverageSpeed));
                            double tempAverageForce = Double.parseDouble(dataSnapshot.child("totalForce").getValue().toString()) / tempTotalPunches;
                            averageForce.setText(String.format("%.2f", tempAverageForce));
                        }

                    }
                    else{
                        totalPunches.setText("0");
                        totalSessions.setText("0");
                        averageSpeed.setText("0");
                        averageForce.setText("0");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Fragment fr = new TrainingFragment() ;

            Bundle args = new Bundle();
            args.putInt("selectedMonth", selectedMonth);
            args.putInt("selectedYear", selectedYear);

            fr.setArguments(args);


            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragmentSelect, fr);
            mFragmentTransaction.commit();
        }
        else if(position == 1){
            mDatabase.child("users").child(uid).child("TargetHistorySummary").child(String.valueOf(selectedYear))
                    .child(String.valueOf(selectedMonth)).addValueEventListener(new ValueEventListener()  {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("totalPunches").exists()) {
                        int tempTotalPunches = Integer.parseInt(dataSnapshot.child("totalPunches").getValue().toString());
                        totalPunches.setText(dataSnapshot.child("totalPunches").getValue().toString());
                        totalSessions.setText(dataSnapshot.child("totalSessions").getValue().toString());
                        if(tempTotalPunches == 0){
                            averageSpeed.setText("0");
                            averageForce.setText("0");

                        }
                        else{
                            float tempAverageSpeed = (float)(Double.parseDouble(dataSnapshot.child("totalSpeed").getValue().toString()) / tempTotalPunches);
                            averageSpeed.setText(String.format("%.2f", tempAverageSpeed));
                            float tempAverageForce = (float)(Double.parseDouble(dataSnapshot.child("totalForce").getValue().toString()) / tempTotalPunches);
                            averageForce.setText(String.format("%.2f", tempAverageForce));
                        }

                    }
                    else{
                        totalPunches.setText("0");
                        totalSessions.setText("0");
                        averageSpeed.setText("0");
                        averageForce.setText("0");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            Bundle args = new Bundle();
            args.putInt("selectedMonth", selectedMonth);
            args.putInt("selectedYear", selectedYear);


            Fragment fr = new TargetFragment() ;

            fr.setArguments(args);

            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.fragmentSelect, fr);
            mFragmentTransaction.commit();
        }
    }
}
