package com.quality.mytester.Modelos;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Molino implements Serializable {

    int singleDoses;
    int doubleDoses;
    String name;
    String MAC;
    double singleDosesTime;
    double doubleDosesTime;
    GrinderMode mode;
    int missingForCleanning;
    int missingForDrillChange;
    double singleDosesGrams;
    double doubleDosesGrams;
    BluetoothDevice device;
    boolean correct;

    public Molino() {
        this.correct = false;
    }

    public Molino(BluetoothDevice device) {
        this.device = device;
        this.name = device.getName();
        this.MAC = device.getAddress();
        this.singleDoses = -1;
        this.doubleDoses = -1;
        this.correct = false;

    }

    public Molino(int singleDoses, int doubleDoses, double singleDosesTime, double doubleDosesTime, GrinderMode mode, int missingForCleanning, int missingForDrillChange, double singleDosesGrams, double doubleDosesGrams, BluetoothDevice device) {
        this.singleDoses = singleDoses;
        this.doubleDoses = doubleDoses;
        this.singleDosesTime = singleDosesTime;
        this.doubleDosesTime = doubleDosesTime;
        this.mode = mode;
        this.missingForCleanning = missingForCleanning;
        this.missingForDrillChange = missingForDrillChange;
        this.singleDosesGrams = singleDosesGrams;
        this.doubleDosesGrams = doubleDosesGrams;
        this.device = device;
        this.name = device.getName();
        this.MAC = device.getAddress();
        this.correct = false;
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

    public double getSingleDosesTime() {
        return singleDosesTime;
    }

    public void setSingleDosesTime(double singleDosesTime) {
        this.singleDosesTime = singleDosesTime;
    }

    public double getDoubleDosesTime() {
        return doubleDosesTime;
    }

    public void setDoubleDosesTime(double doubleDosesTime) {
        this.doubleDosesTime = doubleDosesTime;
    }

    public GrinderMode getMode() {
        return mode;
    }

    public void setMode(GrinderMode mode) {
        this.mode = mode;
    }

    public int getMissingForCleanning() {
        return missingForCleanning;
    }

    public void setMissingForCleanning(int missingForCleanning) {
        this.missingForCleanning = missingForCleanning;
    }

    public int getMissingForDrillChange() {
        return missingForDrillChange;
    }

    public void setMissingForDrillChange(int missingForDrillChange) {
        this.missingForDrillChange = missingForDrillChange;
    }

    public double getSingleDosesGrams() {
        return singleDosesGrams;
    }

    public void setSingleDosesGrams(double singleDosesGrams) {
        this.singleDosesGrams = singleDosesGrams;
    }

    public double getDoubleDosesGrams() {
        return doubleDosesGrams;
    }

    public void setDoubleDosesGrams(double doubleDosesGrams) {
        this.doubleDosesGrams = doubleDosesGrams;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
        this.name = device.getName();
        this.MAC = device.getAddress();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Molino)) return false;
        Molino molino = (Molino) o;
        return getMAC().equals(molino.getMAC());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMAC());
    }

    @Override
    public String toString() {
        return "Molino{" +
                "Name='" + name + '\'' +
                "\nMAC='" + MAC + '\'' +
                "\nSingleDoses=" + singleDoses +
                "\nDoubleDoses=" + doubleDoses +
                "\nMode=" + mode +
                "\nMissingForCleanning=" + missingForCleanning +
                "\nMissingForDrillChange=" + missingForDrillChange +
                '}';
    }
}
