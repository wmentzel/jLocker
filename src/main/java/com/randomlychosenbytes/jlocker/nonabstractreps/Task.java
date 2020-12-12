package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task {

    @Expose
    private String description;

    @Expose
    private boolean isDone = false;

    @Expose
    private String creationDate;

    public Task(String description) {
        this.description = description;

        Calendar today = new GregorianCalendar();

        creationDate = String.format("%02d.%02d.%02d", today.get(Calendar.DATE),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.YEAR));
    }

    public String getSDate() {
        return creationDate;
    }

    public String getSDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setSDate(String sDate) {
        this.creationDate = sDate;
    }

    public void setSDescription(String sDescription) {
        this.description = sDescription;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }
}

