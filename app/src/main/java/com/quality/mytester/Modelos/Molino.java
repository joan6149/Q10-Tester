package com.quality.mytester.Modelos;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Molino implements Serializable {

    String name;
    String MAC;

    public Molino(String name, String MAC) {
        this.name = name;
        this.MAC = MAC;
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

    @NonNull
    @Override
    public String toString() {
        return "Name: " + this.getName() + " MAC: " + this.getMAC();
    }
}
