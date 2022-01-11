/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.util;

import com.beust.jcommander.internal.Lists;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestUtils {

  public static Map<String, Object> initParam(String yamlFileInClassPath) throws IOException {
    Yaml yaml = new Yaml();
    InputStream inParam = TestUtils.class.getResourceAsStream(yamlFileInClassPath);
    Map<String, Object> map = yaml.loadAs(inParam, Map.class);

    inParam.close();
    return map;

  }

  public static Object[][] loadParam(
      Map<String, Object> propParams, String className, Method method) {
    Set<String> keys = propParams.keySet();
    List<Object> parameters = Lists.newArrayList();
    for (String key : keys) {
      if (key.equals(className.concat(".").concat(method.getName()))) {
        parameters = (List<Object>) propParams.get(key);
      }
    }
    Object[][] object = new Object[parameters.size()][];
    for (int i = 0; (i < object.length); i++) {
      object[i] = new Object[] {parameters.get(i)};
    }
    return object;
  }

  /** 使用reflect进行转换 getDeclaredFields()会获取所有属性，需要过滤不需要的类型如 static final 不会获取父类的属性，需要自己编码实现 */
  public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {

    if (map == null || map.size() <= 0) return null;

    Object obj = beanClass.newInstance();

    // 获取关联的所有类，本类以及所有父类
    boolean ret = true;
    Class oo = obj.getClass();
    List<Class> clazzs = new ArrayList<Class>();
    while (ret) {
      clazzs.add(oo);
      oo = oo.getSuperclass();
      if (oo == null || oo == Object.class) break;
    }

    for (int i = 0; i < clazzs.size(); i++) {
      Field[] fields = clazzs.get(i).getDeclaredFields();
      for (Field field : fields) {
        int mod = field.getModifiers();
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
          continue;
        }
        // 由字符串转换回对象对应的类型
        if (field != null) {
          field.setAccessible(true);
          field.set(obj, map.get(field.getName()));
        }
      }
    }
    return obj;
  }
}
