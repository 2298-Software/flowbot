package com.trite.apps.flowbot.devices.processor;

import com.trite.apps.flowbot.devices.Runnable;
import com.trite.apps.flowbot.devices.result.Result;

public class Processor implements Runnable {

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public Result run() {
        return null;
    }
}