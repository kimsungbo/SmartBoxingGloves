package com.example.sungbo.databaseexample.Model;

public class HistorySummary {
    private double totalForce;
    private double totalSpeed;
    private int totalPunches;
    private int totalSessions;

    public HistorySummary(){}

    public HistorySummary(int totalPunches, int totalSessions, double averageSpeed, double averageForce){
        this.totalPunches = totalPunches;
        this.totalSessions = totalSessions;
        this.totalForce = averageForce;
        this.totalSpeed = averageSpeed;
    }

    public double getTotalForce() {
        return totalForce;
    }

    public double getTotalSpeed() {
        return totalSpeed;
    }

    public int getTotalPunches() {
        return totalPunches;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalForce(double totalForce) {
        this.totalForce = totalForce;
    }

    public void setTotalSpeed(double totalSpeed) {
        this.totalSpeed = totalSpeed;
    }

    public void setTotalPunches(int totalPunches) {
        this.totalPunches = totalPunches;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }
}
