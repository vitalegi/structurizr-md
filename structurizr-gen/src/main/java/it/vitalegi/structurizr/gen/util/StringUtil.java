package it.vitalegi.structurizr.gen.util;

public class StringUtil {

    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        }
        if (str.equals("")) {
            return true;
        }
        if (str.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public static String getFirstNotNullOrEmpty(String... strs) {
        if (strs == null) {
            throw new IllegalArgumentException();
        }
        for (var i = 0; i < strs.length; i++) {
            if (isNotNullOrEmpty(strs[i])) {
                return strs[i];
            }
        }
        return null;
    }
}
