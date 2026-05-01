package com.caiwu.utils;

import java.util.regex.Pattern;

public class ValidateUtils {

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int USERNAME_MIN_LENGTH = 2;
    private static final int USERNAME_MAX_LENGTH = 20;
    private static final int PASSWORD_MIN_LENGTH = 6;

    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String trimmed = username.trim();
        return trimmed.length() >= USERNAME_MIN_LENGTH && trimmed.length() <= USERNAME_MAX_LENGTH;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return true;
        }
        return password.length() >= PASSWORD_MIN_LENGTH;
    }

    public static String validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty() && !isValidPhone(phone)) {
            return "手机号格式不正确";
        }
        return null;
    }

    public static String validateEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            return "邮箱格式不正确";
        }
        return null;
    }

    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        String trimmed = username.trim();
        if (trimmed.length() < USERNAME_MIN_LENGTH || trimmed.length() > USERNAME_MAX_LENGTH) {
            return "用户名长度必须在" + USERNAME_MIN_LENGTH + "-" + USERNAME_MAX_LENGTH + "个字符之间";
        }
        return null;
    }

    public static String validatePassword(String password, boolean isRequired) {
        if (isRequired) {
            if (password == null || password.isEmpty()) {
                return "密码不能为空";
            }
        }
        if (password != null && !password.isEmpty() && password.length() < PASSWORD_MIN_LENGTH) {
            return "密码长度不能少于" + PASSWORD_MIN_LENGTH + "位";
        }
        return null;
    }
}
