package com.example.sungbo.databaseexample.Model;

public class TrainingHistory {
    private  int duration;
    private int year, month, day;
    private int punchesThrown;
    private double averageSpeed;
    private double averageForce;

    public TrainingHistory(){}

    public TrainingHistory(int punchesThrown, double averageSpeed, double averageForce, int year, int month, int day, int duration){
        this.punchesThrown = punchesThrown;
        this.averageForce = averageForce;
        this.averageSpeed = averageSpeed;
        this.year = year;
        this.month = month;
        this.day = day;

        this.duration = duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }


    public void setPunchesThrown(int punchesThrown) {
        this.punchesThrown = punchesThrown;
    }

    public void setYear(int year) {
        this.year = year;
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


    public int getPunchesThrown() {
        return punchesThrown;
    }

    public int getYear() {
        return year;
    }

    public double getAverageForce() {
        return averageForce;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setAverageForce(double averageForce) {
        this.averageForce = averageForce;
    }
}
