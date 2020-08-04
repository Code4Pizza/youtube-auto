package org.youtube.util;

import java.util.Random;

public class ProxyUtil {

    public static String findProxyForAccount(boolean needEthernet) {
        if (needEthernet)
            return "proxy_full_" + new Random().nextInt(3);
        else
            return "proxy_" + new Random().nextInt(6);
    }

}
