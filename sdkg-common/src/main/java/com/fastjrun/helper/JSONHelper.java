package com.fastjrun.helper;

import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JSONHelper {
    /**
     * 将一个对象直接转换为一个JSONObject对象，
     * 同样适合于JSON格式的字符串
     * 但是如果存在java.sql.Date或者java.sql.Timestamp时间格式，调用例外一个toJson转换方法
     *
     * @param obj
     *
     * @return
     */
    public static JSONObject toJson(Object obj) {
        return JSONObject.fromObject(obj);
    }

    /**
     * @param obj        需要转换的参数
     * @param processors 类型转换器的集合,参数是一个Map集合，键代表需要转换类型的全路径，值是类型转换器
     *
     * @return
     *
     * @throws ClassNotFoundException
     */
    public static JSONObject toJson(Object obj, Map<String, JsonValueProcessor> processors)
            throws ClassNotFoundException {
        //定义一个JSONConfig对象，该对象可以制定一个转换规则  
        JsonConfig config = new JsonConfig();
        if (processors != null && !processors.isEmpty()) {
            Iterator<java.util.Map.Entry<String, JsonValueProcessor>> it = processors.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<java.lang.String, net.sf.json.processors.JsonValueProcessor> entry =
                        (Map.Entry<java.lang.String, net.sf.json.processors.JsonValueProcessor>) it
                                .next();
                String key = entry.getKey();
                JsonValueProcessor processor = processors.get(key);
                //反射获取到需要转换的类型  
                Class<?> cls = Class.forName(key);
                config.registerJsonValueProcessor(cls, processor);
            }
        }
        return JSONObject.fromObject(obj, config);
    }
}  