package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.manager.DataManager;

import java.util.LinkedList;
import java.util.List;

public class Walk extends Entity {

    @Expose
    private List<ManagementUnit> mus;

    public Walk(String name) {
        sName = name;
        mus = new LinkedList<>();
    }

    public void setMus(List<ManagementUnit> mus) {
        this.mus = mus;
    }

    public void setCurLockerIndex(Locker locker) {
        for (int m = 0; m < mus.size(); m++) {
            List<Locker> lockers = mus.get(m).getLockerList();

            int l;

            if ((l = lockers.indexOf(locker)) != -1) {
                DataManager dm = DataManager.getInstance();
                dm.setCurrentLockerIndex(l);
                dm.setCurrentMUnitIndex(m);
            }
        }
    }

    public List<ManagementUnit> getManagementUnitList() {
        return mus;
    }

    public List<ManagementUnit> getMus() {
        return mus;
    }
}
