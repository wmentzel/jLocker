package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.TreeMap;

public class JsonRoot {

    public JsonRoot(byte[] encryptedBuildings, TreeMap<String, Object> settings, List<Task> tasks, List<User> users) {
        this.encryptedBuildings = encryptedBuildings;
        this.settings = settings;
        this.tasks = tasks;
        this.users = users;
    }

    @Expose
    public byte[] encryptedBuildings;

    @Expose
    public TreeMap<String, Object> settings;

    @Expose
    public List<Task> tasks;

    @Expose
    public List<User> users;
}
