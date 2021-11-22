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
}
