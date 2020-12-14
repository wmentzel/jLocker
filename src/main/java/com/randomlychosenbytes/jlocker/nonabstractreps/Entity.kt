package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

public class Entity {

    @Expose
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}