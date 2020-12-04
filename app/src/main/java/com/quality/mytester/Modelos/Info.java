package com.quality.mytester.Modelos;

import androidx.annotation.NonNull;

public class Info {
    int singleDoses;
    int doubleDoses;
    int cleaningGrinder;
    int revisionGrinder;
    double timeToSingle;
    double timeTo;
    GrinderMode mode;

    public Info() {

    }

    public Info(int singleDoses, int doubleDoses, int cleaningGrinder, int revisionGrinder, double timeToSingle, double timeTo, GrinderMode mode) {
        this.singleDoses = singleDoses;
        this.doubleDoses = doubleDoses;
        this.cleaningGrinder = cleaningGrinder;
        this.revisionGrinder = revisionGrinder;
        this.timeToSingle = timeToSingle;
        this.timeTo = timeTo;
        this.mode = mode;
    }

    public int getSingleDoses() {
        return singleDoses;
    }

    public void setSingleDoses(int singleDoses) {
        this.singleDoses = singleDoses;
    }

    public int getDoubleDoses() {
        return doubleDoses;
    }

    public void setDoubleDoses(int doubleDoses) {
        this.doubleDoses = doubleDoses;
    }

    public int getCleaningGrinder() {
        return cleaningGrinder;
    }

    public void setCleaningGrinder(int cleaningGrinder) {
        this.cleaningGrinder = cleaningGrinder;
    }

    public int getRevisionGrinder() {
        return revisionGrinder;
    }

    public void setRevisionGrinder(int revisionGrinder) {
        this.revisionGrinder = revisionGrinder;
    }

    public double getTimeToSingle() {
        return timeToSingle;
    }

    public void setTimeToSingle(double timeToSingle) {
        this.timeToSingle = timeToSingle;
    }

    public double getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(double timeTo) {
        this.timeTo = timeTo;
    }

    public GrinderMode getMode() {
        return mode;
    }

    public void setMode(GrinderMode mode) {
        this.mode = mode;
    }

    @NonNull
    @Override
    public String toString() {
        return "Info: " + this.getMode().toString() + "--->" + this.getSingleDoses() + " - " + getDoubleDoses();
    }
}
