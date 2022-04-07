package io.sephy.playwright;

import java.util.Random;

/**
 * @author sephy
 * @date 2022-02-05 23:02
 */
public class IPUtils {

    private IPUtils() {}

    public static final String generateRandomIP() {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}
