package com.trite.apps.flowbot.devices;

import org.json.JSONObject;

/**
 * Created by joe on 7/25/2019.
 */
public class BaseDevice {


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    private String mac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String toString() {


        return this.name + "{" +
                "mac='" + mac + '\'' +
                '}';
    }


    public String toJsonString() {
        return new JSONObject()
                .put("mac", mac)
                .put("device_type", this.name)
                .toString();
    }
}
