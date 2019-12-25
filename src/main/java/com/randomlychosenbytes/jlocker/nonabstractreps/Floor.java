package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.LinkedList;
import java.util.List;

public class Floor extends Entity {

    @Expose
    private List<Walk> walks;

    public Floor(String name) {
        sName = name;
        walks = new LinkedList<>();
    }

    public void setWalks(List<Walk> walks) {
        this.walks = walks;
    }

    public List<Walk> getWalkList() {
        return walks;
    }

    public List<Walk> getWalks() {
        return walks;
    }
}
