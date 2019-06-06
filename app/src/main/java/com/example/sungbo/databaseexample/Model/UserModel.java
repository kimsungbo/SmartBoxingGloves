package com.example.sungbo.databaseexample.Model;

import android.icu.text.DecimalFormat;

import java.math.RoundingMode;

public class UserModel {
    private String email;
    private String password;
    private String uid;
    private double height;
    private double weight;
    private double armlength;
    private int stance;

    public UserModel(){}
    public UserModel(String email, String password, String uid){
        this.email = email;
        this.password = password;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUid() {
        return uid;
    }

    public double getArmlength() {
        return armlength;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public int getStance() {
        return stance;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setArmlength(float armlength) {
        this.armlength = (double)Math.round(armlength * 100d) / 100d;
    }

    public void setHeight(float height) {
        this.height = (double)Math.round(height * 100d) / 100d;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public void setWeight(float weight) {
        this.weight = (double)Math.round(weight * 100d) / 100d;
    }

}
