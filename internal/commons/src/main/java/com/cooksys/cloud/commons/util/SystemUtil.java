package com.cooksys.cloud.commons.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * System utility class
 *
 * @author Tim Davidson
 */
public class SystemUtil {

    public static String getIpAddressOfCurrentHost() {

        InetAddress ip = null;

        try {
            ip = InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            e.printStackTrace();
            return null;
        }

        return ip.getHostAddress();
    }
}
