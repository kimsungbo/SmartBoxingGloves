package com.example.sungbo.databaseexample.Model;


public class TargetHistory {
    private  int duration;
    private int completion;
    private int year, month, day;
    private int punchesTargeted, punchesThrown;

    public TargetHistory(){}

    public TargetHistory(int punchesTargeted, int punchesThrown, int year, int month, int day, int duration){
        this.punchesTargeted = punchesTargeted;
        this.punchesThrown = punchesThrown;
        this.year = year;
        this.month = month;
        this.day = day;

        this.duration = duration;
    }

    public TargetHistory(int punchesTargeted, int punchesThrown, int year, int month, int day, int duration, int completion){
        this.punchesTargeted = punchesTargeted;
        this.punchesThrown = punchesThrown;
        this.year = year;
        this.month = month;
        this.day = day;

        this.duration = duration;
        this.completion = completion;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setPunchesTargeted(int punchesTargeted) {
        this.punchesTargeted = punchesTargeted;
    }

    public void setPunchesThrown(int punchesThrown) {
        this.punchesThrown = punchesThrown;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCompletion() {
        return completion;
    }

    public int getDay() {
        return day;
    }

    public int getDuration() {
        return duration;
    }

    public int getMonth() {
        return month;
    }

    public int getPunchesTargeted() {
        return punchesTargeted;
    }

    public int getPunchesThrown() {
        return punchesThrown;
    }

    public int getYear() {
        return year;
    }
}

