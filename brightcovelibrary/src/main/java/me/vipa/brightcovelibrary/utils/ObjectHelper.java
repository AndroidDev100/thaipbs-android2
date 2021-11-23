package me.vipa.brightcovelibrary.utils;

import androidx.annotation.Nullable;

import java.util.List;

public final class ObjectHelper {
    private ObjectHelper() {
    }

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        } else if (obj instanceof List) {
            return ((List<?>) obj).isEmpty();
        }
        return false;
    }

    public static boolean isNotEmpty(@Nullable Object obj) {
        return !isEmpty(obj);
    }

    public static <T> int  getCount(List<T> list) {
        if (isEmpty(list)) {
            return 0;
        } else {
            return list.size();
        }
    }

    public static boolean isExactlySame(@Nullable String str1, @Nullable String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if (str1 != null) {
            return str1.equals(str2);
        } else {
            return false;
        }
    }

    public static boolean isSame(@Nullable String str1, @Nullable String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if (str1 != null) {
            return str1.equalsIgnoreCase(str2);
        } else {
            return false;
        }
    }

    public static boolean isNotSame(@Nullable String str1, @Nullable String str2) {
        return !isSame(str1, str2);
    }
}
