package com.example.sungbo.databaseexample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sungbo.databaseexample.Model.TrainingHistory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrainingFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private String uid;

    private LineChart mChart;

    int selectedYear;
    int selectedMonth;
    View v;
    int counter = 1;

    private ArrayList<Entry> speedEntry = new ArrayList<>();
    private ArrayList<Entry> forceEntry = new ArrayList<>();

    public TrainingFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_training, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        uid = mFirebaseUser.getUid();

        Bundle args = getArguments();
        if (args != null) {
            selectedYear = args.getInt("selectedYear");
            selectedMonth = args.getInt("selectedMonth");
        }

        mChart = v.findViewById(R.id.chart);

        mDatabase.child("users").child(uid).child("trainingHistory").child(String.valueOf(selectedYear))
                .child(String.valueOf(selectedMonth)).addValueEventListener(new ValueEventListener()  {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                readData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        return v;
    }

    public void readData(DataSnapshot dataSnapshot) {
        if(dataSnapshot != null){

            for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                TrainingHistory tempHistory = new TrainingHistory();
                tempHistory.setPunchesThrown(Integer.parseInt(messageData.child("punchesThrown").getValue().toString()));
                tempHistory.setDuration(Integer.parseInt(messageData.child("duration").getValue().toString()));
                tempHistory.setAverageForce(Float.parseFloat(messageData.child("averageForce").getValue().toString()));
                tempHistory.setAverageSpeed(Float.parseFloat(messageData.child("averageSpeed").getValue().toString()));
                tempHistory.setYear(Integer.parseInt(messageData.child("year").getValue().toString()));
                tempHistory.setMonth(Integer.parseInt(messageData.child("month").getValue().toString()));
                tempHistory.setDay(Integer.parseInt(messageData.child("day").getValue().toString()));

                speedEntry.add(new Entry(counter, (float) tempHistory.getAverageSpeed()));
                forceEntry.add(new Entry(counter, (float) tempHistory.getAverageForce()));
                counter++;
            }


            Legend legend = mChart.getLegend();
            legend.setEnabled(true);


            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(1f);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


            LineDataSet speedDataSet = new LineDataSet(speedEntry, "Average Speed");
            speedDataSet.setLineWidth(4);
            speedDataSet.setColor(Color.BLUE);
            speedDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            speedDataSet.setValueTextSize(12);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setStartAtZero(true);
            leftAxis.setDrawGridLines(false);


            LineDataSet forceDataSet = new LineDataSet(forceEntry, "Average Force");
            forceDataSet.setLineWidth(4);
            forceDataSet.setColor(Color.RED);
            forceDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            forceDataSet.setValueTextSize(12);

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setStartAtZero(true);
            rightAxis.setDrawGridLines(false);


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(speedDataSet);
            dataSets.add(forceDataSet);



            LineData data = new LineData(dataSets);


            mChart.setTouchEnabled(true);
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(false);


            mChart.setData(data);

            mChart.invalidate();





        }

    }

}
