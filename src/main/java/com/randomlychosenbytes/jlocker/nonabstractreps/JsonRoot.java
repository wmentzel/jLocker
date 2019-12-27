package com.randomlychosenbytes.jlocker.nonabstractreps;

import com.google.gson.annotations.Expose;

import java.util.List;

public class JsonRoot {

    public JsonRoot(String encryptedBuildingsBase64, Settings settings, List<Task> tasks, List<User> users) {
        this.encryptedBuildingsBase64 = encryptedBuildingsBase64;
        this.settings = settings;
        this.tasks = tasks;
        this.users = users;
    }

    @Expose
    public String encryptedBuildingsBase64;

    @Expose
    public Settings settings;

    @Expose
    public List<Task> tasks;

    @Expose
    public List<User> users;
}
