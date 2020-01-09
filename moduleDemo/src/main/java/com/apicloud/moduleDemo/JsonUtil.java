package com.apicloud.moduleDemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.text.MessageFormat;
import java.util.*;


public class JsonUtil {
    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
    }

    private static final SerializerFeature[] features = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
    };


    public static String convertObjectToJSON(Object object) {
        return JSON.toJSONString(object, config, features);
    }

    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, config);
    }



    public static Object toBean(String text) {
        return JSON.parse(text);
    }

    public static <T> T toBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     *  转换为数组
     * @param text
     * @return
     */
    public static <T> Object[] toArray(String text) {
        return toArray(text, null);
    }

    /**
     *  转换为数组
     * @param text
     * @param clazz
     * @return
     */
    public static <T> Object[] toArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz).toArray();
    }

    /**
     * 转换为List
     * @param text
     * @param clazz
     * @return
     */
    public static <T> List<T> toList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * 将string转化为序列化的json字符串
     * @param text
     * @return
     */
    public static Object textToJson(String text) {
        Object objectJson  = JSON.parse(text);
        return objectJson;
    }

    /**
     * json字符串转化为map
     * @param s
     * @return
     */
    public static <K, V> Map<K, V>  stringToCollect(String s) {
        Map<K, V> m = (Map<K, V>) JSONObject.parseObject(s);
        return m;
    }

    /**
     * 转换JSON字符串为对象
     * @param jsonData
     * @param clazz
     * @return
     */
    public static Object convertJsonToObject(String jsonData, Class<?> clazz) {
        return JSONObject.parseObject(jsonData, clazz);
    }

    /**
     * 将map转化为string
     * @param m
     * @return
     */
    public static <K, V> String collectToString(Map<K, V> m) {
        String s = JSONObject.toJSONString(m);
        return s;
    }

    /**
     * 将object对象转化为数量不等的实体
     * @param o 传入的object对象
     * @param list 需要转化的实体列表
     * @return
     */
    public static ArrayList<Object> object2Beans(Object o, Class... list) {
        String jsonString = JSON.toJSONString(o);
        ArrayList<Object> objects = new ArrayList<>();
        for (Class tclass : list) {
            Object o2 = JSON.parseObject(jsonString, tclass);
            objects.add(o2);
        }
        return objects;
    }

    public static String getKeyValue(HashMap map) {
        Set keySet = map.keySet();
        StringBuilder sb = new StringBuilder();
        Object[] array = keySet.toArray();
        Arrays.sort(array);
        for (Object key : array) {
            Object o = map.get(key + "");
            if (null == o) o = "";
            else if (o instanceof Integer) o = String.valueOf(o);
            else if (o instanceof List) {
                JSONArray jsonArray = (JSONArray) o;
                JSONArray newList = new JSONArray();
                for (Object obj : jsonArray) {
                    if (!(obj instanceof JSONObject)) break;
                    JSONObject jsonObject = (JSONObject) obj;
                    Map pramMap = JSONObject.parseObject(jsonObject.toJSONString(), TreeMap.class);
                    String s = JSON.toJSONString(pramMap, SerializerFeature.SortField);
                    newList.add(JSONObject.parseObject(s, Feature.OrderedField));
                }
                o = newList.size() == 0 ? o : newList;
            }
            sb.append(MessageFormat.format("{0}={1}&", key, o));
        }
        return sb.toString();
    }


}
