package com.example.sungbo.databaseexample.Model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Session {
    List<Punch> punches = new ArrayList<>();
    int duration;

    public int getDuration() {
        return duration;
    }

    public List<Punch> getPunches() {
        return punches;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPunches(List<Punch> punches) {
        this.punches = punches;
    }
}
