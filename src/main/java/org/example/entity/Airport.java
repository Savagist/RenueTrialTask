package org.example.entity;

import java.util.ArrayList;
import java.util.List;


public class Airport {


    private final List<Object[]> information;

    public Airport() {
        this.information = new ArrayList<>();
    }

    public List<Object[]> getInformation() {
        return information;
    }

    public void addInformation(Object[] information) {
        this.information.add(information);
    }
}