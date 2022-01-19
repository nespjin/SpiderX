package com.nesp.fishplugin.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LinkedHashMapExtensions {

    private LinkedHashMapExtensions() {
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map.Entry<K, V> eldest(final LinkedHashMap<K, V> $this) {
        if ($this == null) return null;
        try {
            return (Map.Entry<K, V>) LinkedHashMap.class.getDeclaredField("head").get($this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

}