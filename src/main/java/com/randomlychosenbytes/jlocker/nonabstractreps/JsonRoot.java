package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.List;

public class JsonRoot {

    public JsonRoot(byte[] encryptedBuildings, Settings settings, List<Task> tasks, List<User> users) {
        this.encryptedBuildings = encryptedBuildings;
        this.settings = settings;
        this.tasks = tasks;
        this.users = users;
    }

    @Expose
    public byte[] encryptedBuildings;

    @Expose
    public Settings settings;

    @Expose
    public List<Task> tasks;

    @Expose
    public List<User> users;
}
