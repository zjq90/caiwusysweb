package com.caiwusys.util;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static boolean verify(String rawPassword, String encodedPassword) {
        return md5(rawPassword).equals(encodedPassword);
    }
}
