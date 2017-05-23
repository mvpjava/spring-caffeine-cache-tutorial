package com.mvpjava;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document 
public class Aircraft {

    @Id private String id; //mongoID
    @Indexed(unique = true) private final String model;
    private int topSpeed;

    public Aircraft(String model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public int getTopSpeed() {
        return topSpeed;
    }

    public Aircraft setTopSpeed(int topSpeed) {
        this.topSpeed = topSpeed;
        return this;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.model);
        hash = 89 * hash + this.topSpeed;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Aircraft other = (Aircraft) obj;
        if (this.topSpeed != other.topSpeed) {
            return false;
        }
        if (!Objects.equals(this.model, other.model)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Aircraft{" + "model=" + model + ", topSpeed=" + topSpeed + '}';
    }


}
