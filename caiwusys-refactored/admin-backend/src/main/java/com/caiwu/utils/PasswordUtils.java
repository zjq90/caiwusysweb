package com.caiwu.utils;

import org.springframework.util.DigestUtils;

public class PasswordUtils {

    private static final String DEFAULT_PASSWORD = "123456";

    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return encode(DEFAULT_PASSWORD);
        }
        return DigestUtils.md5DigestAsHex(rawPassword.getBytes());
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        String encodedRaw = encode(rawPassword);
        return encodedRaw.equals(encodedPassword);
    }

    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }
}
