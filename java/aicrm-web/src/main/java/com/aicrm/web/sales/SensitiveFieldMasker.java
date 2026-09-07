package com.aicrm.web.sales;

final class SensitiveFieldMasker {
    private SensitiveFieldMasker() {
    }

    static String mobile(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.length() <= 7) {
            return "****";
        }
        return value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    static String email(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int separator = value.indexOf('@');
        if (separator <= 0) {
            return "****";
        }
        return value.substring(0, 1) + "***" + value.substring(separator);
    }
}
