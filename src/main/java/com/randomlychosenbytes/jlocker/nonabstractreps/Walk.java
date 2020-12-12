package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;
import com.randomlychosenbytes.jlocker.abstractreps.ManagementUnit;
import com.randomlychosenbytes.jlocker.manager.DataManager;

import java.util.LinkedList;
import java.util.List;

public class Walk extends Entity {

    @Expose
    private List<ManagementUnit> managementUnits;

    public Walk(String name) {
        this.name = name;
        managementUnits = new LinkedList<>();
    }

    public void setManagementUnits(List<ManagementUnit> managementUnits) {
        this.managementUnits = managementUnits;
    }

    public void setCurLockerIndex(Locker locker) {
        for (int m = 0; m < managementUnits.size(); m++) {
            List<Locker> lockers = managementUnits.get(m).getLockerList();

            int l = lockers.indexOf(locker);

            if (l != -1) {
                DataManager dm = DataManager.getInstance();
                dm.setCurrentLockerIndex(l);
                dm.setCurrentMUnitIndex(m);
            }
        }
    }

    public List<ManagementUnit> getManagementUnitList() {
        return managementUnits;
    }

    public List<ManagementUnit> getManagementUnits() {
        return managementUnits;
    }
}
