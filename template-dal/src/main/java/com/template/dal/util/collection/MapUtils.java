package com.template.dal.util.collection;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static <K, V> Map<K, V> getColsMap(Map<K, V> sourceMap, K... keys) {
        if (keys == null || keys.length == 0 || sourceMap == null) {
            return sourceMap;
        }
        Map<K, V> resultMap = new HashMap<K, V>();
        for (K key : keys) {
            resultMap.put(key, sourceMap.get(key));
        }
        return resultMap;
    }



    public static <K, V> void removeKeys(Map<K, V> source, K... keys) {
        if (source == null || keys == null || keys.length == 0) {
            return;
        }
        for (K key : keys) {
            source.remove(key);
        }
    }


    public static <K> Map toMap(K key, Object obj) {
        Map<K, Object> resultMap = new HashMap<>();
        resultMap.put(key, obj);
        return resultMap;
    }

    public static <K, V, T extends V> T getValue(Map<K, V> map, K key) {
        V value = getValue(map, key, null);
        return value == null ? null : (T) value;
    }

    public static <K, V> V getValue(Map<K, V> map, K key, V defaultValue) {
        if (valueNULL(map, key)) {
            return defaultValue;
        } else {
            return map.get(key);
        }
    }


    public static <K, V> boolean valueNULL(Map<K, V> map, K key) {
        return (map == null || key == null || !map.containsKey(key) || map.get(key) == null);
    }

    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

}

