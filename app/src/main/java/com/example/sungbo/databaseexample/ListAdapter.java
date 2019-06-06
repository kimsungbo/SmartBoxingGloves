package com.example.sungbo.databaseexample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sungbo.databaseexample.Model.TargetHistory;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private final List<TargetHistory> mTargetHistoryList;
    private String[] months = {"Jan", "Feb", "Mar", "April", "May", "Jun", "July", "August", "Sept", "Nov", "Oct", "Dec"};



    public ListAdapter(Context context, List<TargetHistory> mTargetHistoryList){
        mInflater = LayoutInflater.from(context);
        this.mTargetHistoryList = mTargetHistoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.target_history_list, viewGroup, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d("LIST ADAPTER", String.valueOf(mTargetHistoryList.size()));
        ((ListViewHolder) viewHolder).bindView(i);

    }

    @Override
    public int getItemCount() {
        //return OurData.title.length;
        return mTargetHistoryList.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView yearmonth;
        private TextView day;
        private TextView completion;
        private TextView duration;
        private TextView punches;


        public ListViewHolder(View itemView){
            super(itemView);

            yearmonth = itemView.findViewById(R.id.yearmonth);
            day = itemView.findViewById(R.id.day);
            duration = itemView.findViewById(R.id.duration);
            punches = itemView.findViewById(R.id.punches);
            completion = itemView.findViewById(R.id.completion);

            itemView.setOnClickListener(this);

        }
        public void bindView(int position){
            Log.d("POSITION", String.valueOf(position));
            String temp = months[mTargetHistoryList.get(position).getMonth() - 1] + ", " + mTargetHistoryList.get(position).getYear();

            Log.d("LIST ADAPTER DATE", temp );
            yearmonth.setText(temp);

            punches.setText( String.valueOf(mTargetHistoryList.get(position).getPunchesThrown()));
            Log.d("LIST ADAPTER PUNCHES THROWN", String.valueOf(mTargetHistoryList.get(position).getPunchesThrown()));

            day.setText( String.valueOf(mTargetHistoryList.get(position).getDay()));
            Log.d("LIST ADAPTER DATE", String.valueOf(mTargetHistoryList.get(position).getDay()));

            completion.setText( String.valueOf(mTargetHistoryList.get(position).getCompletion() + " %"));
            Log.d("LIST ADAPTER COMPLETION", String.valueOf(mTargetHistoryList.get(position).getCompletion()));

            int tempMinutes = mTargetHistoryList.get(position).getDuration();
            duration.setText(toClockFormat(tempMinutes));
            Log.d("LIST ADAPTER DURATION", toClockFormat(tempMinutes));

        }

        @Override
        public void onClick(View v) {

        }
    }

    public String toClockFormat(int seconds){
        String clockformat;
        clockformat = (seconds / 60) + " : " + (seconds % 60);
        return clockformat;
    }
}
