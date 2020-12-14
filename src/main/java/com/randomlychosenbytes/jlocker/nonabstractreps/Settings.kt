package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Settings {

    @Expose
    public Integer lockerOverviewFontSize = 20;

    @Expose
    public Integer numOfBackups = 10;

    @Expose
    public List<Integer> lockerMinSizes = List.of(0, 0, 140, 150, 175);
}
