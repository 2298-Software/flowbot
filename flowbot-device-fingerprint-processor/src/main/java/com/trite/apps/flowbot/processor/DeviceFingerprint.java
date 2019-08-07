package com.trite.apps.flowbot.processor;

import java.net.SocketException;
import java.util.Date;

/**
 * Created by joe on 8/7/2019.
 */
public class DeviceFingerprint {
    String mac;
    String os_version;
    Long epoch;

    public DeviceFingerprint(String interface_name, String os_version, Date d) throws SocketException {
        this.mac = getMacAddress(interface_name);
        this.os_version = os_version;
        this.epoch = d.getTime();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    private static String getMacAddress(String _interface) throws SocketException {
        byte[] mac = java.net.NetworkInterface.getByName(_interface).getHardwareAddress();
        if (mac == null)
            return null;

        StringBuilder sb = new StringBuilder(18);
        for (byte b : mac) {
            if (sb.length() > 0)
                sb.append(':');
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
