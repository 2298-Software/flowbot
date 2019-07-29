package com.trite.apps.flowbot.devices;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties(value = { "state" })
public class Step {
    private String name;
    private String description;
    private HashMap<String, String> processor_attributes;
    private String on_success;
    private String on_failure;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status = 0;


    public String getOn_success() {
        return on_success;
    }

    public void setOn_success(String on_success) {
        this.on_success = on_success;
    }

    public String getOn_failure() {
        return on_failure;
    }

    public void setOn_failure(String on_failure) {
        this.on_failure = on_failure;
    }

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

    public HashMap<String, String> getProcessorAttributes() {
        return processor_attributes;
    }

    public void setProcessorAttributes(HashMap<String, String> processor) {
        this.processor_attributes = processor;
    }
}