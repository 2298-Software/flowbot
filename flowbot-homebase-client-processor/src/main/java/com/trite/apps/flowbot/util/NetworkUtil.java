package com.trite.apps.flowbot.util;

import java.net.SocketException;

/**
 * Created by joe on 8/9/2019.
 */
public class NetworkUtil {
    public static String getMacAddress(String _interface) throws SocketException {
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
