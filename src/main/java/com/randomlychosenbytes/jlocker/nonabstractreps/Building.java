package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.LinkedList;
import java.util.List;

public class Building extends Entity {

    @Expose
    private String notes;

    @Expose
    private List<Floor> floors;

    public Building(String name, String notes) {
        sName = name;
        this.notes = notes;
        floors = new LinkedList<>();
    }

    public Building(String name) {
        sName = name;
        this.notes = "";
        floors = new LinkedList<>();
    }

    public Building() {
    }

    /* *************************************************************************
     * Setter
     **************************************************************************/
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    /* *************************************************************************
     * Getter
     **************************************************************************/

    public String getNotes() {
        return notes;
    }

    public List<Floor> getFloorList() {
        return floors;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
