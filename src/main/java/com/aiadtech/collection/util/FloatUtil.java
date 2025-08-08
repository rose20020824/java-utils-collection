package com.aiadtech.collection.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * 浮点数工具类
 */
public class FloatUtil {

    private FloatUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param floats 浮点型数组
     * @param dimension 维度
     */
    public static List<Float> asFloatList(float[] floats, int dimension) {
        List<Float> list = new ArrayList<>(dimension);
        for (var i = 0; i < dimension; i++) {
            if (i < floats.length) {
                list.add(floats[i]);
            } else {
                list.add((float) 0);
            }
        }
        return list;
    }

    public static float[] decodeFromJson(String floatJson) {
        float[] floats = new float[0];
        try {
            floats = JsonUtil.deserializer(floatJson, new TypeReference<>() {
            });
        } catch (Exception ignored) {}

        if (floats == null) {
            floats = new float[0];
        }
        return floats;
    }

}
