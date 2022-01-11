/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.service.helper;

import java.util.Collection;
import java.util.Map;

public class Check {

    public static boolean isEmpty(@SuppressWarnings("rawtypes") Collection target) {
        if (null == target || target.isEmpty()) {
            return true;
        }
        return false;

    }

    public static boolean isEmpty(String target) {
        if (null == target || "".equals(target)) {
            return true;
        }
        return false;

    }

    public static boolean isEmpty(@SuppressWarnings("rawtypes") Map target) {
        if (null == target || target.isEmpty()) {
            return true;
        }
        return false;

    }

    public static boolean check(Object target) {
        if (null == target) {
            return false;
        }
        return true;
    }

    public static boolean isNotNull(Object target) {
        if (null == target) {
            return false;
        }
        return true;
    }
}
