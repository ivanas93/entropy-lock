package com.github.ivanas93.os;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemCommunicator {
    public static String getOperatingSystem() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static boolean isWindows() {
        return getOperatingSystem().contains("win");
    }

    public static boolean isLinux() {
        return getOperatingSystem().contains("nux");
    }

    public static boolean isMac() {
        return getOperatingSystem().contains("mac");
    }
}
