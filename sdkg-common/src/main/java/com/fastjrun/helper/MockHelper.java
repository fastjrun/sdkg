package com.fastjrun.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class MockHelper {

    public final static String NUMERICANDALPHABETIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String geStringWithUpperAlphabetic(int maxLength) {
        int length = RandomUtils.nextInt(maxLength);
        return RandomStringUtils.randomAlphabetic(length).toUpperCase();
    }

    public static String geStringWithLowerAlphabetic(int maxLength) {
        int length = RandomUtils.nextInt(maxLength);
        return RandomStringUtils.randomAlphabetic(length).toLowerCase();
    }

    public static String geStringWithAlphabetic(int maxLength) {
        int length = RandomUtils.nextInt(maxLength);
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static String geStringWithAscii(int maxLength) {
        int length = RandomUtils.nextInt(maxLength);
        return RandomStringUtils.random(length, NUMERICANDALPHABETIC);
    }

    public static String geStringWithNumeric(int maxLength) {
        int length = RandomUtils.nextInt(maxLength);
        return RandomStringUtils.randomNumeric(length);
    }

    public static Boolean geBoolean() {

        return Boolean.valueOf(RandomUtils.nextBoolean());
    }

    public static Integer geInteger(int maxValue) {

        return Integer.valueOf(RandomUtils.nextInt(maxValue));
    }

    public static Long geLong(int maxValue) {

        return Long.valueOf(RandomUtils.nextInt(maxValue));
    }

    public static Double geDouble(int maxValue) {

        return Double.valueOf(RandomUtils.nextDouble() * maxValue);
    }

    public static Float geFloat(int maxValue) {

        return Float.valueOf(RandomUtils.nextFloat() * maxValue);
    }

    public static List<String> geStringListWithAscii(int maxSize) {
        char[] chars = geStringWithAscii(maxSize).toCharArray();
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < chars.length; i++) {
            strings.add(String.valueOf(chars[i]));
        }
        return strings;
    }

    public static String[] geStringArrayWithAscii(int maxSize) {
        char[] chars = geStringWithAscii(maxSize).toCharArray();
        String[] strings = new String[chars.length];
        for (int i = 0; i < chars.length; i++) {
            strings[i] = String.valueOf(chars[i]);
        }
        return strings;
    }

    public static List<Boolean> geBooleanList(int maxSize) {
        int size = RandomUtils.nextInt(maxSize);
        List<Boolean> booleans = new ArrayList<Boolean>();
        for (int i = 0; i < size; i++) {
            booleans.add(Boolean.valueOf(RandomUtils.nextBoolean()));
        }
        return booleans;
    }

    public static Boolean[] geBooleanArray(int maxSize) {
        int size = RandomUtils.nextInt(maxSize);
        Boolean[] booleans = new Boolean[size];
        for (int i = 0; i < size; i++) {
            booleans[i] = Boolean.valueOf(RandomUtils.nextBoolean());
        }
        return booleans;
    }

    public static List<Integer> geIntegerList(int maxSize) {
        return geIntegerList(maxSize, 10);
    }

    public static Integer[] geIntegerArray(int maxSize) {
        return geIntegerArray(maxSize, 10);
    }

    public static List<Integer> geIntegerList(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        List<Integer> integers = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            integers.add(Integer.valueOf(RandomUtils.nextInt(maxValue)));
        }
        return integers;
    }

    public static Integer[] geIntegerArray(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        Integer[] integers = new Integer[size];
        for (int i = 0; i < size; i++) {
            integers[i] = Integer.valueOf(RandomUtils.nextInt(maxValue));
        }
        return integers;
    }

    public static List<Long> geLongList(int maxSize) {
        return geLongList(maxSize, 10000);
    }

    public static Long[] geLongArray(int maxSize) {
        return geLongArray(maxSize, 10000);
    }

    public static List<Long> geLongList(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        List<Long> longs = new ArrayList<Long>();
        for (int i = 0; i < size; i++) {
            longs.add(Long.valueOf(RandomUtils.nextInt(maxValue)));
        }
        return longs;
    }

    public static Long[] geLongArray(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        Long[] longs = new Long[size];
        for (int i = 0; i < size; i++) {
            longs[i] = Long.valueOf(RandomUtils.nextInt(maxValue));
        }
        return longs;
    }

    public static List<Float> geFloatList(int maxSize) {
        return geFloatList(maxSize, 1);
    }

    public static Float[] geFloatArray(int maxSize) {
        return geFloatArray(maxSize, 1);
    }

    public static List<Float> geFloatList(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        List<Float> floats = new ArrayList<Float>();
        for (int i = 0; i < size; i++) {
            floats.add(Float.valueOf(RandomUtils.nextFloat() * maxValue));
        }
        return floats;
    }

    public static Float[] geFloatArray(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        Float[] floats = new Float[size];
        for (int i = 0; i < size; i++) {
            floats[i] = Float.valueOf(RandomUtils.nextFloat() * maxValue);
        }
        return floats;
    }

    public static List<Double> geDoubleList(int maxSize) {
        return geDoubleList(maxSize, 1);
    }

    public static Double[] geDoubleArray(int maxSize) {
        return geDoubleArray(maxSize, 1);
    }

    public static List<Double> geDoubleList(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        List<Double> doubles = new ArrayList<Double>();
        for (int i = 0; i < size; i++) {
            doubles.add(Double.valueOf(RandomUtils.nextDouble() * maxValue));
        }
        return doubles;
    }

    public static Double[] geDoubleArray(int maxSize, int maxValue) {
        int size = RandomUtils.nextInt(maxSize);
        Double[] doubles = new Double[size];
        for (int i = 0; i < size; i++) {
            doubles[i] = Double.valueOf(RandomUtils.nextDouble() * maxValue);
        }
        return doubles;
    }

}
