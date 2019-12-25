package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

public class Entity {

    @Expose
    protected String sName;

    public void setName(String newname) {
        sName = newname;
    }

    public String getName() {
        return sName;
    }
}