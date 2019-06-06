package com.example.sungbo.databaseexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sungbo.databaseexample.Model.TargetHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TargetFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private String uid;

    int selectedYear;
    int selectedMonth;

    RecyclerView recyclerview;

    View v;


    private List<TargetHistory> mTargetHistoryList = new ArrayList<>();
    private String TARGET_FRAGMENT = "TARGET_FRAGMENT";


    public TargetFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_target, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        uid = mFirebaseUser.getUid();

        Bundle args = getArguments();
        if (args != null) {
            selectedYear = args.getInt("selectedYear");
            selectedMonth = args.getInt("selectedMonth");
        }

        mDatabase.child("users").child(uid).child("targetHistory").child(String.valueOf(selectedYear))
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

                TargetHistory tempHistory = new TargetHistory();
                tempHistory.setPunchesTargeted(Integer.parseInt(messageData.child("punchesTargeted").getValue().toString()));
                tempHistory.setPunchesThrown(Integer.parseInt(messageData.child("punchesThrown").getValue().toString()));
                tempHistory.setDuration(Integer.parseInt(messageData.child("duration").getValue().toString()));
                tempHistory.setCompletion(Integer.parseInt(messageData.child("completion").getValue().toString()));
                tempHistory.setYear(Integer.parseInt(messageData.child("year").getValue().toString()));
                tempHistory.setMonth(Integer.parseInt(messageData.child("month").getValue().toString()));
                tempHistory.setDay(Integer.parseInt(messageData.child("day").getValue().toString()));

                mTargetHistoryList.add(tempHistory);
            }

            recyclerview = v.findViewById(R.id.recyclerview);

            ListAdapter listadapter = new ListAdapter(getContext(), mTargetHistoryList);
            recyclerview.setAdapter(listadapter);


            RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getActivity());
            recyclerview.setLayoutManager(layoutmanager);

        }

    }
}
