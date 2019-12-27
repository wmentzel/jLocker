package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task {

    @Expose
    private String sDescription;

    @Expose
    private boolean isDone = false;

    @Expose
    private String sDate;

    public Task(String description) {
        sDescription = description;

        Calendar today = new GregorianCalendar();

        sDate = String.format("%02d.%02d.%02d", today.get(Calendar.DATE),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.YEAR));
    }

    public String getSDate() {
        return sDate;
    }

    public String getSDescription() {
        return sDescription;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setSDate(String sDate) {
        this.sDate = sDate;
    }

    public void setSDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }
}

