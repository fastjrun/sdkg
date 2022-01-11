/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.service.helper;


import com.fastjrun.example.dto.PageResult;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockHelper {

  public static final String NUMERICANDALPHABETIC =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

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

    return RandomUtils.nextBoolean();
  }

  public static Integer geInteger(int maxValue) {

    return RandomUtils.nextInt(maxValue);
  }

  public static Long geLong(long maxValue) {

    return RandomUtils.nextLong() % maxValue + 1;
  }

  public static Double geDouble(int maxValue) {

    return RandomUtils.nextDouble() * maxValue;
  }

  public static Float geFloat(int maxValue) {

    return RandomUtils.nextFloat() * maxValue;
  }

  public static Date geDate() {
    long nextLong = RandomUtils.nextInt(10);
    return Date.from(
        LocalDate.now().plusDays(nextLong).atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public static List<String> geStringListWithAscii(int maxSize) {
    char[] chars = geStringWithAscii(maxSize).toCharArray();
    List<String> strings = new ArrayList<>();
    for (int i = 0; i < chars.length; i++) {
      strings.add(String.valueOf(chars[i]));
    }
    return strings;
  }

  public static String[] geStringArrayWithAscii(int maxSize) {
    return geStringListWithAscii(maxSize).toArray(new String[maxSize]);
  }

  public static List<Boolean> geBooleanList(int maxSize) {
    int size = RandomUtils.nextInt(maxSize);
    List<Boolean> booleans = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      booleans.add(RandomUtils.nextBoolean());
    }
    return booleans;
  }

  public static List<Integer> geIntegerList(int maxSize) {
    return geIntegerList(maxSize, 10);
  }

  public static List<Integer> geIntegerList(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    List<Integer> integers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      integers.add(RandomUtils.nextInt(maxValue));
    }
    return integers;
  }

  public static List<Long> geLongList(int maxSize) {
    return geLongList(maxSize, 10000);
  }

  public static List<Long> geLongList(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    List<Long> longs = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      longs.add(RandomUtils.nextLong() % maxValue + 1);
    }
    return longs;
  }

  public static List<Float> geFloatList(int maxSize) {
    return geFloatList(maxSize, 1);
  }

  public static List<Float> geFloatList(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    List<Float> floats = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      floats.add(RandomUtils.nextFloat() * maxValue);
    }
    return floats;
  }

  public static List<Double> geDoubleList(int maxSize) {
    return geDoubleList(maxSize, 1);
  }

  public static List<Double> geDoubleList(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    List<Double> doubles = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      doubles.add(RandomUtils.nextDouble() * maxValue);
    }
    return doubles;
  }

  public static List<Date> geDateList(int maxSize) {
    int size = RandomUtils.nextInt(maxSize);
    List<Date> dates = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      dates.add(geDate());
    }
    return dates;
  }

  public static PageResult<Boolean> geBooleanPage(int maxSize) {
    int size = RandomUtils.nextInt(maxSize);
    PageResult<Boolean> pageResult = new PageResult<>();
    List<Boolean> booleans = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      booleans.add(RandomUtils.nextBoolean());
    }
    pageResult.setRows(booleans);
    pageResult.setCurrPage(RandomUtils.nextInt());
    pageResult.setPageSize(RandomUtils.nextInt());
    pageResult.setTotalPage(RandomUtils.nextInt());
    pageResult.setTotal(RandomUtils.nextInt());
    return pageResult;
  }

  public static PageResult<Integer> geIntegerPage(int maxSize) {
    return geIntegerPage(maxSize, 10);
  }

  public static PageResult<Integer> geIntegerPage(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    PageResult<Integer> pageResult = new PageResult<>();
    List<Integer> integers = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      integers.add(RandomUtils.nextInt(maxValue));
    }

    pageResult.setRows(integers);
    pageResult.setCurrPage(RandomUtils.nextInt());
    pageResult.setPageSize(RandomUtils.nextInt());
    pageResult.setTotalPage(RandomUtils.nextInt());
    pageResult.setTotal(RandomUtils.nextInt());
    return pageResult;
  }

  public static PageResult<Long> geLongPage(int maxSize) {
    return geLongPage(maxSize, 10000);
  }

  public static PageResult<Long> geLongPage(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    PageResult<Long> pageResult = new PageResult<>();
    List<Long> longs = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      longs.add(RandomUtils.nextLong() % maxValue + 1);
    }

    pageResult.setRows(longs);
    pageResult.setCurrPage(RandomUtils.nextInt());
    pageResult.setPageSize(RandomUtils.nextInt());
    pageResult.setTotalPage(RandomUtils.nextInt());
    pageResult.setTotal(RandomUtils.nextInt());
    return pageResult;
  }

  public static PageResult<Float> geFloatPage(int maxSize) {
    return geFloatPage(maxSize, 1);
  }

  public static PageResult<Float> geFloatPage(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    PageResult<Float> pageResult = new PageResult<>();
    List<Float> floats = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      floats.add(RandomUtils.nextFloat() * maxValue);
    }

    pageResult.setRows(floats);
    pageResult.setCurrPage(RandomUtils.nextInt());
    pageResult.setPageSize(RandomUtils.nextInt());
    pageResult.setTotalPage(RandomUtils.nextInt());
    pageResult.setTotal(RandomUtils.nextInt());
    return pageResult;
  }

  public static PageResult<Double> geDoublePage(int maxSize) {
    return geDoublePage(maxSize, 1);
  }

  public static PageResult<Double> geDoublePage(int maxSize, int maxValue) {
    int size = RandomUtils.nextInt(maxSize);
    PageResult<Double> pageResult = new PageResult<>();
    List<Double> doubles = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      doubles.add(RandomUtils.nextDouble() * maxValue);
    }

    pageResult.setRows(doubles);
    pageResult.setCurrPage(RandomUtils.nextInt());
    pageResult.setPageSize(RandomUtils.nextInt());
    pageResult.setTotalPage(RandomUtils.nextInt());
    pageResult.setTotal(RandomUtils.nextInt());
    return pageResult;
  }

  public static PageResult<Date> geDatePage(int maxSize) {
    int size = RandomUtils.nextInt(maxSize);
    PageResult<Date> pageResult = new PageResult<>();
    List<Date> dates = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      dates.add(geDate());
    }
    pageResult.setRows(dates);
    pageResult.setCurrPage(RandomUtils.nextInt());
    pageResult.setPageSize(RandomUtils.nextInt());
    pageResult.setTotalPage(RandomUtils.nextInt());
    pageResult.setTotal(RandomUtils.nextInt());
    return pageResult;
  }
}
